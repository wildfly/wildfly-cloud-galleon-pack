# Server startup

When a WildFly server provisioned with the cloud feature-pack is started, a set of bash scripts are executed in order to adjust 
the server configuration. These bash scripts are controlled by a set of environment variables that this document describes.

When running a WildFly server inside the WildFly S2I runtime and builder images, you can use [these environment variables](https://github.com/jboss-container-images/openjdk/blob/develop/modules/jvm/api/module.yaml) to configure the Java VM.
These environment variables are not provided by the WildFly cloud feature-pack. They can be used with a vanilla WildFly server installed in the image.

WildFly S2I runtime and builder images are exposing a set of [environment variables](https://github.com/wildfly/wildfly-cekit-modules/blob/main/jboss/container/wildfly/run/api/module.yaml) to fine tune the server execution.

# Datasources

Configure datasources subsystem. More documentations can be found in [this documentation](datasources.md).

## Environment variables

[Environment variables.](https://github.com/wildfly/wildfly-cekit-modules/blob/main/jboss/container/wildfly/launch/datasources/module.yaml)

# Deployment Scanner

Controls whether exploded deployment content should be automatically deployed.

## Environment variables

[Environment variables.](https://github.com/wildfly/wildfly-cekit-modules/blob/main/jboss/container/wildfly/launch/deployment-scanner/module.yaml)

# Eclipse MicroProfile Config

Add config maps as source of configuration to the Eclipse MicroProfile subsystem.

## Environment variables

[Environment variables.](https://github.com/wildfly/wildfly-cekit-modules/blob/main/jboss/container/wildfly/launch/mp-config/module.yaml)

# Elytron

Configure elytron subsystem with new security domains and HTTPS configuration.

## Environment variables

[Environment variables.](https://github.com/wildfly/wildfly-cekit-modules/blob/main/jboss/container/wildfly/launch/elytron/module.yaml)

# Elytron OIDC

Secure deployments with the `elytron-oidc-client` subsystem. Supports the Keycloak server provider.

## Environment variables

[Environment variables.](https://github.com/wildfly/wildfly-cekit-modules/blob/main/jboss/container/wildfly/launch/oidc/module.yaml)

# JGroups

Configure JGroups node discovery.

## Environment variables

[Environment variables.](https://github.com/wildfly/wildfly-cekit-modules/blob/main/jboss/container/wildfly/launch/jgroups/module.yaml)

# JSON logging

Enable JSON-formatted logging.

## Environment variables

[Environment variables.](https://github.com/wildfly/wildfly-cekit-modules/blob/main/jboss/container/wildfly/launch/json-logging/module.yaml)

# Logging loggers

Add loggers to the logging subsystem.

## Environment variables

[Environment variables.](https://github.com/wildfly/wildfly-cekit-modules/blob/main/jboss/container/wildfly/launch/logger-category/module.yaml)

# Messaging

Add Topics and Queues to messaging configuration.

## Environment variables

[Environment variables.](https://github.com/wildfly/wildfly-cekit-modules/blob/main/jboss/container/wildfly/launch/messaging/2.0/module.yaml)

# Management interface admin user

Configures the admin user used to access the management interface.

## Environment variables

[Environment variables.](https://github.com/wildfly/wildfly-cekit-modules/blob/main/jboss/container/wildfly/launch/admin/2.0/module.yaml)

# Resource Adapters

Add resource adapters to the `resource-adapters` subsystem.

## Environment variables

[Environment variables.](https://github.com/wildfly/wildfly-cekit-modules/blob/main/jboss/container/wildfly/launch/resource-adapters/module.yaml)

# Server startup extension scripts

Support for ``$JBOSS_HOME/extensions/preconfigure.sh|postconfigure.sh`` scripts. If such scripts are found in 
the server installation, they are called at server startup.

# Socket binding port-offset

Configures the socket binding group port offset.

## Environment variables

[Environment variables.](https://github.com/wildfly/wildfly-cekit-modules/blob/main/jboss/container/wildfly/launch/port-offset/module.yaml)

# StatefulSet Service Name

Allows to set the name of the headless service which belongs to the StatefulSet managing the cluster. 
Used to configure the socket binding client mapping for the EJB client.

## Environment variables

[Environment variables.](https://github.com/wildfly/wildfly-cekit-modules/blob/main/jboss/container/wildfly/launch/statefulset/module.yaml)

# Undertow access log

Configures the undertow subsystem access-log valve and add the logger `org.infinispan.REST_ACCESS_LOG` to the logging subsystem.

## Environment variables

[Environment variables.](https://github.com/wildfly/wildfly-cekit-modules/blob/main/jboss/container/wildfly/launch/access-log-valve/module.yaml)

# Undertow response filters

Configures the response header filters in the undertow server's hosts. Applies to all hosts.

## Environment variables

[Environment variables.](https://github.com/wildfly/wildfly-cekit-modules/blob/main/jboss/container/wildfly/launch/filters/module.yaml)

