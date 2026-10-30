package com.intuit.tank.vmManager.jms;

import com.intuit.tank.vm.vmManager.VMInformation;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JMSMessagesTest {

    @Test
    public void postResponse_isANoOp() {
        // the JMS response is no longer sent; the call must still be safe
        assertNotNull(new JMSMessages());
        assertDoesNotThrow(() -> JMSMessages.postResponse(List.of(new VMInformation())));
        assertDoesNotThrow(() -> JMSMessages.postResponse(null));
    }
}
