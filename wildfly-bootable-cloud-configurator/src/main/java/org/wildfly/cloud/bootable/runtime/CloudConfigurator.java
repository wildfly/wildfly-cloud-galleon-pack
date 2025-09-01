/*
 * Copyright The WildFly Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.wildfly.cloud.bootable.runtime;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import org.wildfly.core.jar.runtime.BootableServerConfigurator;
import org.wildfly.core.jar.runtime.Configuration;
/**
 *
 * @author jdenise
 */
public class CloudConfigurator implements BootableServerConfigurator {

    private static final String OPENSHIFT_HOST_NAME_ENV = "HOSTNAME";
    private static final String JBOSS_NODE_NAME_PROPERTY = "jboss.node.name";
    private static final String JBOSS_TX_NODE_ID_PROPERTY = "jboss.tx.node.id";
    private static final Path TMP_DIR = Paths.get("/tmp");
    private static final Path JBOSS_CONTAINER_BOOTABLE_DIR = TMP_DIR.resolve("wildfly-bootable-jar");
    private static final Path INSTALL_DIR_FILE = JBOSS_CONTAINER_BOOTABLE_DIR.resolve("install-dir");

    @Override
    public Configuration configure(List<String> args, Path installDir) throws Exception {
        System.out.println("Booting with the cloud configurator");
        String hostname = System.getenv(OPENSHIFT_HOST_NAME_ENV);
        List<String> extraArguments = doBoot(args, installDir, hostname);
        // Handle JGroup.
        String passwordEnv = System.getenv("JGROUPS_CLUSTER_PASSWORD");
        List<String> cmds = JGroupsUtil.getProtocolCommands(installDir.resolve("standalone").
                    resolve("configuration").resolve("standalone.xml"), passwordEnv != null);
        return new Configuration(extraArguments, cmds);
    }

    List<String> doBoot(List<String> args, Path installDir, String hostname) throws Exception {
        // Required by Operator
        if (Files.exists(TMP_DIR)) {
            try {
                if (!Files.exists(JBOSS_CONTAINER_BOOTABLE_DIR)) {
                    Files.createDirectory(JBOSS_CONTAINER_BOOTABLE_DIR);
                }
                Files.write(INSTALL_DIR_FILE, installDir.toString().getBytes(), StandardOpenOption.CREATE);
            } catch (IOException ex) {
                System.err.println("Warning, can't generate " + INSTALL_DIR_FILE + ". " + ex);
            }
        }
        return handleCloud(args, installDir, hostname);
    }

    private static List<String> handleCloud(List<String> args, Path installDir, String hostname) throws Exception {

        String nodeName = null;
        for (String arg : args) {
            if (arg.startsWith("-D" + JBOSS_NODE_NAME_PROPERTY + "=")) {
                int eq = arg.indexOf("=");
                nodeName = arg.substring(eq + 1, arg.length());
            }
        }
        List<String> extraArguments = new ArrayList<>();
        if (nodeName == null) {
            boolean setNodeName = true;
            nodeName = System.getProperty(JBOSS_NODE_NAME_PROPERTY);
            if (nodeName == null) {
                nodeName = hostname;
            } else {
                setNodeName = false;
            }
            if (nodeName != null) {
                String txId = trunkTxIdValue(nodeName);
                if (setNodeName) {
                    extraArguments.add("-D" + JBOSS_NODE_NAME_PROPERTY + "=" + nodeName);
                }
                extraArguments.add("-D" + JBOSS_TX_NODE_ID_PROPERTY + "=" + txId);
            }
        } else {
            String txId = trunkTxIdValue(nodeName);
            extraArguments.add("-D" + JBOSS_TX_NODE_ID_PROPERTY + "=" + txId);
        }
        return extraArguments;
    }

    private static String trunkTxIdValue(String value) {
        if (value.length() > 23) {
            String originalValue = value;
            StringBuilder builder = new StringBuilder();
            char[] chars = value.toCharArray();
            for (int i = 1; i <= 23; i++) {
                char c = chars[value.length() - i];
                builder.insert(0, c);
            }
            value = builder.toString();
            System.out.println("The HOSTNAME env variable used to set "
                    + JBOSS_TX_NODE_ID_PROPERTY + " is longer than 23 bytes. "
                    + JBOSS_TX_NODE_ID_PROPERTY + " value was adjusted to 23 bytes long string "
                    + value + " from the original value " + originalValue);
        }
        return value;
    }
}
