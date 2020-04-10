package com.xq.study.ovsdb.client;

import com.vmware.ovsdb.callback.ConnectionCallback;
import com.vmware.ovsdb.exception.OvsdbClientException;
import com.vmware.ovsdb.service.OvsdbClient;
import com.vmware.ovsdb.service.OvsdbPassiveConnectionListener;
import com.vmware.ovsdb.service.impl.OvsdbPassiveConnectionListenerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * @author sk-qianxiao
 */
public class OvsdbClientPassiveConnectionTest extends OvsdbClientTest {
    private static final Logger logger = LoggerFactory.getLogger(OvsdbClientPassiveConnectionTest.class);
    private static final OvsdbPassiveConnectionListener passiveListener
            = new OvsdbPassiveConnectionListenerImpl(executorService);


    @Override
    void setUp(boolean withSsl) {
        CompletableFuture<OvsdbClient> ovsdbClientFuture = new CompletableFuture<>();

        final ConnectionCallback connectionCallback = new ConnectionCallback() {
            @Override
            public void connected(OvsdbClient ovsdbClient) {
                logger.info("xxxxxxxxxxconnected  {}", ovsdbClient + " connected");
                ovsdbClientFuture.complete(ovsdbClient);
            }

            @Override
            public void disconnected(OvsdbClient ovsdbClient) {
                logger.info("xxxxxxxxxxdisconnected {}", ovsdbClient + " disconnected");
            }
        };
        if (!withSsl) {
            passiveListener.startListening(PORT, connectionCallback).join();
        } else {
//      // In passive connection test, the controller is the server and the ovsdb-server is the client
//      SslContext serverSslCtx = sslContextPair.getServerSslCtx();
//      SslContext clientSslCtx = sslContextPair.getClientSslCtx();
//      passiveListener.startListeningWithSsl(PORT, serverSslCtx, connectionCallback).join();
//      activeOvsdbServer.connectWithSsl(clientSslCtx).join();
        }
        ovsdbClient = ovsdbClientFuture.join();
        logger.info("XXXXX 连接获取成功...");
    }

    @Override
    public void testTcpConnection()
            throws OvsdbClientException, IOException {
        super.testTcpConnection();
    }

    @Override
    public void testSslConnection() throws OvsdbClientException, IOException {
        super.testSslConnection();
    }

    public void tearDown() {
        super.teardown();
        passiveListener.stopListening(PORT).join();
    }
}
