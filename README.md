# Napster

[![Build Status](https://travis-ci.org/hzxie/Napster.svg)](https://travis-ci.org/hzxie/Napster)

## Introduction

A simple file sharing computer program base on Java.

This is the homework of *Practice of Software System Development* in [Harbin Institute of Technology](http://www.hit.edu.cn).

## Setup

> NOTE: Java 1.8.0 Update 42 or above is required.

### Build server

You can use following commands to build and run the Napster server:

```
# Build Server
cd server
mvn package -DskipTests

# Run Server
java -jar target/napster.server.jar
```

Similar to Napster server, you can build and run Napster client as follows:

```
# Build Client
cd client
mvn package -DskipTests

# Run Client
java -jar target/napster.client.jar
```

Enjoy!

## Screenshots

<img width="884" alt="Napster Client" src="https://cloud.githubusercontent.com/assets/1730504/15991081/6af6978c-30da-11e6-8c63-24000a484440.png">

## License

This project is open sourced under Apache license.
