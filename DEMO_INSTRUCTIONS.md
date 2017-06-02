# Demo Instructions

This demo illustrates the challenges when changing the log level at runtime for a distributed system.

Questions about changing the log level at runtime are often raised during trainings, as changing it via an environment variable requires to re-stage the application (i.e. downtime).

The demo contains an HTTP endpoint for checking the active log levels and one for setting it. Setting it an runtime, however, will only change it on the given instance of the microservice . By checking the log level again after changing it, you can show that the change wasn't propagated. A mechanism to synchronize the log levels across instances is required.

## Run Locally

Note: you cannot easily illustrate the problem when running locally, as you cannot easily spin up multiple instances of the microservice.

The demo reads the `CF_INSTANCE_INDEX` environment variable, which you need to make available, e.g. from command line:

```shell
CF_INSTANCE_INDEX=0 mvn tomcat7:run
```

The following endpoints are available:

- Check log levels: http://localhost:8080/log-level (GET)
- Set log level: http://localhost:8080/log-level/set/info (GET)

## Run on Cloud Foundry

Deploy the microservice with multiple instances:

```shell
cf push -i 3
```

The following endpoints are available:

- Check log levels: DOMAIN/log-level (GET)
- Set log level: DOMAIN/log-level/set/info (GET)

## Demo Script

1. Run the microservice on CF with multiple instances
1. Open the `/log-level` endpoint multiple times to show that the log level is the same on all instances
1. Set the log level with by opening `/log-level/set/{level}` and point out that the log level was only changed on the given instances
1. Open the `/log-level` endpoint again multiple times to prove that you know have different log levels on the individual instances

## Good to Know

You can send HTTP request to specific instances of your microservice: https://docs.cloudfoundry.org/concepts/http-routing.html#-app-instance-routing-in-http-headers