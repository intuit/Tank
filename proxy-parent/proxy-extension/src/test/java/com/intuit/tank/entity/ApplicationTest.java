package com.intuit.tank.entity;

import com.intuit.tank.conversation.*;
import com.intuit.tank.proxy.config.ProxyConfiguration;
import com.intuit.tank.proxy.table.TransactionRecordedListener;
import com.intuit.tank.test.TestGroups;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ApplicationTest {

    // Mocks
    private ProxyConfiguration _proxyConfigurationMock;
    private TransactionRecordedListener _transactionRecordedListenerMock;

    // Stubs
    private Transaction _transactionStub;
    private Request _requestStub;
    private Request _requestWithHeaderStub;
    private Response _responseStub;
    private final Header headerStub = new Header("Referer", "https://c11.perf.qbo.intuit.com/qbo11/login");
    private final String startLineStub = "GET /test/servlet/PartnerGatewayServlet?dest=logmein&widgetLogin=true&smartLogin=true&destUrl=!1&userStrId=rtanizawa_iamtestpass HTTP/1.1";
    private final String foundResponseFirstLineStub = "HTTP/1.1 302 Moved Temporarily";

    private Application _sut;

    @BeforeEach
    public void SetUp() {
        _proxyConfigurationMock = mock(ProxyConfiguration.class);
        _transactionRecordedListenerMock = mock(TransactionRecordedListener.class);

        _transactionStub = new Transaction();
        _requestStub = new Request();
        _requestWithHeaderStub = new Request();
        _responseStub = new Response();

        _requestStub.setProtocol(Protocol.https);
        _requestStub.setFirstLine(startLineStub);
        _requestWithHeaderStub.setProtocol(Protocol.https);
        _requestWithHeaderStub.setFirstLine(startLineStub);
        _requestWithHeaderStub.addHeader(headerStub);
        _responseStub.setFirstLine(foundResponseFirstLineStub);
        _transactionStub.setRequest(_requestStub);

        when(_proxyConfigurationMock.getOutputFile()).thenReturn("OutputFileStub");

        _sut = new Application(_proxyConfigurationMock);
        _sut.setConfig(_proxyConfigurationMock);
        _sut.startSession(_transactionRecordedListenerMock);
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void setResponseForCurrentTransaction_Given_302_HappyPath_Processes_Transaction() throws Exception {
        // Arrange
        when(_proxyConfigurationMock.isFollowRedirects()).thenReturn(true);

        // Act
        _sut.setResponseForCurrentTransaction(_transactionStub, _responseStub);

        // Assert
        verify(_transactionRecordedListenerMock, times(1)).transactionProcessed(any(Transaction.class), anyBoolean());
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void setResponseForCurrentTransaction_Given_302_With_Redirect_Configuration_Disabled_Still_Processes_Transaction() throws Exception {
        // Arrange
        when(_proxyConfigurationMock.isFollowRedirects()).thenReturn(false);

        // Act
        _sut.setResponseForCurrentTransaction(_transactionStub, _responseStub);

        // Assert
        verify(_transactionRecordedListenerMock, times(1)).transactionProcessed(any(Transaction.class), anyBoolean());
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void setResponseForCurrentTransaction_Given_MessageFormatException_Still_Processes_Transaction() throws Exception {
        // Arrange
        when(_proxyConfigurationMock.isFollowRedirects()).thenReturn(true);

        // Act
        _sut.setResponseForCurrentTransaction(_transactionStub, _responseStub);

        // Assert
        verify(_transactionRecordedListenerMock, times(1)).transactionProcessed(any(Transaction.class), anyBoolean());
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void setRequestForCurrentTransaction_Given_Request_Embeds_Expected_Request() {
        // Arrange
        Request expectedRequest = _transactionStub.getRequest();

        // Act
        Transaction actualTransaction = _sut.setRequestForCurrentTransaction(_requestStub);
        Request actualRequest = actualTransaction.getRequest();

        // Assert
        assertEquals(expectedRequest, actualRequest);
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void setRequestForCurrentTransaction_Given_Request_Embeds_Expected_FirstLine() {
        // Arrange
        Request expectedRequest = _requestWithHeaderStub;
        String expectedFirstLine = expectedRequest.getFirstLine();

        // Act
        Transaction actualTransaction = _sut.setRequestForCurrentTransaction(_requestStub);
        Request actualRequest = actualTransaction.getRequest();
        String actualFirstLine = actualRequest.getFirstLine();

        // Assert
        assertEquals(expectedFirstLine, actualFirstLine);
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void setRequestForCurrentTransaction_Given_Request_Embeds_Expected_Header() {
        // Arrange
        Request expectedRequest = _requestWithHeaderStub;
        List<Header> expectedHeadersList = expectedRequest.getHeaders();
        int expectedHeaderIndex = expectedHeadersList.indexOf(headerStub);
        Header expectedHeader = expectedHeadersList.get(expectedHeaderIndex);

        // Act
        Transaction actualTransaction = _sut.setRequestForCurrentTransaction(_requestWithHeaderStub);
        Request actualRequest = actualTransaction.getRequest();
        List<Header> actualHeadersList = actualRequest.getHeaders();
        int actualHeaderIndex = actualHeadersList.indexOf(headerStub);
        Header actualHeader = actualHeadersList.get(actualHeaderIndex);

        // Assert
        assertEquals(expectedHeader, actualHeader);
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void setRequestForCurrentTransaction_Given_Multiple_Requests_Embeds_Expected_Request() {
        // Arrange
        Request expectedRequest = _transactionStub.getRequest();

        // Act
        _sut.setRequestForCurrentTransaction(_requestWithHeaderStub);
        Transaction actualTransaction = _sut.setRequestForCurrentTransaction(_requestStub);

        Request actualRequest = actualTransaction.getRequest();

        // Assert
        assertEquals(expectedRequest, actualRequest);
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void setRequestForCurrentTransaction_Given_Multiple_Requests_Embeds_Expected_FirstLine() {
        // Arrange
        Request expectedRequest = _requestWithHeaderStub;
        String expectedFirstLine = expectedRequest.getFirstLine();

        // Act
        _sut.setRequestForCurrentTransaction(_requestStub);
        Transaction actualTransaction = _sut.setRequestForCurrentTransaction(_requestWithHeaderStub);
        Request actualRequest = actualTransaction.getRequest();
        String actualFirstLine = actualRequest.getFirstLine();

        // Assert
        assertEquals(expectedFirstLine, actualFirstLine);
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void setRequestForCurrentTransaction_Given_Multiple_Requests_Embeds_Expected_Header() {
        // Arrange
        Request expectedRequest = _requestWithHeaderStub;
        List<Header> expectedHeadersList = expectedRequest.getHeaders();
        int expectedHeaderIndex = expectedHeadersList.indexOf(headerStub);
        Header expectedHeader = expectedHeadersList.get(expectedHeaderIndex);

        // Act
        _sut.setRequestForCurrentTransaction(_requestStub);
        Transaction actualTransaction = _sut.setRequestForCurrentTransaction(_requestWithHeaderStub);
        Request actualRequest = actualTransaction.getRequest();
        List<Header> actualHeadersList = actualRequest.getHeaders();
        int actualHeaderIndex = actualHeadersList.indexOf(headerStub);
        Header actualHeader = actualHeadersList.get(actualHeaderIndex);

        // Assert
        assertEquals(expectedHeader, actualHeader);
    }
}


