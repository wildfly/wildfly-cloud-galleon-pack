/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wildfly.cloud.test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import org.junit.runner.RunWith;
import org.junit.Test;
import javax.inject.Inject;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.dmr.ModelNode;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.wildfly.core.testrunner.ManagementClient;
import org.wildfly.core.testrunner.WildflyTestRunner;

@RunWith(WildflyTestRunner.class)
public class SimpleTestCase {

    private static final long RELOAD_WAITING_TIME = 10000;

    @Inject
    private ManagementClient managementClient;

    @Test
    public void test() throws Exception {
        ModelControllerClient client = managementClient.getControllerClient();
        // Wait for the server to be in ready state
        long timeout = RELOAD_WAITING_TIME;
        int freq = 500;
        while(timeout > 0) {
            if (managementClient.isServerInNormalMode() && managementClient.isServerInRunningState()) {
                break;
            }
            timeout -= freq;
            Thread.sleep(freq);
        }
        if (!managementClient.isServerInNormalMode() && !managementClient.isServerInRunningState()) {
            throw new Exception("Server never reached the READY state");
        }
        final ModelNode operation = new ModelNode();
        operation.get("operation").set("read-resource");
        operation.get("address").set("/subsystem=jaxrs");
        ModelNode result = client.execute(operation);
        assertEquals(result.asString(), "success", result.get("outcome").asString());
        // Check exclusions.
        String excludedPaths = System.getProperty("test.excluded.paths");
        if (excludedPaths != null) {
            System.out.println("Some paths are excluded " + excludedPaths);
            Path install = Paths.get(System.getProperty("jboss.home"));
            String[] paths = excludedPaths.split(",");
            for (String p : paths) {
                assertFalse(p, Files.exists(install.resolve(p)));
            }
        }
        String expectedPaths = System.getProperty("test.expected.paths");
        if (expectedPaths != null) {
            System.out.println("Some paths are expected " + expectedPaths);
            Path install = Paths.get(System.getProperty("jboss.home"));
            String[] paths = expectedPaths.split(",");
            for (String p : paths) {
                assertTrue(p, Files.exists(install.resolve(p)));
            }
        }
        boolean bootableJAR = Boolean.getBoolean("wildfly.bootable.jar");
        if (bootableJAR) {
            // Check that the KUBE_PING protocol has been set
            final ModelNode op = new ModelNode();
            op.get("operation").set("read-resource");
            op.get("address").set("/subsystem=jgroups/stack=tcp/protocol=kubernetes.KUBE_PING");
            ModelNode res = client.execute(op);
            assertEquals(res.asString(), "success", res.get("outcome").asString());
            // Check that the System properties have been set
            final ModelNode op2 = new ModelNode();
            op2.get("operation").set("read-attribute");
            op2.get("name").set("system-properties");
            op2.get("address").set("/core-service=platform-mbean/type=runtime");
            ModelNode res2 = client.execute(op2);
            ModelNode props = res2.get("result");
            String nodeName = props.get("jboss.node.name").asString();
            String txId = props.get("jboss.tx.node.id").asString();
            String expected = System.getProperty("test.expected.node.name");
            assertNotNull(expected);
            assertTrue("Node name " + nodeName + ", txid " + txId, expected.equals(nodeName) && expected.equals(txId));
            // Check that the extension has been installed
            Path install = Paths.get(System.getProperty("jboss.home"));
            Path configurators = install.resolve("modules/system/layers/base/org/wildfly/bootable-jar/main/bootable-configurators/bootable-configurators.properties");
            assertTrue(Files.exists(configurators));
            Properties p = new Properties();
            try (InputStream in = configurators.toFile().toURI().toURL().openStream()) {
                p.load(in);
                String module = p.getProperty("cloud-module");
                assertNotNull(module);
                Path modulePath = install.resolve("modules/system/layers/base/" + module.replaceAll("\\.", "/"));
                assertTrue(Files.exists(modulePath));
            }
        }
    }
}
