package okhttp3.internal.ws;

import okhttp3.internal.ws.WebSocketHttpTest;
import org.junit.Test;

public class WebSocketHttpTestTest {
    
    WebSocketHttpTest webSocketHttpTest = new WebSocketHttpTest();

    @Test
    public void testSetUp() throws Exception {
        webSocketHttpTest.setUp(null);
    }

    @Test
    public void testTearDown() throws Exception {
        webSocketHttpTest.tearDown();
    }

    @Test
    public void testTextMessage() throws Exception {
        webSocketHttpTest.textMessage();
    }

    @Test
    public void testBinaryMessage() throws Exception {
        webSocketHttpTest.binaryMessage();
    }

    @Test
    public void testNullStringThrows() throws Exception {
        webSocketHttpTest.nullStringThrows();
    }

    @Test
    public void testNullByteStringThrows() throws Exception {
        webSocketHttpTest.nullByteStringThrows();
    }

    @Test
    public void testServerMessage() throws Exception {
        webSocketHttpTest.serverMessage();
    }

    @Test
    public void testThrowingOnOpenFailsImmediately() throws Exception {
        webSocketHttpTest.throwingOnOpenFailsImmediately();
    }

    @Test
    public void testThrowingOnFailLogs() throws Exception {
        webSocketHttpTest.throwingOnFailLogs();
    }

    @Test
    public void testThrowingOnMessageClosesImmediatelyAndFails() throws Exception {
        webSocketHttpTest.throwingOnMessageClosesImmediatelyAndFails();
    }

    @Test
    public void testThrowingOnClosingClosesImmediatelyAndFails() throws Exception {
        webSocketHttpTest.throwingOnClosingClosesImmediatelyAndFails();
    }

    @Test
    public void testUnplannedCloseHandledByCloseWithoutFailure() throws Exception {
        webSocketHttpTest.unplannedCloseHandledByCloseWithoutFailure();
    }

    @Test
    public void testUnplannedCloseHandledWithoutFailure() throws Exception {
        webSocketHttpTest.unplannedCloseHandledWithoutFailure();
    }

    @Test
    public void testNon101RetainsBody() throws Exception {
        webSocketHttpTest.non101RetainsBody();
    }

    @Test
    public void testNotFound() throws Exception {
        webSocketHttpTest.notFound();
    }

    @Test
    public void testClientTimeoutClosesBody() throws Exception {
        webSocketHttpTest.clientTimeoutClosesBody();
    }

    @Test
    public void testMissingConnectionHeader() throws Exception {
        webSocketHttpTest.missingConnectionHeader();
    }

    @Test
    public void testWrongConnectionHeader() throws Exception {
        webSocketHttpTest.wrongConnectionHeader();
    }

    @Test
    public void testMissingUpgradeHeader() throws Exception {
        webSocketHttpTest.missingUpgradeHeader();
    }

    @Test
    public void testWrongUpgradeHeader() throws Exception {
        webSocketHttpTest.wrongUpgradeHeader();
    }

    @Test
    public void testMissingMagicHeader() throws Exception {
        webSocketHttpTest.missingMagicHeader();
    }

    @Test
    public void testWrongMagicHeader() throws Exception {
        webSocketHttpTest.wrongMagicHeader();
    }

    @Test
    public void testClientIncludesForbiddenHeader() throws Exception {
        webSocketHttpTest.clientIncludesForbiddenHeader();
    }

    @Test
    public void testWebSocketAndApplicationInterceptors() throws Exception {
        webSocketHttpTest.webSocketAndApplicationInterceptors();
    }

    @Test
    public void testWebSocketAndNetworkInterceptors() throws Exception {
        webSocketHttpTest.webSocketAndNetworkInterceptors();
    }

    @Test
    public void testOverflowOutgoingQueue() throws Exception {
        webSocketHttpTest.overflowOutgoingQueue();
    }

    @Test
    public void testCloseReasonMaximumLength() throws Exception {
        webSocketHttpTest.closeReasonMaximumLength();
    }

