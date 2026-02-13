/*
 * Copyright The WildFly Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.wildfly.cloud.bootable.runtime;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author jdenise
 */
public class CloudConfiguratorTestCase {

    @Test
    public void test() throws Exception {
        CloudConfigurator extension = new CloudConfigurator();
        Path p = Files.createTempDirectory("cloud-ext-test");
        p.toFile().deleteOnExit();
        List<String> args = extension.doBoot(Collections.emptyList(), p, "abcdefghijklmnopqrstvuwxyz");
        assertTrue(args.size() == 2);
        assertTrue(args.toString(), "-Djboss.node.name=abcdefghijklmnopqrstvuwxyz".equals(args.get(0)));
        assertTrue(args.toString(), "-Djboss.tx.node.id=defghijklmnopqrstvuwxyz".equals(args.get(1)));
    }

    @Test
    public void test2() throws Exception {
        CloudConfigurator extension = new CloudConfigurator();
        Path p = Files.createTempDirectory("cloud-ext-test");
        p.toFile().deleteOnExit();
        List<String> args = extension.doBoot(Collections.emptyList(), p, "abcdefghijklmnopqrstvuw");
        assertTrue(args.size() == 2);
        assertTrue(args.toString(), "-Djboss.node.name=abcdefghijklmnopqrstvuw".equals(args.get(0)));
        assertTrue(args.toString(), "-Djboss.tx.node.id=abcdefghijklmnopqrstvuw".equals(args.get(1)));
    }

    @Test
    public void test3() throws Exception {
        CloudConfigurator extension = new CloudConfigurator();
        Path p = Files.createTempDirectory("cloud-ext-test");
        p.toFile().deleteOnExit();
        List<String> args = extension.doBoot(Collections.emptyList(), p, "a");
        assertTrue(args.size() == 2);
        assertTrue(args.toString(), "-Djboss.node.name=a".equals(args.get(0)));
        assertTrue(args.toString(), "-Djboss.tx.node.id=a".equals(args.get(1)));
    }

    @Test
    public void test4() throws Exception {
        CloudConfigurator extension = new CloudConfigurator();
        Path p = Files.createTempDirectory("cloud-ext-test");
        p.toFile().deleteOnExit();
        List<String> args = new ArrayList<>();
        args.add("-Djboss.node.name=foo");
        List<String> args2 = extension.doBoot(args, p, "abcdef");
        assertTrue(args2.size() == 1);
        assertTrue(args2.toString(), "-Djboss.tx.node.id=foo".equals(args2.get(0)));
    }

    @Test
    public void test5() throws Exception {
        CloudConfigurator extension = new CloudConfigurator();
        Path p = Files.createTempDirectory("cloud-ext-test");
        p.toFile().deleteOnExit();
        System.setProperty("jboss.node.name", "foo");
        try {
            List<String> args = extension.doBoot(Collections.emptyList(), p, "abcdef");
            assertTrue(args.size() == 1);
            assertTrue(args.toString(), "-Djboss.tx.node.id=foo".equals(args.get(0)));
        } finally {
            System.clearProperty("jboss.node.name");
        }
    }

    @Test
    public void test6() throws Exception {
        CloudConfigurator extension = new CloudConfigurator();
        Path p = Files.createTempDirectory("cloud-ext-test");
        p.toFile().deleteOnExit();
        List<String> args = new ArrayList<>();
        args.add("-Dfoo.bar=fromtest");
        List<String> args2 = extension.doBoot(args, p, "abcdef");
        assertTrue(args2.size() == 2);
        assertTrue(args2.toString(), "-Djboss.node.name=abcdef".equals(args2.get(0)));
        assertTrue(args2.toString(), "-Djboss.tx.node.id=abcdef".equals(args2.get(1)));
    }
}
