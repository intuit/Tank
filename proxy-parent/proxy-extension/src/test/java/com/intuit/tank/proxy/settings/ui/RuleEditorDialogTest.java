package com.intuit.tank.proxy.settings.ui;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;

@DisabledIfEnvironmentVariable(named = "SKIP_GUI_TEST", matches = "true")
public class RuleEditorDialogTest {

    @Test
    void testIsCancelFlag() {
        ProxyConfigDialog configDialog = new ProxyConfigDialog(null);
        RuleEditorDialog ruleEditorDialog = new RuleEditorDialog(configDialog);
        assertFalse(ruleEditorDialog.isCancelFlag());
    }

}
