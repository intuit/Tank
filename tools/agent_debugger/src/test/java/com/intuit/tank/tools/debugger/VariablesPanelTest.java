package com.intuit.tank.tools.debugger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.awt.Component;
import java.awt.Container;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JTable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.intuit.tank.harness.data.HDTestPlan;
import com.intuit.tank.harness.data.ThinkTimeStep;

/** Uses a mock frame, so it runs headless */
public class VariablesPanelTest {

    private static final String INITIAL = "Initial Variable Values";
    private static final String COMPLETED = "Completed Variable Values";

    private AgentDebuggerFrame frame;
    private VariablesPanel panel;
    private JTable initialTable;
    private JTable completedTable;

    @BeforeEach
    public void setup() {
        frame = mock(AgentDebuggerFrame.class);
        panel = new VariablesPanel(frame);
        initialTable = tableUnder(panel, INITIAL);
        completedTable = tableUnder(panel, COMPLETED);
    }

    @Test
    public void listensToTheFrame() {
        verify(frame).addStepChangedListener(panel);
        verify(frame).addScriptChangedListener(panel);
        assertEquals("Key", initialTable.getColumnName(0));
        assertEquals("Value", initialTable.getColumnName(1));
        assertEquals(0, initialTable.getRowCount());
    }

    @Test
    public void stepChangedShowsTheEntryAndExitVariablesSortedByKey() {
        panel.stepChanged(step(Map.of("b", "2", "a", "1"), Map.of("c", "3")));

        assertEquals(2, initialTable.getRowCount());
        assertEquals("a", initialTable.getValueAt(0, 0));
        assertEquals("1", initialTable.getValueAt(0, 1));
        assertEquals("b", initialTable.getValueAt(1, 0));
        assertEquals("2", initialTable.getValueAt(1, 1));
        assertEquals(1, completedTable.getRowCount());
        assertEquals("c", completedTable.getValueAt(0, 0));
        assertEquals("3", completedTable.getValueAt(0, 1));
        assertNull(initialTable.getModel().getValueAt(2, 0));
        assertNull(initialTable.getModel().getValueAt(-1, 0));
    }

    @Test
    public void enteringAndExitingAStepFillOneTableEach() {
        panel.stepEntered(step(Map.of("a", "1"), Map.of("a", "2")));
        assertEquals(1, initialTable.getRowCount());
        assertEquals(0, completedTable.getRowCount());

        panel.stepExited(step(null, Map.of("a", "2")));
        assertEquals("2", completedTable.getValueAt(0, 1));

        panel.stepEntered(null);
        panel.stepExited(null);
        assertEquals(0, initialTable.getRowCount());
        assertEquals(0, completedTable.getRowCount());
    }

    @Test
    public void noStepOrANewScriptClearsBothTables() {
        panel.stepChanged(step(Map.of("a", "1"), Map.of("a", "2")));
        panel.stepChanged(null);
        assertEquals(0, initialTable.getRowCount());
        assertEquals(0, completedTable.getRowCount());

        panel.stepChanged(step(Map.of("a", "1"), Map.of("a", "2")));
        panel.scriptChanged(new HDTestPlan());
        assertEquals(0, initialTable.getRowCount());
        assertEquals(0, completedTable.getRowCount());
    }

    private static DebugStep step(Map<String, String> entry, Map<String, String> exit) {
        DebugStep step = new DebugStep(new ThinkTimeStep());
        step.setEntryVariables(entry);
        step.setExitVariables(exit);
        return step;
    }

    /** @return the table in the section headed by the label with the given text */
    private static JTable tableUnder(Container root, String title) {
        JLabel label = find(root, JLabel.class, l -> title.equals(l.getText()));
        assertNotNull(label, title);
        JTable table = find(label.getParent().getParent(), JTable.class, t -> true);
        assertNotNull(table, title);
        return table;
    }

    static <T extends Component> T find(Container root, Class<T> type, java.util.function.Predicate<T> test) {
        for (Component c : root.getComponents()) {
            if (type.isInstance(c) && test.test(type.cast(c))) {
                return type.cast(c);
            }
            if (c instanceof Container container) {
                T found = find(container, type, test);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }
}
