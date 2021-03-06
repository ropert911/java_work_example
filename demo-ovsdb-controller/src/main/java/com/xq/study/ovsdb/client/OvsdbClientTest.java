/*
 * Copyright (c) 2018 VMware, Inc. All Rights Reserved.
 *
 * This product is licensed to you under the BSD-2 license (the "License").
 * You may not use this product except in compliance with the BSD-2 License.
 *
 * This product may include a number of subcomponents with separate copyright
 * notices and license terms. Your use of these subcomponents is subject to the
 * terms and conditions of the subcomponent's license, as noted in the LICENSE
 * file.
 *
 * SPDX-License-Identifier: BSD-2-Clause
 */
package com.xq.study.ovsdb.client;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.vmware.ovsdb.exception.OvsdbClientException;
import com.vmware.ovsdb.jsonrpc.v1.util.JsonUtil;
import com.vmware.ovsdb.protocol.operation.*;
import com.vmware.ovsdb.protocol.operation.notation.*;
import com.vmware.ovsdb.protocol.operation.result.ErrorResult;
import com.vmware.ovsdb.protocol.operation.result.InsertResult;
import com.vmware.ovsdb.protocol.operation.result.OperationResult;
import com.vmware.ovsdb.protocol.operation.result.UpdateResult;
import com.vmware.ovsdb.protocol.schema.DatabaseSchema;
import com.vmware.ovsdb.protocol.schema.TableSchema;
import com.vmware.ovsdb.protocol.util.OvsdbConstant;
import com.vmware.ovsdb.service.OvsdbClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


abstract class OvsdbClientTest {
    private static final Logger logger = LoggerFactory.getLogger(OvsdbClientTest.class);

    static final int TEST_TIMEOUT_MILLIS = 60 * 1000; // 60 seconds

