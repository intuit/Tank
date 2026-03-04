# CLAUDE.md

## Git Workflow

- **Never commit or push on the user's behalf.** Always present the commands for the user to run.
- When changes are ready, provide concise `git add`, `git commit`, and `git push` commands.

## Project Overview

Tank is Intuit's open-source distributed load testing platform (Controller-Agent model) for HTTP and WebSocket protocols.
The active feature branch `zkofiro/websocket-support` adds first-class WebSocket support across all layers: proxy recording, JAXB data model, agent execution, script editor, and debugger.

## Guiding Principles

These are non-negotiable technical requirements derived from the WebSocket load testing white paper:

- **Session-based model**: Fire-and-forget sends, background message collection, declarative assertions. NOT event-driven callbacks.
- **Non-blocking I/O**: Netty-based `TankWebSocketClient` using shared `NioEventLoopGroup`. Each agent must handle 50,000+ concurrent WS connections.
- **Message processing overhead**: < 1ms per message under load.
- **Persistent connections**: WS connections survive across test steps within a user session. Managed in `TestPlanRunner.webSocketClients` (`ConcurrentHashMap<String, TankWebSocketClient>`).
- **Connection lifecycle**: CONNECT → SEND/ASSERT (repeatable) → DISCONNECT. Four actions only: `WebSocketAction.CONNECT`, `SEND`, `ASSERT`, `DISCONNECT`.
- **Fail-on patterns**: Background regex matchers registered on `MessageStream` that trigger immediate test failure on match.
- **Assertions**: Count-based (min/max expected messages), content-based (regex expect), and extraction-based (save first/last match to variable).
- **Variable substitution**: All fields (URL, payload, connectionId, assertion patterns) must resolve `#{variables}` before use.
- **Proxy transparency**: WebSocket relay must preserve frame masking, handle fragmentation, and not alter payloads.

## Coding Standards

### Language and Runtime
- Java 17 (`maven.compiler.release=17`), CI runs on Corretto 25
- Lombok for boilerplate reduction (annotation processor in parent POM)
- JAXB for XML serialization of test scripts and recorded sessions

### Logging
- Log4j 2 (`org.apache.logging.log4j`), version 2.25.3
- Logger declaration: `private static final Logger LOG = LogManager.getLogger(ClassName.class);`
- Use `LogUtil.getLogMessage(...)` for structured agent log messages

### Package Structure
- Base package: `com.intuit.tank`
- OWASP proxy fork: `org.owasp.proxy` (separate root — do not rename)
- Packages follow module boundaries:
  - `com.intuit.tank.runner.method` — step runners (apiharness)
  - `com.intuit.tank.harness.data` — JAXB data model (harness_data)
  - `com.intuit.tank.httpclientjdk` — Netty WS client (http_client_jdk)
  - `com.intuit.tank.handler` — proxy-side WS handling (proxy-extension)
  - `com.intuit.tank.conversation` — recorded session model (WebConversation)
  - `com.intuit.tank.transform.scriptGenerator` — script conversion (tank_common)

### Threading and Concurrency
- `AtomicBoolean` / `AtomicReference` for state flags and channel references
- `volatile` for fields read across Netty event loop and runner threads
- `CopyOnWriteArrayList` for collections written by relay/event threads and read by runner threads
- `CompletableFuture` for async connect/send operations
- `ConcurrentHashMap` for shared connection registries

### Exception Handling
- Custom unchecked exceptions extend `RuntimeException` (e.g., `WebSocketException`, `KillScriptException`)
- Async errors routed via `CompletableFuture.completeExceptionally()`
- Runner boundary catches `Exception` broadly and returns `TankConstants.HTTP_CASE_FAIL` or `HTTP_CASE_PASS`

### Testing
- JUnit 5 (Jupiter) with `@DisplayName` annotations
- Mockito 5.20 (`mockito-junit-jupiter`)
- JUnit 5 built-in assertions (`assertEquals`, `assertTrue`, `assertThrows`) — no AssertJ/Hamcrest
- WireMock 3.13 for HTTP/WS mock servers in `http_client_jdk`
- Embedded test servers for integration tests (e.g., `EmbeddedWebSocketTestServer`)
- Test exclusion groups in surefire: `manual`, `integration`, `experimental`
- JAXB model classes require registration in `jaxb.index` file

### JAXB Conventions
- All new XML-serializable WS classes must be added to `harness_data/.../jaxb.index`
- Use `@XmlType(propOrder = {...})` to enforce field ordering
- `@XmlRootElement` required on top-level elements
- `Map<K,V>` fields require `@XmlJavaTypeAdapter` — JAXB cannot marshal raw maps

## TDD Protocol

**Mandatory for all feature development. No exceptions.**

Every code change follows Red-Green-Refactor strictly. No implementation code is written until a failing test exists.

### Red Phase (test first)
1. Write a test that describes the expected behavior.
2. Run the test. It **must fail**. If it passes, the test is wrong — it's not testing new behavior.
3. Confirm the failure message is meaningful (asserts the right thing, not a compile error or unrelated NPE).

