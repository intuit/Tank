package com.intuit.tank.harness;

import com.intuit.tank.harness.data.HDTestPlan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestPlanStarterTest {

    private Object _httpClientMock;
    private HDTestPlan _hdTestPlanMock;
    private ThreadGroup _threadGroupMock;

    private final String _tankHttpClientClassStub = "com.intuit.tank.httpclient4.TankHttpClient4";
    private final int _threadCountStub = 10;

    private TestPlanStarter _sut;

    @BeforeEach
    public void SetUp() {
        _httpClientMock = mock(Object.class);
        _hdTestPlanMock = mock(HDTestPlan.class);
        _threadGroupMock = mock(ThreadGroup.class);
    }

    @Test
    public void getPlan_Returns_Initialized_HDTestPlan() {
        // Arrange + Act
        _sut = new TestPlanStarter(_httpClientMock, _hdTestPlanMock, 1, _tankHttpClientClassStub, _threadGroupMock);

        // Assert
        assertEquals(_hdTestPlanMock, _sut.getPlan(), "TestPlan assigned to TestPlanStarter");
    }

    @Test
    public void getNumThreads_Returns_Initialized_Number_Of_Threads() {
        // Arrange
        when(_hdTestPlanMock.getUserPercentage()).thenReturn(100);

        // Act
        _sut = new TestPlanStarter(_httpClientMock, _hdTestPlanMock, _threadCountStub, _tankHttpClientClassStub, _threadGroupMock);

        // Assert
        assertEquals(_threadCountStub, _sut.getNumThreads());
    }

    @Test
    public void getThreadStarted_Given_TestPlanStarter_Not_Run_Returns_Zero() {
        // Arrange + Act
        _sut = new TestPlanStarter(_httpClientMock, _hdTestPlanMock, 1, _tankHttpClientClassStub, _threadGroupMock);

        // Assert
        assertEquals(0, _sut.getThreadsStarted());
    }

    @Test
    public void getThreadStarted_Given_TestPlanStarter_Completion_Returns_Number_Of_Threads_Started() {
        // Arrange
        _sut = new TestPlanStarter(_httpClientMock, _hdTestPlanMock, 1, _tankHttpClientClassStub, _threadGroupMock);

        // Act
        _sut.run();

        // Assert
        assertEquals(1, _sut.getThreadsStarted());
    }

    @Test
    public void isDone_Given_TestPlanStarter_Not_Run_Returns_False() {
        // Arrange + Act
        _sut = new TestPlanStarter(_httpClientMock, _hdTestPlanMock, 1, _tankHttpClientClassStub, _threadGroupMock);

        // Assert
        assertFalse(_sut.isDone());
    }

    @Test
    public void isDone__Given_TestPlanStarter_Completion_Returns_True() {
        // Arrange
        _sut = new TestPlanStarter(_httpClientMock, _hdTestPlanMock, 1, _tankHttpClientClassStub, _threadGroupMock);

        // Act
        _sut.run();

        // Assert
        assertTrue(_sut.isDone());
    }

    @Test
    public void run_Given_Zero_Number_Of_Threads_And_Default_Initialization_Creates_One_Thread() {
        // Arrange
        when(_hdTestPlanMock.getUserPercentage()).thenReturn(100);
        _sut = new TestPlanStarter(_httpClientMock, _hdTestPlanMock, 10, _tankHttpClientClassStub, _threadGroupMock);

        // Act
        _sut.run();

        // Assert
        verify(_threadGroupMock, times(1)).getName();
    }
}
