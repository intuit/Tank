package com.intuit.tank.vm.settings;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ModifiedEntityMessageTest {
   private ModifiedEntityMessage message = null;
   private Class<?> entityClass = null;

    @BeforeEach
    public void init() {
        message = new ModifiedEntityMessage(entityClass, 12, ModificationType.ADD);
    }

    @Test
    public void testModifiedEntityMessage() {
        assertNull(message.getEntityClass());
        assertEquals(12, message.getId());
        assertEquals(ModificationType.ADD, message.getType());
    }
}
