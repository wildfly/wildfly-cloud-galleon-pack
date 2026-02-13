# WildFly Bootable JAR specific configuration

When running a WildFly bootable JAR inside the OpenJDK S2I runtime and builder images, 
you can use [these environment variables](https://github.com/jboss-container-images/openjdk/blob/develop/modules/jvm/api/module.yaml) to configure the Java VM.
NOTE: These environment variables are not provided by the WildFly cloud feature-pack. They can be used with a vanilla WildFly server installed in the image.

## Custom configuration for the cloud

* `jboss.node.name` and `jboss.tx.node.id` system properties are set to the hostname. 
If the hostname of the POD running the server is longer than 23 characters, the value is truncated. That is a constraint of the transaction subsystem.

* High Availability, if jgroups subsystem is present, if `dns.DNS_PING` nor `kubernetes.KUBE_PING` protocols are present, the `kubernetes.KUBE_PING` protocol is added.
* High Availability, the environment variable `JGROUPS_CLUSTER_PASSWORD` can be set to use a password to encrypt the jgroups communication.
