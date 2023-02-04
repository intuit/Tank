package com.intuit.tank.harness.functions;

import com.intuit.tank.harness.test.data.Variables;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FunctionHandlerTest {
    @Test
    public void testValidFunctionException() {
        boolean result = FunctionHandler.validFunction("#function");
        assertFalse(result);
        result = FunctionHandler.validFunction("string");
        assertFalse(result);
        result = FunctionHandler.validFunction(null);
        assertFalse(result);
    }

    @Test
    public void testValidRemainingExecuteFunction() {
        Variables variables = new Variables();
        String result = FunctionHandler.executeFunction("#function.string.userid.2.06-01-2011", variables, "test");
        assertTrue(result.contains("06-01-2011"));
        result = FunctionHandler.executeFunction("#function.datatype.BYTE_MAX", variables, "test");
        assertEquals("127", result);
        result = FunctionHandler.executeFunction("#function.tax.getssn", variables, "test");
        assertEquals("", result);
        result = FunctionHandler.executeFunction(null, variables, "test");
        assertNull(result);
        result = FunctionHandler.executeFunction("#function.test", variables, "test");
        assertNull(result);
    }
}
