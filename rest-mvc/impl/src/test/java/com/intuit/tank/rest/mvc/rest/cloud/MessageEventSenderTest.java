package com.intuit.tank.rest.mvc.rest.cloud;

import com.intuit.tank.vm.settings.ModifiedEntityMessage;
import com.intuit.tank.vm.settings.ModificationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.enterprise.event.Event;

import static org.mockito.Mockito.verify;

class MessageEventSenderTest {

    @InjectMocks
    private MessageEventSender sender;

    @Mock
    private Event<ModifiedEntityMessage> eventSender;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void sendEvent_firesEvent() {
        ModifiedEntityMessage msg = new ModifiedEntityMessage(Object.class, 1, ModificationType.ADD);
        sender.sendEvent(msg);
        verify(eventSender).fire(msg);
    }
}
