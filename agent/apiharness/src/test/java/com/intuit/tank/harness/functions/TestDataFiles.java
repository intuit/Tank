package com.intuit.tank.harness.functions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import com.intuit.tank.harness.APITestHarness;
import com.intuit.tank.vm.common.TankConstants;

/**
 * Creates data files for the CSV and file functions in a fresh directory under the agent data file storage directory,
 * which is where {@link CSVReader} and {@link JexlIOFunctions} resolve file names from. Each instance uses its own
 * directory so concurrent test JVMs never share a file.
 */
final class TestDataFiles implements AutoCloseable {

    private final Path dir;

    TestDataFiles() throws IOException {
        Path dataFileDir = Path.of(APITestHarness.getInstance().getTankConfig().getAgentConfig()
                .getAgentDataFileStorageDir());
        Files.createDirectories(dataFileDir);
        dir = Files.createTempDirectory(dataFileDir, "tank-functions-test");
    }

    /**
     * Writes the file and returns its name relative to the data file storage directory.
     */
    String write(String name, String content) throws IOException {
        Files.writeString(dir.resolve(name), content);
        return relativeName(name);
    }

    /**
     * Creates a sub directory and returns its name relative to the data file storage directory.
     */
    String mkdir(String name) throws IOException {
        Files.createDirectory(dir.resolve(name));
        return relativeName(name);
    }

    Path path(String name) {
        return dir.resolve(name);
    }

    String relativeName(String name) {
        return dir.getFileName() + "/" + name;
    }

    /**
     * Registers the reader of the given file as the reader of the default csv file (csv-data.txt), so the functions
     * that read the default file can be tested without writing a shared file into the data file storage directory.
     * {@link CSVReader#reset()} removes it again.
     */
    @SuppressWarnings("unchecked")
    static void useAsDefaultCsv(String fileName) throws IllegalAccessException {
        Map<String, CSVReader> readers = (Map<String, CSVReader>) FieldUtils.readStaticField(CSVReader.class,
                "instance", true);
        readers.put(TankConstants.DEFAULT_CSV_FILE_NAME, CSVReader.getInstance(fileName));
    }

    @Override
    public void close() throws IOException {
        FileUtils.deleteDirectory(dir.toFile());
    }
}