    static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);


    static final int PORT = 6640; // Use port 6641 for testing purpose


    private static final AtomicInteger id = new AtomicInteger(0);


    OvsdbClient ovsdbClient;


    abstract void setUp(boolean withSsl);

    void teardown() {
        ovsdbClient.shutdown();

        OvsdbClientException exception = null;
        try {
            ovsdbClient.listDatabases();
        } catch (OvsdbClientException e) {
            exception = e;
        }
    }

    private void testBasic() throws OvsdbClientException, IOException {
        testListDatabases();
        testGetSchema();
        testInsertTransact();
        testSelectTransact();
//        testUpdateTransact();
//        testMutateTransact();
//        testDeleteTransact();
//        testMultiOperationTransact();
//        testTransactErrorResult();
//        testMonitor();
//        testCancelMonitor();
//        testConnectionInfo();
//        testErrorOperation();
//        testLock();
//        testEcho();
    }

    private void testNegative() throws OvsdbClientException, IOException {
        testInvalidJsonRpcMessage();
        testInvalidMonitorUpdate();
    }

    private void testAll() throws IOException, OvsdbClientException {
        testBasic();
//    testNegative();
    }

    void testTcpConnection()
            throws OvsdbClientException, IOException {
        testAll();
    }

    void testSslConnection()
            throws OvsdbClientException, IOException {
        testAll();
    }

    private void testListDatabases() throws OvsdbClientException {
//        String expectedRequest = getJsonRequestString(OvsdbConstant.LIST_DBS);
//        setupOvsdbEmulator(expectedRequest, "[\"ovs\",\"hardware_vtep\"]", null);

        CompletableFuture<String[]> f = ovsdbClient.listDatabases();
        String[] dbs = f.join();
        for (String db : dbs) {
            logger.info("遍历数据库-{}", db);
        }
//    assertArrayEquals(new String[] {"ovs", "hardware_vtep"}, f.join());
    }

    private void testGetSchema() throws OvsdbClientException, IOException {
//        URL url = getClass().getClassLoader().getResource("vtep_schema.json");
//    assertNotNull(url);

//        File schemaFile = new File(url.getFile());
//        String schemaString = new String(
//                Files.readAllBytes(schemaFile.toPath()));
//        String expectedRequest = getJsonRequestString(
//                OvsdbConstant.GET_SCHEMA, "hardware_vtep"
//        );
//        setupOvsdbEmulator(expectedRequest, schemaString, null);
//        DatabaseSchema expectedResult = JsonUtil.deserialize(
//                schemaString, DatabaseSchema.class);
        CompletableFuture<DatabaseSchema> databaseSchemaCompletableFuture = ovsdbClient.getSchema("Open_vSwitch");
        DatabaseSchema databaseSchema = databaseSchemaCompletableFuture.join();
        logger.info("遍历表项--getName={}", databaseSchema.getName());
        logger.info("遍历表项--getCksum={}", databaseSchema.getCksum());
        Map<String, TableSchema> tableSchemaMap = databaseSchema.getTables();
        for (Map.Entry<String, TableSchema> tableSchemaEntry : tableSchemaMap.entrySet()) {
            logger.info("遍历表项-- 表名={}, toString={}", tableSchemaEntry.getKey(), tableSchemaEntry.getValue());
        }

//    assertEquals(expectedResult, f.join());
    }

    private void testInsertTransact() throws OvsdbClientException {
//        UUID uuid = UUID.randomUUID();
        Map<String, Value> columns = ImmutableMap.of(
                "ssid", Atom.string("ssid_xq"),
                "if_name", Atom.string("if_name"),
                "btm", Atom.integer(1)
        );
        Insert insert = new Insert("Wifi_VIF_Config", new Row(columns));
//        String expectedRequest = getJsonRequestString(OvsdbConstant.TRANSACT,
//                "hardware_vtep", insert
//        );
//        setupOvsdbEmulator(expectedRequest,
//                "[{\"uuid\":[\"uuid\",\"" + uuid + "\"]}]", null
//        );

        CompletableFuture<OperationResult[]> f = ovsdbClient.transact("Open_vSwitch", ImmutableList.of(insert));
        OperationResult[] ops = f.join();
        for (OperationResult opr : ops) {
            logger.info("insert 结果:{}", opr.toString());
        }

//        InsertResult expectedResult = new InsertResult(new Uuid(uuid));
//    assertArrayEquals(new OperationResult[] {expectedResult}, f.join());
    }

    private void testSelectTransact() throws OvsdbClientException {
//        Map<String, Value> columns = ImmutableMap.of(
//                "name", Atom.string("ls1"),
//                "description", Atom.string("first logical switch"),
//                "tunnel_key", Atom.integer(5001)
//        );


        Select select = new Select("Wifi_VIF_Config").where("ssid", Function.EQUALS, "ssid_xq");
//        String expectedRequest = getJsonRequestString(
//                OvsdbConstant.TRANSACT, "hardware_vtep", select);

//        setupOvsdbEmulator(expectedRequest,
//                "[{\"rows\":[{\"name\":\"ls1\",\"description\":\"first "
//                        + "logical switch\",\"tunnel_key\":5001}]"
//                        + "}]", null
//        );

        logger.info("开始查询........");
        CompletableFuture<OperationResult[]> f = ovsdbClient.transact("Open_vSwitch", ImmutableList.of(select));
        OperationResult[] ops = f.join();
        logger.info("get result........");
        for (OperationResult opr : ops) {
            logger.info("select 结果:{}", opr.toString());
        }
//        SelectResult expectedResult = new SelectResult(ImmutableList.of(new Row(columns)));
//    assertArrayEquals(new OperationResult[] {expectedResult}, f.join());
    }

    private void testUpdateTransact() throws OvsdbClientException {
        Map<String, Value> columns = ImmutableMap.of(
                "name", Atom.string("ls1"),
                "description", Atom.string("first logical switch"),
                "tunnel_key", Atom.integer(5000)
        );
        Update update = new Update(
                "Logical_Switch", new Row(columns))
                .where("name", Function.EQUALS, "ls1");
//        String expectedRequest = getJsonRequestString(
//                OvsdbConstant.TRANSACT, "hardware_vtep", update);
//        setupOvsdbEmulator(expectedRequest, "[" + "{\"count\":1}" + "]", null);

        CompletableFuture<OperationResult[]> f = ovsdbClient.transact(
                "hardware_vtep", ImmutableList.of(update));

        UpdateResult expectedResult = new UpdateResult(1);
        logger.info("更新结果 {}", expectedResult);
//    assertArrayEquals(new OperationResult[] {expectedResult}, f.join());
    }

    private void testMutateTransact() throws OvsdbClientException {
        Mutate mutate = new Mutate("Logical_Switch")
                .where("tunnel_key", Function.GREATER_THAN, 5000)
                .mutation("tunnel_key", Mutator.SUM, 3);
        String expectedRequest = getJsonRequestString(OvsdbConstant.TRANSACT,
                "hardware_vtep", mutate
        );
        setupOvsdbEmulator(expectedRequest, "[" + "{\"count\":3}" + "]", null);

        CompletableFuture<OperationResult[]> f = ovsdbClient
                .transact("hardware_vtep", ImmutableList.of(mutate));

        UpdateResult expectedResult = new UpdateResult(3);
//    assertArrayEquals(new OperationResult[] {expectedResult}, f.join());
    }

    private void testDeleteTransact() throws OvsdbClientException {
        Delete delete = new Delete("Logical_Switch")
                .where("description", Function.INCLUDES, "something");

        String expectedRequest = getJsonRequestString(OvsdbConstant.TRANSACT,
                "hardware_vtep", delete
        );
        setupOvsdbEmulator(expectedRequest, "[" + "{\"count\":0}" + "]", null);

        CompletableFuture<OperationResult[]> f = ovsdbClient
                .transact("hardware_vtep", ImmutableList.of(delete));

        UpdateResult expectedResult = new UpdateResult(0);
//    assertArrayEquals(new OperationResult[] {expectedResult}, f.join());
    }

    private void testMultiOperationTransact() throws OvsdbClientException {
        UUID uuid = UUID.randomUUID();
        Map<String, Value> columns = ImmutableMap.of(
                "name", Atom.string("ls1"),
                "description", Atom.string("first logical switch"),
                "tunnel_key", Atom.integer(5001)
        );
        String uuidName = "insert";
        Insert insert = new Insert(
                "Logical_Switch", new Row(columns), "insert");

        Mutate mutate = new Mutate("Ucast_Macs_Remote")
                .where("MAC", Function.EQUALS, "01:23:45:67:89:ab")
                .mutation("logical_switch", Mutator.INSERT, new NamedUuid(uuidName));

        Delete delete = new Delete("Physical_Switch")
                .where("tunnel_ips", Function.INCLUDES, Set.of("192.168.1.1"));

        String expectedRequest = getJsonRequestString(OvsdbConstant.TRANSACT,
                "hardware_vtep", insert, mutate, delete
        );
        setupOvsdbEmulator(expectedRequest,
                "[{\"uuid\":[\"uuid\",\"" + uuid + "\"]},"
                        + "{\"count\":1},{\"count\":0}]", null
        );

        CompletableFuture<OperationResult[]> f = ovsdbClient.transact(
                "hardware_vtep", ImmutableList.of(insert, mutate, delete));

        OperationResult[] expectedResult = new OperationResult[3];
        expectedResult[0] = new InsertResult(new Uuid(uuid));
        expectedResult[1] = new UpdateResult(1);
        expectedResult[2] = new UpdateResult(0);
//    assertArrayEquals(expectedResult, f.join());
    }

    private void testTransactErrorResult() throws OvsdbClientException {
        Map<String, Value> columns = ImmutableMap.of(
                "name", Atom.string("ls1"),
                "description", Atom.string("first logical switch"),
                "tunnel_key", Atom.integer(5001)
        );
        String uuidName = "insert";
        Insert insert = new Insert(
                "Logical_Switch", new Row(columns), "insert");

        Mutate mutate = new Mutate("Ucast_Macs_Remote")
                .where("MAC", Function.EQUALS, "01:23:45:67:89:ab")
                .mutation("logical_switch", Mutator.INSERT, new NamedUuid(uuidName));

        Delete delete = new Delete("Physical_Switch")
                .where("tunnel_ips", Function.INCLUDES, Set.of("192.168.1.1"));

        String expectedRequest = getJsonRequestString(OvsdbConstant.TRANSACT,
                "hardware_vtep", insert, mutate, delete
        );
        setupOvsdbEmulator(expectedRequest,
                "[{\"error\":\"constraint violation\",\"details\":\"ls1 already "
                        + "exists\"}, null, null]", null
        );

        CompletableFuture<OperationResult[]> f = ovsdbClient.transact(
                "hardware_vtep", ImmutableList.of(insert, mutate, delete));

        OperationResult[] expectedResult = new OperationResult[3];
        expectedResult[0] = new ErrorResult("constraint violation", "ls1 already exists");

//    assertArrayEquals(expectedResult, f.join());
    }

    private void testMonitor() throws OvsdbClientException {
//    String monitorId = "1";
//    MonitorRequest monitorRequest = new MonitorRequest();
//    MonitorRequests monitorRequests = new MonitorRequests(
//        ImmutableMap.of("Logical_Switch", monitorRequest));
//
//    Map<String, Value> columns = ImmutableMap.of(
//        "name", Atom.string("ls1"),
//        "description", Atom.string("First logical switch"),
//        "tunnel_key", Atom.integer(5001)
//    );
//    UUID uuid = UUID.randomUUID();
//    RowUpdate initRowUpdate = new RowUpdate(null, new Row(columns));
//
//    TableUpdate expectedInitUpdate = new TableUpdate(
//        ImmutableMap.of(uuid, initRowUpdate));
//    TableUpdates expectedInitUpdates = new TableUpdates(
//        ImmutableMap.of("Logical_Switch", expectedInitUpdate));
//
//    String expectedRequest = getJsonRequestString(
//        "monitor", "hardware_vtep", monitorId, monitorRequests);
//
//    setupOvsdbEmulator(
//        expectedRequest,
//        "{\"Logical_Switch\":{\"" + uuid
//            + "\":{\"old\":null,\"new\":{\"name\":\"ls1\","
//            + "\"description\":\"First logical switch\","
//            + "\"tunnel_key\":5001}}}}",
//        null
//    );
//
//    MonitorCallback monitorCallback = mock(MonitorCallback.class);
//    CompletableFuture<TableUpdates> f = ovsdbClient
//        .monitor("hardware_vtep", monitorId, monitorRequests, monitorCallback);
//
//    assertEquals(expectedInitUpdates, f.join());
//
//    // Verify that the monitor callback works
//    UUID insertUuid = UUID.randomUUID();
//    RowUpdate insertUpdate = new RowUpdate(
//        null,
//        new Row(ImmutableMap.of(
//            "name", Atom.string("ls2"),
//            "tunnel_key", Atom.integer(5002)
//        ))
//    );
//
//    UUID deleteUuid = UUID.randomUUID();
//    RowUpdate deleteUpdate = new RowUpdate(
//        new Row(ImmutableMap.of(
//            "name", Atom.string("ls1"),
//            "tunnel_key", Atom.integer(5001)
//        )), null
//    );
//
//    UUID modifyUuid = UUID.randomUUID();
//    RowUpdate modifyUpdate = new RowUpdate(
//        new Row(ImmutableMap.of("tunnel_key", Atom.integer(5003))),
//        new Row(ImmutableMap.of("tunnel_key", Atom.integer(5004)))
//    );
//
//    TableUpdate expectedTableUpdate = new TableUpdate(
//        ImmutableMap.of(
//            insertUuid, insertUpdate, deleteUuid, deleteUpdate,
//            modifyUuid, modifyUpdate
//        ));
//    TableUpdates expectedTableUpdates = new TableUpdates(
//        ImmutableMap.of("Logical_Switch", expectedTableUpdate));
//
//    ovsdbServerEmulator.write(
//        "{\"method\":\"update\",\"params\":[\"" + monitorId + "\","
//            + "{\"Logical_Switch\":" + "{"
//            + "\"" + insertUuid + "\":{\"old\":null,"
//            + "\"new\":{\"name\":\"ls2\",\"tunnel_key\":5002}},"
//            + "\"" + deleteUuid + "\":"
//            + "{\"old\":{\"name\":\"ls1\",\"tunnel_key\":5001},"
//            + "\"new\":null},"
//            + "\"" + modifyUuid + "\":{\"old\":{\"tunnel_key\":5003},"
//            + "\"new\":{\"tunnel_key\":5004}}}"
//            + "}],\"id\":null}");
//
//    verify(monitorCallback, timeout(1000).times(1)).update(expectedTableUpdates);
    }

    private void testCancelMonitor() throws OvsdbClientException {
//    String monitorId = "1";
//    MonitorRequest monitorRequest = new MonitorRequest();
//    MonitorRequests monitorRequests = new MonitorRequests(
//        ImmutableMap.of("Logical_Switch", monitorRequest));
//
//    String expectedRequest = getJsonRequestString(
//        "monitor", "hardware_vtep", monitorId, monitorRequests);
//
//    setupOvsdbEmulator(expectedRequest, "{}", null);
//
//    MonitorCallback monitorCallback = mock(MonitorCallback.class);
//    CompletableFuture f = ovsdbClient
//        .monitor("hardware_vtep", monitorId, monitorRequests, monitorCallback);
//    f.join();
//
//    expectedRequest = getJsonRequestString("monitor_cancel", monitorId);
//    setupOvsdbEmulator(expectedRequest, "{}", null);
//    f = ovsdbClient.cancelMonitor(monitorId);
//    f.join();
    }

    private void testConnectionInfo() {
//    OvsdbConnectionInfo ovsdbClientConnectionInfo = ovsdbClient.getConnectionInfo();
//    OvsdbConnectionInfo ovsdbServerConnectionInfo = ovsdbServerEmulator.getConnectionInfo();
//
//    String ovsdbClientRemoteIp = ovsdbClientConnectionInfo.getRemoteAddress().getHostAddress();
//    String ovsdbServerLocalIp = ovsdbServerConnectionInfo.getLocalAddress().getHostAddress();
//
//    assertEquals(ovsdbClientRemoteIp, ovsdbServerLocalIp);
//
//    String ovsdbClientLocalIp = ovsdbClientConnectionInfo.getLocalAddress().getHostAddress();
//    String ovsdbServerRemoteIp = ovsdbServerConnectionInfo.getRemoteAddress().getHostAddress();
//
//    assertEquals(ovsdbClientLocalIp, ovsdbServerRemoteIp);
//
//    int ovsdbClientRemotePort = ovsdbClientConnectionInfo.getRemotePort();
//    int ovsdbServerLocalPort = ovsdbServerConnectionInfo.getLocalPort();
//
//    assertEquals(ovsdbClientRemotePort, ovsdbServerLocalPort);
//
//    int ovsdbClientLocalPort = ovsdbClientConnectionInfo.getLocalPort();
//    int ovsdbServerRemotePort = ovsdbServerConnectionInfo.getRemotePort();
//
//    assertEquals(ovsdbClientLocalPort, ovsdbServerRemotePort);
//
//    Certificate ovsdbClientRemoteCertificate = ovsdbClientConnectionInfo.getRemoteCertificate();
//    Certificate ovsdbServerLocalCertificate = ovsdbServerConnectionInfo.getLocalCertificate();
//
//    assertEquals(ovsdbClientRemoteCertificate, ovsdbServerLocalCertificate);
//
//    Certificate ovsdbClientLocalCertificate = ovsdbClientConnectionInfo.getLocalCertificate();
//    Certificate ovsdbServerRemoteCertificate = ovsdbServerConnectionInfo.getRemoteCertificate();
//
//    assertEquals(ovsdbClientLocalCertificate, ovsdbServerRemoteCertificate);
    }

    private void testErrorOperation() throws OvsdbClientException {
//    Map<String, Value> columns = ImmutableMap.of(
//        "name", Atom.string("ls1"),
//        "description", Atom.string("first logical switch"),
//        "tunnel_key", Atom.integer(5001)
//    );
//    String uuidName = "insert";
//    Insert insert = new Insert(
//        "Logical_Switch", new Row(columns), "insert");
//
//    Mutate mutate = new Mutate("Ucast_Macs_Remote")
//        .where("MAC", Function.EQUALS, "01:23:45:67:89:ab")
//        .mutation("logical_switch", Mutator.INSERT, new NamedUuid(uuidName));
//
//    Delete delete = new Delete("Physical_Switch")
//        .where("tunnel_ips", Function.INCLUDES, Set.of("192.168.1.1"));
//
//    String expectedRequest = getJsonRequestString(OvsdbConstant.TRANSACT,
//        "hardware_vtep", insert, mutate, delete
//    );
//    setupOvsdbEmulator(expectedRequest,
//        "[{\"error\":\"resources exhausted\"}, null, null]", null
//    );
//
//    CompletableFuture<OperationResult[]> f = ovsdbClient.transact(
//        "hardware_vtep", ImmutableList.of(insert, mutate, delete));
//
//    // After the first error, following results are all null
//    OperationResult[] expectedResult = new OperationResult[3];
//    expectedResult[0] = new ErrorResult("resources exhausted", null);
//    assertArrayEquals(expectedResult, f.join());
    }

    private void testLock() throws OvsdbClientException {
//    // Get lock-1
//    String lockId1 = "lock-1";
//    LockCallback lockCallback1 = mock(LockCallback.class);
//    String expectedRequest1 = getJsonRequestString(LOCK, lockId1);
//
//    setupOvsdbEmulator(expectedRequest1, "{\"locked\":true}", null);
//
//    LockResult lockResult1 = ovsdbClient.lock(lockId1, lockCallback1).join();
//    assertTrue(lockResult1.isLocked());
//
//    // Doest not get lock-2
//    String lockId2 = "lock-2";
//    LockCallback lockCallback2 = mock(LockCallback.class);
//    String expectedRequest2 = getJsonRequestString(LOCK, lockId2);
//
//    setupOvsdbEmulator(expectedRequest2, "{\"locked\":false}", null);
//    LockResult lockResult2 = ovsdbClient.lock(lockId2, lockCallback2).join();
//    assertFalse(lockResult2.isLocked());
//
//    // After a while, get locked notification for lock-2
//    ovsdbServerEmulator.write("{\"method\":\"locked\", "
//        + "\"params\":[\"" + lockId2 + "\"], \"id\":null}");
//
//    verify(lockCallback2, timeout(VERIFY_TIMEOUT_MILLIS)).locked();
//
//    // Unlock lock-1
//    String expectedRequest3 = getJsonRequestString(UNLOCK, lockId1);
//    setupOvsdbEmulator(expectedRequest3, "{}", null);
//    ovsdbClient.unlock(lockId1).join();
//
//    // Steal lock-3
//    String lockId3 = "lock-3";
//    String expectedRequest4 = getJsonRequestString(STEAL, lockId3);
//    LockCallback lockCallback3 = mock(LockCallback.class);
//    setupOvsdbEmulator(expectedRequest4, "{\"locked\":true}", null);
//    LockResult stealResult = ovsdbClient.steal(lockId3, lockCallback3).join();
//    assertTrue(stealResult.isLocked());
//
//    // lock-2 is stolen
//    ovsdbServerEmulator.write("{\"method\":\"stolen\", "
//        + "\"params\":[\"" + lockId2 + "\"], \"id\":null}");
//    verify(lockCallback2, timeout(VERIFY_TIMEOUT_MILLIS)).stolen();
//
//    // lock-3 is stolen
//    ovsdbServerEmulator.write("{\"method\":\"stolen\", "
//        + "\"params\":[\"" + lockId3 + "\"], \"id\":null}");
//    verify(lockCallback3, timeout(VERIFY_TIMEOUT_MILLIS)).stolen();
    }

    private void testEcho() {
//    CompletableFuture<Void> successFuture1 = new CompletableFuture<>();
//    final String expectedResponse1 = "{\"result\":[]," + "\"error\":null,\"id\":\"echo\"}";
//    ovsdbServerEmulator.registerReadCallback(msg -> {
//      if (msg.equals(expectedResponse1)) {
//        successFuture1.complete(null);
//      }
//    });
//    ovsdbServerEmulator.write("{\"method\":\"echo\",\"params\":[],\"id\":\"echo\"}");
//    try {
//      successFuture1.get(VERIFY_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
//    } catch (InterruptedException | ExecutionException | TimeoutException e) {
//      fail(e.getMessage());
//    }
//
//    CompletableFuture<Void> successFuture2 = new CompletableFuture<>();
//    final String expectedResponse2 = "{\"result\":[123,\"456\",true]," + "\"error\":null,"
//        + "\"id\":\"echo\"}";
//    ovsdbServerEmulator.registerReadCallback(msg -> {
//      if (msg.equals(expectedResponse2)) {
//        successFuture2.complete(null);
//      }
//    });
//    ovsdbServerEmulator.write("{\"method\":\"echo\",\"params\":[123,\"456\",true],"
//        + "\"id\":\"echo\"}");
//    try {
//      successFuture2.get(VERIFY_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
//    } catch (InterruptedException | ExecutionException | TimeoutException e) {
//      fail(e.getMessage());
//    }
//
//    CompletableFuture<Void> successFuture3 = new CompletableFuture<>();
//    final String expectedResponse3 = "{\"result\":[\"inactivity probe\"]," + "\"error\":null,"
//        + "\"id\":\"echo\"}";
//    ovsdbServerEmulator.registerReadCallback(msg -> {
//      if (msg.equals(expectedResponse3)) {
//        successFuture3.complete(null);
//      }
//    });
//    ovsdbServerEmulator.write("{\"method\":\"echo\",\"params\":[\"inactivity probe\"],"
//        + "\"id\":\"echo\"}");
//    try {
//      successFuture3.get(VERIFY_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
//    } catch (InterruptedException | ExecutionException | TimeoutException e) {
//      fail(e.getMessage());
//    }
    }

    private void testInvalidJsonRpcMessage() throws OvsdbClientException, IOException {
//    // Send an invalid JSON-RPC message
//    ovsdbServerEmulator.write("{\"method\":\"foo\"}");
//    // Make sure it still works after receiving this invalid message.
//    testBasic();
    }

    private void testInvalidMonitorUpdate() throws OvsdbClientException, IOException {
//    // Send an invalid monitor update message
//    ovsdbServerEmulator.write("{\"method\":\"update\", \"params\":[\"blabla\"], \"id\":null}");
//    // Make sure it still works after processing this invalid message.
//    testBasic();
    }

    private void setupOvsdbEmulator(
            String request, String result, String error
    ) {
//    ovsdbServerEmulator.registerReadCallback(msg -> {
//      if (msg.equals(request)) {
//        ovsdbServerEmulator.write(
//            "{\"id\":\"" + (id.getAndIncrement()) + "\", \"result\":" + result + ", "
//                + "\"error\":" + error + "}");
//      }
//    });
    }

    private String getJsonRequestString(String method, Object... params) {
        // Need to remove space between each two params
        String strParams = Arrays.stream(params).map(
                JsonUtil::serializeNoException).collect(
                Collectors.toList()).toString().replace(", ", ",");
        return "{\"method\":\"" + method
                + "\",\"params\":" + strParams + ",\"id\":\"" + id.get() + "\"}";
    }
}