```bash
# Run the new test in isolation to confirm RED
mvn test -pl <module> -Dtest=<TestClassName>#<methodName>
```

### Green Phase (minimum implementation)
1. Write the **minimum** code to make the failing test pass. No extras, no "while I'm here" additions.
2. Run the test again. It **must pass**.
3. Run the full test suite for the module to confirm no regressions.

```bash
# Confirm GREEN on the target test
mvn test -pl <module> -Dtest=<TestClassName>#<methodName>

# Confirm no regressions in the module
mvn test -pl <module>
```

### Refactor Phase (clean up, stay green)
1. Restructure, rename, deduplicate — only if needed. Do not refactor for its own sake.
2. After every change, re-run the full module test suite. Tests **must stay green**.
3. No new functionality in this phase. If you see something new to add, start a new Red phase.

```bash
# Confirm still GREEN after refactor
mvn test -pl <module>
```

### Enforcement Rules
- **Claude must announce the TDD phase** before every task: "Starting RED phase for [description]."
- If an implementation file is modified before a failing test exists for that change, stop and write the test first.
- Each commit should be phase-labeled: `[RED] Add test for ...`, `[GREEN] Implement ...`, `[REFACTOR] Clean up ...`.
- For bug fixes: the Red phase test must reproduce the bug. The Green phase fix must make it pass.
- For JAXB model changes: Red phase is a round-trip serialization test (marshal → unmarshal → assertEquals).
- For proxy changes: Red phase uses embedded socket/server test fixtures, not mocks of I/O.

## Manual Validation

**Required before any commit, merge, or deployment. No exceptions. Test often!**

After automated tests pass, Claude must propose a manual validation plan and the user must execute it before proceeding. Err on the side of more validation, not less.

### When to validate
- After completing any feature or fix (before committing)
- After merging branches
- Before any deployment or PR creation
- Whenever code touches I/O, networking, serialization, or UI

### What to validate
- **Proxy recording**: Launch the proxy (`java -jar proxy-parent/proxy_pkg/target/Tank-Proxy-pkg-all.jar`), record real traffic through a browser, verify WS sessions appear in the table
- **Save/load round-trip**: Record a session with HTTP + WS traffic, save it, close the proxy, reopen and load the file — verify all data survives
- **Script import**: Load a saved recording into the web UI script editor, verify WS steps appear with correct action/URL/payload
- **Agent debugger**: Open a script with WS steps in the debugger (`java -jar tools/agent_debugger/target/Tank-Debugger-all.jar`), step through, verify WS actions execute
- **Edge cases**: Binary messages, fragmented frames, large payloads, reserved opcodes, connection drops

### Validation protocol
1. Claude proposes a numbered checklist of manual validation steps specific to the changes made
2. User executes the steps and reports results
3. If any step fails, fix the issue and re-validate from step 1
4. Only after all steps pass does the user commit/merge/deploy

## Verification Commands

```bash
# --- Unit Tests ---

# Run all tests in a module
mvn test -pl agent/apiharness
mvn test -pl agent/http_client_jdk
mvn test -pl harness_data
mvn test -pl proxy-parent/proxy-extension
mvn test -pl proxy-parent/WebConversation
mvn test -pl proxy-parent/owasp-proxy -Dowasp.proxy.skipTests=false
mvn test -pl tank_common

# Run a single test class
mvn test -pl <module> -Dtest=<TestClassName>

# Run a single test method
mvn test -pl <module> -Dtest=<TestClassName>#<methodName>

# --- Coverage ---

# Module-level coverage (JaCoCo)
mvn test -P coverage -pl <module>
# Report at: <module>/target/site/jacoco/index.html

# Full project coverage
mvn clean install -P coverage
# Aggregated report at: jacoco-report-aggregator/target/site/jacoco-aggregate/index.html

# --- Static Analysis ---

# FindBugs + Checkstyle
mvn clean install -P static-analysis

# --- Integration Tests ---

# Requires the integration profile
mvn verify -P integration -pl <module>

# --- CI Build (full pipeline) ---
mvn clean install surefire-report:report -P release
# Surefire reports at: <module>/target/surefire-reports/
```

## Feature Implementation Roadmap

### Current branch status (zkofiro/websocket-support)
- **Agent execution layer**: ~90% complete (WebSocketRunner, TankWebSocketClient, MessageStream)
- **JAXB data model**: Complete (WebSocketStep, WebSocketRequest, WebSocketAction, WebSocketAssertion, AssertionBlock, FailOnPattern, SaveOccurrence)
- **Proxy recording**: ~85% complete (frame codec, session tracking, relay working)
- **Script editor UI**: Complete (WebSocketStepEditor)
- **Debugger**: Complete (DebugStep snapshots, MessageStreamDialog, RequestResponsePanel WS mode)
- **Recording → Replay pipeline**: BROKEN — critical gaps below

### Immediate priorities (record and replay production traffic)