    @Test
    public void testCloseReasonTooLong() throws Exception {
        webSocketHttpTest.closeReasonTooLong();
    }

    @Test
    public void testWsScheme() throws Exception {
        webSocketHttpTest.wsScheme();
    }

    @Test
    public void testWsUppercaseScheme() throws Exception {
        webSocketHttpTest.wsUppercaseScheme();
    }

    @Test
    public void testWssScheme() throws Exception {
        webSocketHttpTest.wssScheme();
    }

    @Test
    public void testHttpsScheme() throws Exception {
        webSocketHttpTest.httpsScheme();
    }

    @Test
    public void testReadTimeoutAppliesToHttpRequest() throws Exception {
        webSocketHttpTest.readTimeoutAppliesToHttpRequest();
    }

    @Test
    public void testReadTimeoutAppliesWithinFrames() throws Exception {
        webSocketHttpTest.readTimeoutAppliesWithinFrames();
    }

    @Test
    public void testReadTimeoutDoesNotApplyAcrossFrames() throws Exception {
        webSocketHttpTest.readTimeoutDoesNotApplyAcrossFrames();
    }

    @Test
    public void testClientPingsServerOnInterval() throws Exception {
        webSocketHttpTest.clientPingsServerOnInterval();
    }

    @Test
    public void testClientDoesNotPingServerByDefault() throws Exception {
        webSocketHttpTest.clientDoesNotPingServerByDefault();
    }

    @Test
    public void testUnacknowledgedPingFailsConnection() throws Exception {
        webSocketHttpTest.unacknowledgedPingFailsConnection();
    }

    @Test
    public void testClientCancelsIfCloseIsNotAcknowledged() throws Exception {
        webSocketHttpTest.clientCancelsIfCloseIsNotAcknowledged();
    }

    @Test
    public void testWebSocketsDontTriggerEventListener() throws Exception {
        webSocketHttpTest.webSocketsDontTriggerEventListener();
    }

    @Test
    public void testCallTimeoutAppliesToSetup() throws Exception {
        webSocketHttpTest.callTimeoutAppliesToSetup();
    }

    @Test
    public void testCallTimeoutDoesNotApplyOnceConnected() throws Exception {
        webSocketHttpTest.callTimeoutDoesNotApplyOnceConnected();
    }

    @Test
    public void testWebSocketConnectionIsReleased() throws Exception {
        webSocketHttpTest.webSocketConnectionIsReleased();
    }

    @Test
    public void testCloseWithoutSuccessfulConnect() throws Exception {
        webSocketHttpTest.closeWithoutSuccessfulConnect();
    }

    @Test
    public void testReconnectingToNonWebSocket() throws Exception {
        webSocketHttpTest.reconnectingToNonWebSocket();
    }

    @Test
    public void testCompressedMessages() throws Exception {
        webSocketHttpTest.compressedMessages();
    }

    @Test
    public void testCompressedMessagesNoClientContextTakeover() throws Exception {
        webSocketHttpTest.compressedMessagesNoClientContextTakeover();
    }

    @Test
    public void testCompressedMessagesNoServerContextTakeover() throws Exception {
        webSocketHttpTest.compressedMessagesNoServerContextTakeover();
    }

    @Test
    public void testUnexpectedExtensionParameter() throws Exception {
        webSocketHttpTest.unexpectedExtensionParameter();
    }

    @Test
    public void testClientMaxWindowBitsIncluded() throws Exception {
        webSocketHttpTest.clientMaxWindowBitsIncluded();
    }

    @Test
    public void testServerMaxWindowBitsTooLow() throws Exception {
        webSocketHttpTest.serverMaxWindowBitsTooLow();
    }

    @Test
    public void testServerMaxWindowBitsTooHigh() throws Exception {
        webSocketHttpTest.serverMaxWindowBitsTooHigh();
    }

    @Test
    public void testServerMaxWindowBitsJustRight() throws Exception {
        webSocketHttpTest.serverMaxWindowBitsJustRight();
    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme