# Building a WildFly server for the cloud

In order to provision a WildFly server for the cloud, you have first to integrate the [WildFly Maven plugin](https://github.com/wildfly/wildfly-maven-plugin/)
into the `pom.xml` file of your project.

The `package` goal of the plugin allows you to:

* Provision a WildFly server tailored to your needs.
* Incorporate custom content (keystores, properties files, etc).
* Execute CLI scripts.
* Deploy your application.

The generated server is then ready to be installed inside Docker or Podman container image to be deployed in your cluster.

# Provisioning with the cloud feature-pack

The cloud feature-pack is to be provisioned along with WildFly Galleon feature-pack. This is configured in the ``feature-packs`` configuration option 
of the WildFly Maven plugin.

For example:

```xml
<feature-packs>
  <feature-pack>
    <location>org.wildfly:wildfly-galleon-pack:${version.wildfly}</location>
  </feature-pack>
  <feature-pack>
    <location>org.wildfly.cloud:wildfly-cloud-galleon-pack:${version.wildfly.cloud.galleon.pack}</location>
  </feature-pack>
</feature-packs>
```

For a complete plugin configuration see this [example](https://github.com/wildfly/wildfly-s2i/blob/main/test/test-app/pom.xml).

# Building a WildFly Image

## Building an application image from a local execution of the WildFly Maven plugin

Call `mvn clean package wildfly:image` to build an image.

## Building an image in the cluster thanks to the WildFly S2I Builder image and Helm charts

Using [WildFly Helm charts](https://github.com/wildfly/wildfly-charts) is the simplest way to initiate the build of your application image in the cloud. 
This [example](https://github.com/wildfly/wildfly-charts/tree/main/examples/microprofile-config) can shows how to initiate an S2I build.

# Specifics of the `wildfly-cloud-galleon-pack`

When using the cloud galleon feature-pack, the following content will get provisioned:
* Server [startup scripts](launch.md).
* [Automatic adjustment](layers.md) of WildFly Galleon layers to cope with the cloud execution environment.
* Automatic provisioning of the health subsystem allowing for server state monitoring (Liveness and Readiness probes).
* Automatic routing of server logs to the console.

# WildFly Bootable JAR

The cloud feature-pack can be provisioned when building a bootable JAR. 
To enable bootable JAR packaging, use the `<bootable-jar>true</bootable-jar>` WildFly Maven plugin option.

When using the cloud galleon feature-pack to build a bootable JAR, the following content will get provisioned:

* Server [startup scripts](launch.md) although provisioned **are ignored**.
They can be explictely excluded from the provisioned content. To do so, configure the cloud feature-pack in the WildFly Maven plugin to exclude them:
```
...
<feature-pack>
  <groupId>org.wildfly.cloud</groupId>
  <artifactId>wildfly-cloud-galleon-pack</artifactId>
  <version>${wildfly.cloud.version}</version>
  <excludedPackages>
    <package>org.wildfly.cloud.launch.scripts</package>
  </excludedPackages>
</feature-pack>
...
```
* [Automatic adjustment](layers.md) of WildFly Galleon layers to cope with the cloud execution environment.
* Automatic provisioning of the health subsystem allowing for server state monitoring (Liveness and Readiness probes).
* [Boot time cloud specific configuration](bootable.md).
* Automatic routing of server logs to the console.

Note: When provisioning a server installation you can optionally exclude the JBoss Modules module that implements the bootable boot time logic. 
To do so, configure the cloud feature-pack in the WildFly Maven plugin to exclude it:
```
...
<feature-pack>
  <groupId>org.wildfly.cloud</groupId>
  <artifactId>wildfly-cloud-galleon-pack</artifactId>
  <version>${wildfly.cloud.version}</version>
  <excludedPackages>
    <package>org.wildfly.cloud.bootable.runtime</package>
  </excludedPackages>
</feature-pack>
...
```

## Building a WildFly Bootable JAR Image

## Building an application image from a local execution of the WildFly Maven plugin

The plugin has been configured with `<bootable-jar>true</bootable-jar>` option. 
Call `mvn clean package wildfly:image` to build an image.

## Building an image in the cluster thanks to the OpenJDK S2I Builder image and Helm charts

Using [WildFly Helm charts](https://github.com/wildfly/wildfly-charts) is the simplest way to initiate the build of your application image in the cloud. 
This [example](https://github.com/wildfly/wildfly-charts/tree/main/examples/microprofile-config) can be run with WildFly bootable JAR packaging.
