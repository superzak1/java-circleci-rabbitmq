# Java CircleCI with RabbitMQ

## Build Status

[![CircleCI](https://circleci.com/gh/revof11/java-circleci-rabbitmq/tree/develop.svg?style=svg)](https://circleci.com/gh/revof11/java-circleci-rabbitmq/tree/develop)

## About

CircleCI ([http://circleci.com](http://circleci.com)) is a powerful online Continuous
Integration (CI) tool that provides a shared CI environment for just about any modern
software project.  With simple management and configuration, CircleCI can automatically
figure out how to build your applications in many cases.  However, in some instances
you need to run some extra configuration.
 
A simple Java configuration that shows how you can run on CircleCI when you have a
build dependency with RabbitMQ.  We want to use an environment variable to load the
configuration as is common in a lot of AWS and wrapper deployments and our CI
environment should be the same.

## Instructions

### Basics

1. Setup your basic project (Gradle, Maven, whatever)
2. Create a new `circle.yml` file similar to what is in this project
3. Commit
4. Wait for the build to complete
5. ...
6. Profit

### Running locally

#### Docker

I added a `Dockerfile` in here to help so that you don't need to install RabbitMQ on
your machine (unless you really want to, I prefer Docker to keep my machine nice and
clean).  You can build it yourself or run it from my [Hub](https://hub.docker.com/r/revof11/)
account.  To just run it yourself, just do this:
 
1. Run Docker:  `docker run -ti -p 15672:15672 -p 5672:5672 revof11/java-circleci-rabbitmq`
2. Use the instructions below (I have the IP in there that I get from the Docker terminal)
3. Celebrate

#### Completely Manual

If you want to run this locally, just make sure you have Java 8 and a RabbitMQ
installed.  Then you can just run the application from the command line using the
basic Maven build commands and passing in an override that we allow if you don't
want to set an environment variable.

`mvn -U -Denv.AMQP_URL="amqp://testing:testing@192.168.99.100:5672/whatever" clean test`

You can also execute it using the **preferred** method with the actual environment variable:

```
export DS_URL="amqp://testing:testing@192.168.99.100:5672/whatever"
mvn -U clean install
```
