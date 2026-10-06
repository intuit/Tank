package com.intuit.tank.integration;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.time.Duration;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

/**
 * Boots the reactor-built Tank WAR on embedded Tomcat with disposable H2 storage.
 */
public final class TankIntegrationEnvironment implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(TankIntegrationEnvironment.class);

    private Tomcat tomcat;
    private Path workDir;
    private Path tomcatLogPath;
    private String baseUrl;
    private String jdbcUrl;
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .build();

    public static Path installSettingsRoot() throws IOException {
        Path settingsDir = Path.of("target", "system-test-runtime", "settings").toAbsolutePath();
        Files.createDirectories(settingsDir);
        Path dataRoot = settingsDir.getParent().resolve("storage");
        Files.createDirectories(dataRoot.resolve("datafiles"));
        Files.createDirectories(dataRoot.resolve("timing"));
        Files.createDirectories(dataRoot.resolve("tmpfiles"));
        Files.createDirectories(dataRoot.resolve("jars"));
        String settings = loadResource("runtime/settings.xml")
                .replace("${datafiles}", dataRoot.resolve("datafiles").toString())
                .replace("${timing}", dataRoot.resolve("timing").toString())
                .replace("${tmpfiles}", dataRoot.resolve("tmpfiles").toString())
                .replace("${jars}", dataRoot.resolve("jars").toString());
        Files.writeString(settingsDir.resolve("settings.xml"), settings);
        System.setProperty("WATS_PROPERTIES", settingsDir.toString());
        return settingsDir;
    }

    public static TankIntegrationEnvironment start() {
        TankIntegrationEnvironment env = new TankIntegrationEnvironment();
        env.boot();
        return env;
    }

    private void boot() {
        try {
            workDir = Path.of("target", "system-test-runtime").toAbsolutePath();
            Files.createDirectories(workDir);
            tomcatLogPath = workDir.resolve("embedded-tomcat.log");

            String databaseName = "tank_" + UUID.randomUUID().toString().replace("-", "");
            jdbcUrl = "jdbc:h2:mem:" + databaseName
                    + ";MODE=MYSQL;NON_KEYWORDS=USER,END,VALUE;DB_CLOSE_DELAY=-1";

            Path warFile = resolveWarFile();
            Path webappDir = resolveExplodedWar(warFile);
            Path settingsDir = Path.of(System.getProperty("WATS_PROPERTIES"));

            System.setProperty("java.util.logging.config.file", "");
            System.setProperty("org.apache.jasper.compiler.disablejsr199", "true");

            int port = findFreePort();
            baseUrl = "http://127.0.0.1:" + port + "/tank";
            rewriteControllerBaseUrl(settingsDir, baseUrl);

            tomcat = new Tomcat();
            tomcat.setBaseDir(workDir.resolve("tomcat-base").toString());
            tomcat.setPort(port);
            tomcat.getConnector();

            System.out.println("[system-test] Mounting webapp from " + webappDir);
            Context context = tomcat.addWebapp("/tank", webappDir.toAbsolutePath().toString());
            context.setReloadable(false);
            context.addParameter("com.sun.faces.enableWebSocketEndpoint", "false");

            writeContextXml(webappDir);
            writePersistenceOverride(webappDir);
            writeWebSocketLib(webappDir);

            System.out.println("[system-test] Starting Tomcat (Spring/Weld/Hibernate — often 2-8 min on first boot)...");
            long bootStart = System.currentTimeMillis();
            tomcat.start();
            System.out.println("[system-test] Tomcat process up in "
                    + ((System.currentTimeMillis() - bootStart) / 1000) + "s; waiting for /v2/jobs/ping...");
            waitForReady();
            waitForSchema();
            LOG.info("Embedded Tank ready at {} (jdbc={})", baseUrl, jdbcUrl);
        } catch (Exception e) {
            try {
                close();
            } catch (RuntimeException cleanupFailure) {
                e.addSuppressed(cleanupFailure);
            }
            throw new IllegalStateException("Failed to start embedded Tank runtime", e);
        }
    }

    private Path resolveExplodedWar(Path warFile) throws IOException, InterruptedException {
        String cacheKey = warCacheKey(warFile);
        Path globalCache = Path.of(System.getProperty("user.home"), ".cache", "tank-system-tests",
                "exploded-war", cacheKey);
        Path workExploded = workDir.resolve("exploded-war");
        Path workStamp = workDir.resolve("exploded-war.stamp");

        if (!isValidExplodedWar(globalCache)) {
            System.out.println("[system-test] Exploding tank.war to global cache (~290MB, one-time per WAR revision)...");
            deleteRecursively(globalCache);
            Files.createDirectories(globalCache);
            unpackWar(warFile, globalCache);
        } else {
            System.out.println("[system-test] Reusing global exploded WAR cache at " + globalCache);
        }

        if (isValidExplodedWar(workExploded) && Files.isRegularFile(workStamp)
                && cacheKey.equals(Files.readString(workStamp).trim())) {
            System.out.println("[system-test] Reusing workdir exploded WAR at " + workExploded);
            return workExploded;
        }

        System.out.println("[system-test] Copying exploded WAR from cache " + globalCache);
        deleteRecursively(workExploded);
        cloneDirectory(globalCache, workExploded);
        Files.writeString(workStamp, cacheKey);
        return workExploded;
    }

    private static String warCacheKey(Path warFile) throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            try (InputStream input = Files.newInputStream(warFile)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = input.read(buffer)) != -1) {
                    digest.update(buffer, 0, bytesRead);
                }
            }
            return HexFormat.of().formatHex(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is unavailable", e);
        }
    }

    private static void cloneDirectory(Path source, Path destination) throws IOException, InterruptedException {
        if (isMacOs()) {
            Files.createDirectories(destination.getParent());
            Process copy = new ProcessBuilder("cp", "-Rc", source.toString(), destination.toString())
                    .redirectErrorStream(true)
                    .start();
            if (copy.waitFor() == 0 && isValidExplodedWar(destination)) {
                return;
            }
            deleteRecursively(destination);
        }
        copyDirectory(source, destination);
    }

    private static boolean isMacOs() {
        return System.getProperty("os.name", "").toLowerCase().contains("mac");
    }

    private static boolean isValidExplodedWar(Path dir) {
        return Files.isDirectory(dir) && Files.isDirectory(dir.resolve("WEB-INF"));
    }

    private static void unpackWar(Path warFile, Path destination) throws IOException, InterruptedException {
        Files.createDirectories(destination);
        Process unpack = new ProcessBuilder("jar", "xf", warFile.toAbsolutePath().toString())
                .directory(destination.toFile())
                .redirectErrorStream(true)
                .start();
        int exit = unpack.waitFor();
        if (exit != 0) {
            throw new IOException("jar xf failed with exit code " + exit);
        }
        System.out.println("[system-test] WAR exploded to " + destination);
    }

    private static void copyDirectory(Path source, Path destination) throws IOException {
        try (var walk = Files.walk(source)) {
            walk.forEach(path -> {
                try {
                    Path target = destination.resolve(source.relativize(path));
                    if (Files.isDirectory(path)) {
                        Files.createDirectories(target);
                    } else {
                        Files.createDirectories(target.getParent());
                        Files.copy(path, target, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    }
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        } catch (UncheckedIOException e) {
            throw e.getCause();
        }
    }

    private static void deleteRecursively(Path root) throws IOException {
        if (!Files.exists(root)) {
            return;
        }
        try (var walk = Files.walk(root)) {
            walk.sorted(java.util.Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        } catch (UncheckedIOException e) {
            throw e.getCause();
        }
    }

    private Path resolveWarFile() {
        String configured = System.getProperty("tank.war.path");
        if (configured != null && !configured.isBlank()) {
            Path warPath = Path.of(configured.trim());
            if (Files.isRegularFile(warPath)) {
                return warPath;
            }
            throw new IllegalStateException("Configured tank.war.path not found: " + warPath);
        }
        for (Path candidate : List.of(
                Path.of("../web/web_ui/target/tank.war").toAbsolutePath().normalize(),
                localMavenWar())) {
            if (Files.isRegularFile(candidate)) {
                System.out.println("[system-test] Using WAR at " + candidate);
                return candidate;
            }
        }
        throw new IllegalStateException(
                "Tank WAR not found. Build web module (../web/web_ui/target/tank.war) or set tank.war.path.");
    }

    private static Path localMavenWar() {
        String version = System.getProperty("project.version", "4.3.9-SNAPSHOT");
        return Path.of(System.getProperty("user.home"), ".m2", "repository",
                "com", "intuit", "tank", "tank", version, "tank-" + version + ".war");
    }

    private void rewriteControllerBaseUrl(Path settingsDir, String controllerBaseUrl) throws IOException {
        Path settingsFile = settingsDir.resolve("settings.xml");
        String settings = Files.readString(settingsFile);
        settings = settings.replaceAll(
                "<controller-base-url>.*</controller-base-url>",
                "<controller-base-url>" + controllerBaseUrl + "</controller-base-url>");
        Files.writeString(settingsFile, settings);
    }

    private Path resolveEmbeddedWebsocketJar() {
        String configured = System.getProperty("tank.embedded.websocket.jar");
        if (configured != null && !configured.isBlank()) {
            Path jar = Path.of(configured.trim());
            if (Files.isRegularFile(jar)) {
                return jar;
            }
        }
        throw new IllegalStateException("Embedded websocket jar not found at " + configured);
    }

    private static int findFreePort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            socket.setReuseAddress(true);
            return socket.getLocalPort();
        }
    }

    private void waitForReady() {
        Duration timeout = Duration.ofMinutes(8);
        long deadline = System.currentTimeMillis() + timeout.toMillis();
        Exception lastError = null;
        int attempt = 0;
        while (System.currentTimeMillis() < deadline) {
            attempt++;
            try {
                HttpResponse<String> response = get("/v2/jobs/ping");
                if (response.statusCode() == 200 && response.body() != null
                        && response.body().toUpperCase().contains("PONG")) {
                    System.out.println("[system-test] Tank REST ready after " + attempt + " ping attempt(s)");
                    return;
                }
                lastError = new IllegalStateException("Unexpected ping response: HTTP "
                        + response.statusCode() + " body=" + response.body());
            } catch (Exception e) {
                lastError = e;
            }
            if (attempt == 1 || attempt % 15 == 0) {
                System.out.println("[system-test] Still waiting for /v2/jobs/ping (attempt " + attempt + ")...");
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Interrupted waiting for embedded Tank", ie);
            }
        }
        throw new IllegalStateException("Embedded Tank not reachable at " + baseUrl
                + "/v2/jobs/ping. Last error: "
                + (lastError == null ? "unknown" : lastError.getMessage()), lastError);
    }

    private void writeContextXml(Path webappDir) throws IOException {
        Path contextXml = webappDir.resolve("META-INF/context.xml");
        Files.createDirectories(contextXml.getParent());
        String template = loadResource("runtime/context.xml");
        Files.writeString(contextXml, template.replace("${jdbcUrl}", jdbcUrl));
        System.out.println("[system-test] Wrote H2 context.xml");
    }

    private void writePersistenceOverride(Path webappDir) throws IOException, InterruptedException {
        Path metaInf = webappDir.resolve("WEB-INF/classes/META-INF");
        Files.createDirectories(metaInf);
        String persistence = loadResource("runtime/persistence.xml").replace("${jdbcUrl}", jdbcUrl);
        Files.writeString(metaInf.resolve("persistence.xml"), persistence);
        removeConflictingPersistenceJar(webappDir);
        System.out.println("[system-test] Wrote H2 persistence.xml");
    }

    private void removeConflictingPersistenceJar(Path webappDir) throws IOException, InterruptedException {
        try (var jars = Files.list(webappDir.resolve("WEB-INF/lib"))) {
            for (Path jar : jars.filter(p -> p.getFileName().toString().startsWith("data-access-")).toList()) {
                Process list = new ProcessBuilder("jar", "tf", jar.toAbsolutePath().toString())
                        .redirectErrorStream(true)
                        .start();
                String contents = new String(list.getInputStream().readAllBytes());
                if (list.waitFor() != 0 || !contents.contains("META-INF/persistence.xml")) {
                    continue;
                }
                Process strip = new ProcessBuilder("zip", "-d", jar.toAbsolutePath().toString(),
                        "META-INF/persistence.xml")
                        .redirectErrorStream(true)
                        .start();
                if (strip.waitFor() != 0) {
                    throw new IOException("Failed to remove persistence.xml from " + jar);
                }
            }
        }
    }

    private void writeWebSocketLib(Path webappDir) throws IOException {
        Path libDir = webappDir.resolve("WEB-INF/lib");
        Files.createDirectories(libDir);
        Files.copy(resolveEmbeddedWebsocketJar(), libDir.resolve("tomcat-embed-websocket.jar"),
                java.nio.file.StandardCopyOption.REPLACE_EXISTING);
    }

    private void waitForSchema() throws Exception {
        System.out.println("[system-test] Waiting for Hibernate schema (probing /v2/scripts)...");
        long deadline = System.currentTimeMillis() + Duration.ofMinutes(3).toMillis();
        while (System.currentTimeMillis() < deadline) {
            try {
                get("/v2/scripts");
            } catch (Exception ignored) {
                // EMF may not be ready yet
            }
            try (Connection connection = DriverManager.getConnection(jdbcUrl, "sa", "")) {
                if (tableExists(connection, "USER") || tableExists(connection, "user")) {
                    System.out.println("[system-test] Hibernate schema ready");
                    return;
                }
            } catch (Exception ignored) {
                // database not ready yet
            }
            Thread.sleep(2000);
        }
        throw new IllegalStateException("Hibernate schema not created within 3 minutes");
    }

    private static boolean tableExists(Connection connection, String tableName) throws Exception {
        try (ResultSet rs = connection.getMetaData().getTables(null, null, tableName, new String[] {"TABLE"})) {
            return rs.next();
        }
    }

    public String baseUrl() {
        return baseUrl;
    }

    public String jdbcUrl() {
        return jdbcUrl;
    }

    public String jdbcUser() {
        return "sa";
    }

    public String jdbcPassword() {
        return "";
    }

    public HttpClient httpClient() {
        return httpClient;
    }

    public HttpResponse<String> get(String path) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .timeout(Duration.ofSeconds(60))
                .GET()
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> postJson(String path, String body) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .timeout(Duration.ofSeconds(60))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public void retainLogsOnFailure(Throwable failure) {
        LOG.error("System test failure against {}: {}", baseUrl, failure.getMessage());
        try {
            Path logDir = Path.of("target", "system-test-logs");
            Files.createDirectories(logDir);
            Files.writeString(logDir.resolve("failure.txt"),
                    failure.getClass().getName() + ": " + failure.getMessage());
            if (Files.isRegularFile(tomcatLogPath)) {
                Files.copy(tomcatLogPath, logDir.resolve("embedded-tomcat.log"),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception e) {
            LOG.warn("Unable to write system test log artifacts: {}", e.getMessage());
        }
    }

    @Override
    public void close() {
        if (tomcat != null) {
            try {
                tomcat.stop();
                tomcat.destroy();
            } catch (LifecycleException e) {
                LOG.warn("Error stopping embedded Tomcat: {}", e.getMessage());
            }
            tomcat = null;
        }
        LOG.info("Embedded Tank stopped");
    }

    private static String loadResource(String classpathPath) throws IOException {
        try (InputStream in = TankIntegrationEnvironment.class.getClassLoader()
                .getResourceAsStream(classpathPath)) {
            if (in == null) {
                throw new IOException("Missing classpath resource: " + classpathPath);
            }
            return new String(in.readAllBytes());
        }
    }
}
