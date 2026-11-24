package com.intuit.tank.tools.debugger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.awt.BorderLayout;
import java.awt.GraphicsEnvironment;
import java.lang.reflect.Field;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;

import com.intuit.tank.harness.data.HDTestPlan;
import com.intuit.tank.harness.data.ValidationData;
import com.intuit.tank.harness.data.VariableStep;
import com.intuit.tank.runner.ErrorContainer;
import com.intuit.tank.script.RequestDataPhase;

@DisabledIfEnvironmentVariable(named = "SKIP_GUI_TEST", matches = "true")
public class InfoHeaderPanelTest {

    private static AgentDebuggerFrame frame;

    private InfoHeaderPanel panel;
    private JLabel label;
    private JLabel description;
    private JButton errorButton;

    @BeforeAll
    public static void setupFrame() throws Exception {
        // the panel's error dialog needs a real, but never shown, frame
        assumeFalse(GraphicsEnvironment.isHeadless());
        AgentDebuggerFrameTest.keepTheHarnessSettings();
        SwingUtilities.invokeAndWait(() -> frame = new AgentDebuggerFrame(false, null, null));
        // disposing the frame leaves its working dir behind
        TankConfigPin.deleteCurrentWorkingDirAtExit();
    }

    @AfterAll
    public static void disposeFrame() throws Exception {
        if (frame != null) {
            SwingUtilities.invokeAndWait(frame::dispose);
        }
    }

    @BeforeEach
    public void setup() throws Exception {
        SwingUtilities.invokeAndWait(() -> panel = new InfoHeaderPanel(frame));
        BorderLayout layout = (BorderLayout) panel.getLayout();
        JPanel top = (JPanel) layout.getLayoutComponent(BorderLayout.NORTH);
        BorderLayout topLayout = (BorderLayout) top.getLayout();
        label = (JLabel) ((JPanel) topLayout.getLayoutComponent(BorderLayout.WEST)).getComponent(0);
        description = (JLabel) layout.getLayoutComponent(BorderLayout.SOUTH);
        errorButton = (JButton) topLayout.getLayoutComponent(BorderLayout.EAST);
    }

    @Test
    public void startsWithoutAStep() {
        assertEquals("No step selected", label.getText());
        assertEquals(" ", description.getText());
        assertFalse(errorButton.isVisible());
    }

    @Test
    public void showsTheStepNumberAndInfo() {
        DebugStep step = step(2);

        panel.stepChanged(step);

        assertEquals("Step 3 of 0", label.getText());
        assertEquals("Set_Variable(k->v)", description.getText());
        assertFalse(errorButton.isVisible());
    }

    @Test
    public void theErrorButtonShowsWhenTheStepHasErrors() {
        DebugStep step = step(0);
        step.setErrors(List.of(error("body", "missing")));

        panel.stepEntered(step(0));
        assertFalse(errorButton.isVisible());
        panel.stepExited(step);
        assertTrue(errorButton.isVisible());

        panel.stepChanged(null);
        assertEquals("No step selected", label.getText());
        assertEquals(" ", description.getText());
        assertFalse(errorButton.isVisible());
    }

    @Test
    public void aNewScriptClearsTheStep() {
        DebugStep step = step(1);
        step.setErrors(List.of(error("body", "missing")));
        panel.stepChanged(step);

        panel.scriptChanged(new HDTestPlan());

        assertEquals("No step selected", label.getText());
        assertEquals(" ", description.getText());
        assertFalse(errorButton.isVisible());
    }

    @Test
    public void theErrorButtonListsTheValidationFailures() throws Exception {
        // the real dialog is modal, so a mock stands in for it
        ScriptErrorViewDialog dialog = mock(ScriptErrorViewDialog.class);
        Field dialogField = InfoHeaderPanel.class.getDeclaredField("scriptErrorViewDialog");
        dialogField.setAccessible(true);
        dialogField.set(panel, dialog);
        DebugStep step = step(0);
        step.setErrors(List.of(error("body", "missing"), error("header", "wrong value")));
        panel.stepChanged(step);

        SwingUtilities.invokeAndWait(errorButton::doClick);

        verify(dialog).setTitle("Errors in stepSet_Variable(k->v)");
        verify(dialog).setText("Post Request failure in body\n"
                + "  Raw Validation: name == ${expected}\n"
                + "  Interpreted Validation: name == bob\n"
                + "  Message: missing\n\n"
                + "Post Request failure in header\n"
                + "  Raw Validation: name == ${expected}\n"
                + "  Interpreted Validation: name == bob\n"
                + "  Message: wrong value");
        verify(dialog).setVisible(true);
    }

    private static DebugStep step(int index) {
        VariableStep variable = AgentDebuggerFrameTest.variable("k", "v");
        variable.setStepIndex(index);
        return new DebugStep(variable);
    }

    private static ErrorContainer error(String location, String reason) {
        return new ErrorContainer(location, validation("${expected}"), validation("bob"), reason);
    }

    private static ValidationData validation(String value) {
        ValidationData validation = new ValidationData();
        validation.setKey("name");
        validation.setCondition("==");
        validation.setValue(value);
        validation.setPhase(RequestDataPhase.POST_REQUEST);
        return validation;
    }
}
