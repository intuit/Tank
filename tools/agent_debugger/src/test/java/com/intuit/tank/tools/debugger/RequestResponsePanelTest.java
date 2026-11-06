package com.intuit.tank.tools.debugger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.JMenuItem;

import org.fife.ui.rtextarea.RTextArea;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.intuit.tank.harness.data.HDTestPlan;
import com.intuit.tank.harness.data.VariableStep;
import com.intuit.tank.http.BaseRequest;
import com.intuit.tank.http.BaseResponse;

/** Uses a mock frame, so it runs headless */
public class RequestResponsePanelTest {

    private AgentDebuggerFrame frame;
    private final List<DebugStep> steps = new ArrayList<>();
    private RequestResponsePanel panel;
    private RTextArea requestTA;
    private RTextArea responseTA;

    @BeforeEach
    public void setup() {
        frame = mock(AgentDebuggerFrame.class);
        ActionProducer actions = mock(ActionProducer.class);
        when(frame.getDebuggerActions()).thenReturn(actions);
        when(actions.getSaveReqResponseAction()).thenReturn(new AbstractAction("Save") {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });
        when(frame.getSteps()).thenReturn(steps);
        panel = new RequestResponsePanel(frame);
        panel.init();
        requestTA = textAreaUnder("Request:");
        responseTA = textAreaUnder("Response:");
    }

    @Test
    public void listensToTheFrameAndOffersToSave() {
        verify(frame).addStepChangedListener(panel);
        verify(frame).addScriptChangedListener(panel);
        assertEquals("Save", ((JMenuItem) requestTA.getPopupMenu().getComponent(0)).getText());
        assertEquals("Save", ((JMenuItem) responseTA.getPopupMenu().getComponent(0)).getText());
        assertFalse(requestTA.isEditable());
        assertFalse(responseTA.isEditable());
    }

    @Test
    public void stepChangedShowsTheRequestAndResponse() {
        panel.stepChanged(step(0, "GET /index.html", "HTTP 200 OK"));
        assertEquals("GET /index.html", requestTA.getText());
        assertEquals("HTTP 200 OK", responseTA.getText());
        assertEquals(0, requestTA.getCaretPosition());

        panel.stepChanged(null);
        assertEquals("", requestTA.getText());
        assertEquals("", responseTA.getText());
    }

    @Test
    public void enteringAndExitingAStepShowItsRequestAndResponse() {
        panel.stepEntered(step(0, "GET /a", null));
        assertEquals("GET /a", requestTA.getText());
        assertEquals("", responseTA.getText());

        panel.stepExited(step(0, "GET /a", "HTTP 404"));
        assertEquals("HTTP 404", responseTA.getText());

        // blank log messages show nothing
        panel.stepExited(step(0, " ", " "));
        assertEquals("", requestTA.getText());
        assertEquals("", responseTA.getText());
    }

    @Test
    public void aNewScriptClearsBoth() {
        panel.stepChanged(step(0, "GET /a", "HTTP 200"));
        panel.scriptChanged(new HDTestPlan());
        assertEquals("", requestTA.getText());
        assertEquals("", responseTA.getText());
    }

    @Test
    public void hasDataOnlyOnceARequestRan() {
        assertFalse(panel.hasData());
        steps.add(null);
        steps.add(new DebugStep(new VariableStep()));
        steps.add(step(2, " ", null));
        assertFalse(panel.hasData());

        steps.add(step(3, "GET /a", null));
        assertTrue(panel.hasData());
    }

    @Test
    public void writeTextWritesEveryRequestAndItsResponse() {
        steps.addAll(Arrays.asList(null, step(1, "GET /a", "HTTP 200"), new DebugStep(new VariableStep()),
                step(3, "GET /b", null)));
        StringWriter out = new StringWriter();

        panel.writeText(out);

        String info1 = steps.get(1).getStepRun().getInfo();
        String info3 = steps.get(3).getStepRun().getInfo();
        assertEquals("\n############## REQUEST 1 #############\n" + info1 + "\nGET /a\n"
                + "\n############## RESPONSE 1 #############\nHTTP 200\n"
                + "\n############## REQUEST 3 #############\n" + info3 + "\nGET /b\n", out.toString());
    }

    @Test
    public void writeTextWritesNothingWithoutSteps() {
        StringWriter out = new StringWriter();
        panel.writeText(out);
        assertEquals("", out.toString());
    }

    @Test
    public void writeErrorsAreNotThrown() {
        steps.add(step(0, "GET /a", "HTTP 200"));
        Writer failing = new Writer() {
            @Override
            public void write(char[] cbuf, int off, int len) throws IOException {
                throw new IOException("disk full");
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() {
            }
        };
        panel.writeText(failing);
        assertTrue(panel.hasData());
    }

    private static DebugStep step(int index, String requestLog, String responseLog) {
        VariableStep variableStep = new VariableStep();
        variableStep.setKey("k" + index);
        variableStep.setValue("v");
        variableStep.setStepIndex(index);
        DebugStep step = new DebugStep(variableStep);
        if (requestLog != null) {
            BaseRequest request = mock(BaseRequest.class);
            when(request.getLogMsg()).thenReturn(requestLog);
            step.setRequest(request);
        }
        if (responseLog != null) {
            BaseResponse response = mock(BaseResponse.class);
            when(response.getLogMsg()).thenReturn(responseLog);
            step.setResponse(response);
        }
        return step;
    }

    private RTextArea textAreaUnder(String title) {
        JLabel label = VariablesPanelTest.find(panel, JLabel.class, l -> title.equals(l.getText()));
        assertNotNull(label, title);
        RTextArea ta = VariablesPanelTest.find(label.getParent(), RTextArea.class, t -> true);
        assertNotNull(ta, title);
        return ta;
    }
}
