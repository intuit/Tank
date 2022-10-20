package com.intuit.tank.proxy.settings.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * RuleEditorDialogTest
 *
 * @author msreekakula
 *
 */
public class RuleEditorDialogTest {

    @Test
    void testIsCancelFlag() {
        ProxyConfigDialog configDialog = new ProxyConfigDialog(null);
        RuleEditorDialog ruleEditorDialog = new RuleEditorDialog(configDialog);
        assertEquals(false, ruleEditorDialog.isCancelFlag());
    }

}
