/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.controller.cluster.datastore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.opendaylight.controller.md.sal.common.util.jmx.AbstractMXBean;

public class ShardStatsTest {
    private MBeanServer mbeanServer;
    private ShardStats shardStats;
    private ObjectName testMBeanName;

    @Before
    public void setUp() throws Exception {
        shardStats = new ShardStats("shard-1", "DataStore", null);
        shardStats.registerMBean();
        mbeanServer = ManagementFactory.getPlatformMBeanServer();
        String objectName = AbstractMXBean.BASE_JMX_PREFIX + "type=" + shardStats.getMBeanType() + ",Category="
                + shardStats.getMBeanCategory() + ",name=" + shardStats.getMBeanName();
        testMBeanName = new ObjectName(objectName);
    }

    @After
    public void tearDown() {
        shardStats.unregisterMBean();
    }

    @Test
    public void testGetShardName() throws Exception {
        assertEquals("shard-1", mbeanServer.getAttribute(testMBeanName, "ShardName"));
    }

    @Test
    public void testGetCommittedTransactionsCount() throws Exception {
        //let us increment some transactions count and then check
        shardStats.incrementCommittedTransactionCount();
        shardStats.incrementCommittedTransactionCount();
        shardStats.incrementCommittedTransactionCount();

        //now let us get from MBeanServer what is the transaction count.
        assertEquals(3L, mbeanServer.getAttribute(testMBeanName, "CommittedTransactionsCount"));
    }

    @Test
    public void testGetLastCommittedTransactionTime() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        assertEquals(sdf.format(new Date(0L)), shardStats.getLastCommittedTransactionTime());
        long millis = System.currentTimeMillis();
        shardStats.setLastCommittedTransactionTime(millis);

        //now let us get from MBeanServer what is the transaction count.
        Object attribute = mbeanServer.getAttribute(testMBeanName,
            "LastCommittedTransactionTime");
        assertEquals(sdf.format(new Date(millis)), attribute);
        assertNotEquals(attribute, sdf.format(new Date(millis - 1)));
    }

    @Test
    public void testGetFailedTransactionsCount() throws Exception {
        //let us increment some transactions count and then check
        shardStats.incrementFailedTransactionsCount();
        shardStats.incrementFailedTransactionsCount();

        //now let us get from MBeanServer what is the transaction count.
        assertEquals(2L, mbeanServer.getAttribute(testMBeanName, "FailedTransactionsCount"));
    }

    @Test
    public void testGetAbortTransactionsCount() throws Exception {
        //let us increment AbortTransactions count and then check
        shardStats.incrementAbortTransactionsCount();
        shardStats.incrementAbortTransactionsCount();

        //now let us get from MBeanServer what is the transaction count.
        assertEquals(2L, mbeanServer.getAttribute(testMBeanName, "AbortTransactionsCount"));
    }

    @Test
    public void testGetFailedReadTransactionsCount() throws Exception {
        //let us increment FailedReadTransactions count and then check
        shardStats.incrementFailedReadTransactionsCount();
        shardStats.incrementFailedReadTransactionsCount();

        //now let us get from MBeanServer what is the transaction count.
        assertEquals(2L, mbeanServer.getAttribute(testMBeanName, "FailedReadTransactionsCount"));
    }

    @Test
    public void testResetTransactionCounters() throws Exception {
        //let us increment committed transactions count and then check
        shardStats.incrementCommittedTransactionCount();
        shardStats.incrementCommittedTransactionCount();
        shardStats.incrementCommittedTransactionCount();

        //now let us get from MBeanServer what is the transaction count.
        assertEquals(3L, mbeanServer.getAttribute(testMBeanName, "CommittedTransactionsCount"));

        //let us increment FailedReadTransactions count and then check
        shardStats.incrementFailedReadTransactionsCount();
        shardStats.incrementFailedReadTransactionsCount();

        //now let us get from MBeanServer what is the transaction count.
        assertEquals(2L, mbeanServer.getAttribute(testMBeanName, "FailedReadTransactionsCount"));

        //here we will reset the counters and check the above ones are 0 after reset
        mbeanServer.invoke(testMBeanName, "resetTransactionCounters", null, null);

        //now let us get from MBeanServer what is the transaction count.
        assertEquals(0L, mbeanServer.getAttribute(testMBeanName, "CommittedTransactionsCount"));
        assertEquals(0L, mbeanServer.getAttribute(testMBeanName, "FailedReadTransactionsCount"));
    }
}