#### 1. Commit unstaged proxy fix (socket lifecycle)
- `HttpProxyConnectionHandler`: handler-chain unwrapping via reflection, `releaseSocket()` after WS handoff
- `owasp-proxy/pom.xml`: make tests opt-in via `${owasp.proxy.skipTests}` property
- New test: `HttpProxyConnectionHandlerWebSocketTest`

#### 2. Fix save/load round-trip for WS recordings
- `Session.java`: Add `List<WebSocketTransaction>` field with `@XmlElement`
- `ProxyApp.save()`: Include WS sessions in serialized output
- `WebConversationJaxbParseXML.parse()`: Handle `WebSocketTransaction` instances on load
- Fix `WebSocketTransaction.handshakeHeaders`: Add `@XmlJavaTypeAdapter` for `Map<String,String>`

#### 3. Build proxy recording → ScriptStep converter
- Create converter that maps `WebSocketTransaction` → Tank `ScriptStep` objects
- Map recorded messages to `ws-action`, `ws-url`, `ws-connection-id`, `ws-payload` `RequestData` entries
- Wire into the existing script import path in the web UI

#### 4. Proxy hardening
- `WebSocketFrameCodec.readFrame()`: Catch `IllegalArgumentException` for reserved opcodes in relay loop
- `WebSocketSession`: Replace `StringBuilder` fragment buffers with `ByteArrayOutputStream` for binary frame support
- `WebSocketRelay`: Add SO_TIMEOUT on relay sockets to prevent infinite blocking
- `WebSocketSession.messageCallback`: Make `volatile`

### Follow-up priorities (production readiness)

#### 5. Agent-side hardening
- `WebSocketRunner.executeSend()`: Add timeout on `CompletableFuture.get()` call
- `TestPlanRunner`: Add `finally` block to disconnect all WS clients on runner shutdown/abort
- `TestPlanRunner`: Null-check `onFail` before calling `equalsIgnoreCase()` (NPE risk)
- `TankWebSocketClient`: Use shared `EventLoopGroup` injected from runner instead of standalone per-client
- `MessageStream`: Add configurable cap on message buffer size

#### 6. Binary frame support
- `TankWebSocketClient`: Handle `BinaryWebSocketFrame` (currently a TODO, silently dropped)
- Add binary payload representation to `WebSocketRequest` in harness_data
- Update `ConverterUtil` to handle binary payloads

#### 7. Cleanup dead code
- Remove unused `WebSocketProxyHandler` interface
- Decide: either wire `WebSocketRequest`/`WebSocketResponse` (agent_common) into `WebSocketRunner` or delete them

## Common Commands

```bash
# Full build (all modules, unit tests)
mvn clean install

# Build single module (with dependencies)
mvn clean install -pl agent/http_client_jdk -am
mvn clean install -pl proxy-parent/proxy-extension -am
mvn clean install -pl agent/apiharness -am

# Skip tests
mvn clean install -DskipTests

# Run tests for a specific module
mvn test -pl agent/apiharness
mvn test -pl agent/http_client_jdk
mvn test -pl harness_data
mvn test -pl proxy-parent/proxy-extension
mvn test -pl tank_common

# Run a single test class
mvn test -pl agent/apiharness -Dtest=WebSocketRunnerTest
mvn test -pl agent/http_client_jdk -Dtest=TankWebSocketClientTest

# Run owasp-proxy tests (normally skipped)
mvn test -pl proxy-parent/owasp-proxy -Dowasp.proxy.skipTests=false

# Code coverage
mvn clean install -P coverage

# CI build (what CodeBuild runs)
mvn clean install surefire-report:report -P release

# Integration tests
mvn verify -P integration

# Run the proxy GUI (for recording)
# Build proxy-parent first, then run the proxy-extension jar
mvn clean install -pl proxy-parent -am -DskipTests
java -jar proxy-parent/proxy_pkg/target/Tank-Proxy-pkg-all.jar

# Run the agent debugger
java -jar tools/agent_debugger/target/Tank-Debugger-all.jar
```

### Key file locations
```
harness_data/src/main/java/com/intuit/tank/harness/data/     # JAXB WS model
agent/http_client_jdk/src/main/java/com/intuit/tank/httpclientjdk/  # Netty WS client
agent/apiharness/src/main/java/com/intuit/tank/runner/method/       # WebSocketRunner
proxy-parent/proxy-extension/src/main/java/com/intuit/tank/handler/ # Proxy WS recording
proxy-parent/owasp-proxy/src/main/java/org/owasp/proxy/http/       # OWASP proxy fork
proxy-parent/WebConversation/src/main/java/com/intuit/tank/conversation/  # Recorded session model
tank_common/src/main/java/com/intuit/tank/transform/scriptGenerator/     # ConverterUtil
tools/agent_debugger/src/main/java/com/intuit/tank/tools/debugger/       # Debugger UI
web/web_ui/src/main/java/com/intuit/tank/script/                        # Script editor UI
```
