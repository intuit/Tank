package com.intuit.tank.entity;

import com.intuit.tank.conversation.Protocol;
import com.intuit.tank.conversation.Request;
import com.intuit.tank.conversation.Response;
import com.intuit.tank.conversation.Transaction;
import com.intuit.tank.proxy.config.ProxyConfiguration;
import com.intuit.tank.proxy.table.TransactionRecordedListener;
import com.intuit.tank.test.TestGroups;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.owasp.proxy.http.MessageFormatException;
import org.owasp.proxy.http.MutableBufferedRequest;
import static org.mockito.Mockito.*;

public class ApplicationTest {

    // Mocks
    private ProxyConfiguration _proxyConfigurationMock;
    private TransactionRecordedListener _transactionRecordedListenerMock;
    private MutableBufferedRequest _mutableBufferedRequestMock;

    // Stubs
    private Transaction _transactionStub;
    private Request _requestStub;
    private Response _responseStub;
    private final String startLineStub = "GET /test/servlet/PartnerGatewayServlet?dest=logmein&widgetLogin=true&smartLogin=true&destUrl=!1&userStrId=rtanizawa_iamtestpass HTTP/1.1";
    private final String foundResponseFirstLineStub = "HTTP/1.1 302 Moved Temporarily";
    private final String hostStub = "iop-e2e1.onlinepayroll.intuit.com";

    private Application _sut;


    @BeforeEach
    public void SetUp() {
        _proxyConfigurationMock = mock(ProxyConfiguration.class);
        _transactionRecordedListenerMock = mock(TransactionRecordedListener.class);
        _mutableBufferedRequestMock = mock(MutableBufferedRequest.class);

        _transactionStub = new Transaction();
        _requestStub = new Request();
        _responseStub = new Response();

        _requestStub.setProtocol(Protocol.https);
        _requestStub.setFirstLine(startLineStub);
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
        when(_mutableBufferedRequestMock.getHeader("Host")).thenReturn(hostStub);
        when(_mutableBufferedRequestMock.getStartLine()).thenReturn(startLineStub);

        // Act
        _sut.setResponseForCurrentTransaction(_transactionStub, _responseStub, _mutableBufferedRequestMock);

        // Assert
        verify(_transactionRecordedListenerMock, times(1)).transactionProcessed(any(Transaction.class), anyBoolean());
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void setResponseForCurrentTransaction_Given_302_With_Redirect_Configuration_Disabled_Still_Processes_Transaction() throws Exception {
        // Arrange
        when(_proxyConfigurationMock.isFollowRedirects()).thenReturn(false);
        when(_mutableBufferedRequestMock.getHeader("Host")).thenReturn(hostStub);
        when(_mutableBufferedRequestMock.getStartLine()).thenReturn(startLineStub);

        // Act
        _sut.setResponseForCurrentTransaction(_transactionStub, _responseStub, _mutableBufferedRequestMock);

        // Assert
        verify(_transactionRecordedListenerMock, times(1)).transactionProcessed(any(Transaction.class), anyBoolean());
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void setResponseForCurrentTransaction_Given_MessageFormatException_Still_Processes_Transaction() throws Exception {
        // Arrange
        when(_proxyConfigurationMock.isFollowRedirects()).thenReturn(true);
        when(_mutableBufferedRequestMock.getHeader("Host")).thenReturn(hostStub);
        when(_mutableBufferedRequestMock.getStartLine()).thenThrow(MessageFormatException.class);

        // Act
        _sut.setResponseForCurrentTransaction(_transactionStub, _responseStub, _mutableBufferedRequestMock);

        // Assert
        verify(_transactionRecordedListenerMock, times(1)).transactionProcessed(any(Transaction.class), anyBoolean());
    }
}


