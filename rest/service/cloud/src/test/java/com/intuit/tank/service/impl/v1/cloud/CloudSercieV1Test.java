package com.intuit.tank.service.impl.v1.cloud;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

public class CloudSercieV1Test {

    @InjectMocks
    CloudServiceV1 cloudServiceV1;

    private AutoCloseable closeable;

    @BeforeEach
    void initService() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void closeService() throws Exception {
        closeable.close();
    }

    @Test
    public void testPing() {
        assertEquals("PONG CloudServiceV1", cloudServiceV1.ping());
    }

    @Test
    public void testUserIdFromRangeEmpty() {
        assertEquals("0",cloudServiceV1.userIdFromRange("0", 0, 0));
    }

    @Test
    public void testUserIdFromRange() {
        String result = cloudServiceV1.userIdFromRange("0", 0, 10);
        assertTrue(Integer.parseInt(result) >= 0 &&  Integer.parseInt(result) <= 10);
    }

    @Test
    public void testCostingForDates_1() {
        assertThrows(RuntimeException.class, () -> {
            cloudServiceV1.getCostingForDates("", "");
        });
    }

    @Test
    public void testCostingForDates_2() {
        assertThrows(RuntimeException.class, () -> {
            cloudServiceV1.getCostingForDates("");
        });
    }

}
