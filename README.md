# Qulice

![logo](https://www.qulice.com/logo.svg)

[![EO principles respected here](https://www.elegantobjects.org/badge.svg)](https://www.elegantobjects.org)
[![DevOps By Rultor.com](https://www.rultor.com/b/yegor256/qulice)](https://www.rultor.com/p/yegor256/qulice)
[![We recommend IntelliJ IDEA](https://www.elegantobjects.org/intellij-idea.svg)](https://www.jetbrains.com/idea/)

[![mvn](https://github.com/yegor256/qulice/actions/workflows/mvn.yml/badge.svg?branch=master)](https://github.com/yegor256/qulice/actions/workflows/mvn.yml)
[![PDD status](https://www.0pdd.com/svg?name=yegor256/qulice)](https://www.0pdd.com/p?name=yegor256/qulice)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.qulice/qulice/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.qulice/qulice)
[![codebeat badge](https://codebeat.co/badges/9454ea39-1f11-4f6b-b086-ec5a2d658174)](https://codebeat.co/projects/github-com-teamed-qulice)
[![codecov](https://codecov.io/gh/yegor256/qulice/branch/master/graph/badge.svg)](https://codecov.io/gh/yegor256/qulice)
[![Hits-of-Code](https://hitsofcode.com/github/yegor256/qulice)](https://hitsofcode.com/view/github/yegor256/qulice)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/yegor256/qulice/blob/master/LICENSE.txt)

Qulice is a static analysis quality control instrument for Java
projects. It combines all the best static analysis instruments
and pre-configure them, including
[Checkstyle](https://checkstyle.sourceforge.io/) and
[PMD](https://pmd.github.io/).
You don't need to use and configure them individually any more.

## Features

* Pre-configured static analysis tools
* Easy integration with Maven projects
* Comprehensive code quality checks
* Customizable validation rules
* Detailed violation reporting

## Quick Start

Read more at [www.qulice.com](https://www.qulice.com).

Also, read this blog post first:
[_Strict Control of Java Code Quality_](https://www.yegor256.com/2014/08/13/strict-code-quality-control.html).

Just add this plugin to your `pom.xml`:

```xml
<build>
  <plugins>
    <plugin>
      <groupId>com.qulice</groupId>
      <artifactId>qulice-maven-plugin</artifactId>
      <version>0.24.0</version>
      <configuration>
        <license>file:${basedir}/LICENSE.txt</license>
      </configuration>
      <executions>
        <execution>
          <goals>
            <goal>check</goal>
          </goals>
        </execution>
      </executions>
    </plugin>
  </plugins>
</build>
```

Also remember that we support Maven 3.1+.

The path to license has to be declared in the following format:
`file:${basedir}/LICENSE.txt`, it's the default value, one can use any full path
instead of `${basedir}`.

Read this short summary of [typical mistakes](https://github.com/yegor256/qulice/wiki/mistakes)
you may encounter in your project.

In order to download schemas required for XML validation you might need proxy
setup. Maven proxy is not supported, but standard
[JVM proxy](https://docs.oracle.com/javase/8/docs/technotes/guides/net/proxies.html)
works fine. To use it just add `-Dhttp.proxyHost=HOST -Dhttp.proxyPort=PORT`
to your `MAVEN_OPTS` environment variable or to Maven command, e.g.
`mvn clean verify -Dhttp.proxyHost=HOST -Dhttp.proxyPort=PORT`.

## How to contribute

Fork repository, make changes, send us a pull request. We will review
your changes and apply them to the `master` branch shortly, provided
they don't violate our quality standards. To avoid frustration, before
sending us your pull request please run full Maven build:

```bash
mvn clean install -Pqulice
```

Keep in mind that JDK 11+ and Maven 3.8+ are the lowest versions you may use.
