package com.intuit.tank.transform.scriptGenerator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.conversation.WebSocketMessage;
import com.intuit.tank.conversation.WebSocketTransaction;
import com.intuit.tank.project.RequestData;
import com.intuit.tank.project.ScriptStep;

/**
 * Converts proxy-recorded {@link WebSocketTransaction} objects into
 * {@link ScriptStep} objects that can be imported into the Tank script
 * editor and replayed by the agent's WebSocketRunner.
 *
 * Each transaction produces:
 * <ol>
 *   <li>A CONNECT step</li>
 *   <li>One SEND step per client-to-server message</li>
 *   <li>A DISCONNECT step</li>
 * </ol>
 *
 * Server-to-client messages are not converted (they are responses,
 * not actions to replay).
 */
public class WebSocketTransactionConverter {

    private static final Logger LOG = LogManager.getLogger(WebSocketTransactionConverter.class);

    private static final String WS_TYPE = "websocket";
    private static final String KEY_ACTION = "ws-action";
    private static final String KEY_URL = "ws-url";
    private static final String KEY_CONNECTION_ID = "ws-connection-id";
    private static final String KEY_PAYLOAD = "ws-payload";

    private WebSocketTransactionConverter() {
        // utility class
    }

    /**
     * Convert a single WebSocketTransaction into a list of ScriptSteps.
     *
     * @param transaction the recorded WS transaction
     * @param startIndex  the step index for the first generated step
     * @return ordered list of ScriptSteps (CONNECT, SEND..., DISCONNECT)
     */
    public static List<ScriptStep> convert(WebSocketTransaction transaction, int startIndex) {
        List<ScriptStep> steps = new ArrayList<>();
        String url = transaction.getUrl();
        String connectionId = transaction.generateConnectionId();
        int index = startIndex;

        // 1. CONNECT step
        steps.add(createStep("CONNECT", url, connectionId, null, index++));

        // 2. SEND steps for each client message
        for (WebSocketMessage msg : transaction.getClientMessages()) {
            String payload = msg.getContentAsString();
            steps.add(createStep("SEND", url, connectionId, payload, index++));
        }

        // 3. DISCONNECT step
        steps.add(createStep("DISCONNECT", url, connectionId, null, index));

        LOG.debug("Converted WebSocketTransaction [{}] to {} ScriptSteps", url, steps.size());
        return steps;
    }

    /**
     * Convert multiple WebSocketTransactions into a flat list of ScriptSteps
     * with continuous step indexing.
     *
     * @param transactions the recorded WS transactions
     * @param startIndex   the step index for the first generated step
     * @return ordered list of ScriptSteps across all transactions
     */
    public static List<ScriptStep> convertAll(List<WebSocketTransaction> transactions, int startIndex) {
        List<ScriptStep> allSteps = new ArrayList<>();
        int currentIndex = startIndex;
        for (WebSocketTransaction tx : transactions) {
            List<ScriptStep> txSteps = convert(tx, currentIndex);
            allSteps.addAll(txSteps);
            currentIndex += txSteps.size();
        }
        return allSteps;
    }

    private static ScriptStep createStep(String action, String url, String connectionId,
                                          String payload, int stepIndex) {
        ScriptStep step = new ScriptStep();
        step.setType(WS_TYPE);
        step.setStepIndex(stepIndex);
        step.setName("WS " + action + " " + url);
        step.setScriptGroupName("WebSocket " + connectionId);

        Set<RequestData> data = new HashSet<>();
        data.add(new RequestData(KEY_ACTION, action, WS_TYPE));
        data.add(new RequestData(KEY_URL, url, WS_TYPE));
        data.add(new RequestData(KEY_CONNECTION_ID, connectionId, WS_TYPE));

        if (payload != null) {
            data.add(new RequestData(KEY_PAYLOAD, payload, WS_TYPE));
        }

        step.setData(data);
        return step;
    }
}
