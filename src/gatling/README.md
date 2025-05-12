# Load testing with Gatling

[Gatling] it's a framework for running load tests. It works by running what they call scenarios. We
have created only one scenario which tests the Standard Flow. This code is based in two projects,
the [Gatling Kotlin demo][Gatling-Kotlin] and a [Gatling Keycloak][Gatling-Keycloak] we found on the
interwebs.

## How to run it

You run it by executing:

```shell
./gradlew :gatlingRun 
```

### Setup

#### WAF

You'll need to either disable the WAF rate limit or make it very lax. Otherwise, you're going to be
rate limited very soon.

### Create users

You'll need to create the Keycloak users to do the load testing. By default, the script expects the
users to be called `load-test-0`, `load-test-1` and so on. You can change the prefix in the script
configuration.

The script expects these users to be identified by the password `user`, this is hard coded in the
script, so if you want to change that, you'll have to modify the code.

It's worth mentioning that you should not set the flag that forces a user to change their password
when creating the user in Keycloak, otherwise the script will fail.

It's also important to set the `keycloak.users` configuration setting to a number equal or smaller
to the number of users you have created.

### Create clients

You'll need to create the Keycloak clients to do the load testing. By default, the script expects
the clients to be called `load-app-0`, `load-app-1` and so on. You can change the prefix in the
script configuration. Once the clients are created copy their "Client Secret", from the 
"Credentials" tab, in the [configuration file][application.conf].

When creating the clients, look in the "Advanced" tab, in the "Authentication flow overrides"
section, and make sure that the "Session counter limit" is not set, otherwise the script will fail
once the max sessions is reached.

In the "Advanced" tab as well, in the "Advanced settings" section, make sure the "Proof Key for Code
Exchange Code Challenge Method" property is either not set or set to `S256`, otherwise the PKCE will
fail.

### Configuration

You can configure the load script in the [application.conf] file.

The most important settings, are `concurrent-users` and `duration`. The more `concurrent-users` the
more load will the Keycloak server get. `duration`, as the name suggests, will define how long the
test will keep running.

There's a set of settings that are not configurable other than changing the code that you might want
to modify: the open workload ones. You can find them at the `openWorkload` method. We copied the
workload from the [Gatling documentation][Gatling-Injection] and all the tests we did were with the
values that there are now. The configurable setting `open-workload` determines if an open or a
closed workload is used. Gatling recommends to use an open model in our case:

> Don’t reason in terms of concurrent users if your system can’t push excess traffic into a queue.

> If you’re using a closed workload model in your load tests while your system actually is an 
> open one, your test is broken, and you’re testing some different imaginary behavior. In such 
> case, when the system under test starts to have some trouble, response times will increase, 
> journey time will become longer, so number of concurrent users will increase and the virtual 
> users injection will slow down to match the imaginary cap you’ve set.

### Debugging

As far as we know, there's only one way to debug (other than logging / println) Gatling, increasing
the Gatling logging level. You can change it in the [logback-test] file. In the max logging level,
`TRACE`, all HTTP requests are shown. There's more info about how it behaves in that file.

## TODO

We never ended up implementing the session log out for the clients, only for the users. That means
that after running the tests a lot of sessions will remain active. You can log out all of them going
user by user in the Keycloak Admin UI.

[Gatling]: https://gatling.io/
[Gatling-Kotlin]: https://github.com/gatling/gatling-gradle-plugin-demo-kotlin
[Gatling-Keycloak]: https://github.com/lgraf/keycloak-gatling
[application.conf]: ./resources/application.conf
[Gatling-Injection]: https://docs.gatling.io/reference/script/core/injection/#open-model
[logback-test]: ./resources/logback-test.xml
