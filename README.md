<img src="http://img.qulice.com/logo.svg" width="200px" height="55px"/>

[![Donate via Zerocracy](https://www.0crat.com/contrib-badge/C3T49A35L.svg)](https://www.0crat.com/contrib/C3T49A35L)

[![EO principles respected here](http://www.elegantobjects.org/badge.svg)](http://www.elegantobjects.org)
[![Managed by Zerocracy](https://www.0crat.com/badge/C3T49A35L.svg)](https://www.0crat.com/p/C3T49A35L)
[![DevOps By Rultor.com](http://www.rultor.com/b/teamed/qulice)](http://www.rultor.com/p/teamed/qulice)
[![We recommend IntelliJ IDEA](http://www.elegantobjects.org/intellij-idea.svg)](https://www.jetbrains.com/idea/)

[![Build Status](https://travis-ci.org/teamed/qulice.svg?branch=master)](https://travis-ci.org/teamed/qulice)
[![PDD status](http://www.0pdd.com/svg?name=teamed/qulice)](http://www.0pdd.com/p?name=teamed/qulice)
[![Build status](https://ci.appveyor.com/api/projects/status/k8vw7rjdq06olx3b/branch/master?svg=true)](https://ci.appveyor.com/project/yegor256/qulice/branch/master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.qulice/qulice/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.qulice/qulice)
[![codebeat badge](https://codebeat.co/badges/9454ea39-1f11-4f6b-b086-ec5a2d658174)](https://codebeat.co/projects/github-com-teamed-qulice)
[![Dependencies](https://www.versioneye.com/user/projects/561aa18ea193340f2f001188/badge.svg?style=flat)](https://www.versioneye.com/user/projects/561aa18ea193340f2f001188)

Qulice is a static analysis quality control instrument for Java
projects. It combines all the best static analysis instruments
and pre-configure them. You don't need to use and configure them
individually any more.

Read more at [www.qulice.com](http://www.qulice.com). Also,
read this blog post: [Strict Control of Java Code Quality](http://www.yegor256.com/2014/08/13/strict-code-quality-control.html).

Just add this plugin to your `pom.xml`:

```xml
<build>
  <plugins>
    <plugin>
      <groupId>com.qulice</groupId>
      <artifactId>qulice-maven-plugin</artifactId>
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

and make sure you have the JDK binaries (including the Java compiler `javac`)
accessible from your `PATH` environment variable (e.g. if you have JDK 1.8.0
installed in Windows your PATH should contain something like `C:\Program
Files\Java\jdk1.8.0\bin`).

The path to license has to be declared in the following format:
`file:${basedir}/LICENSE.txt`, it's the default value, one can use any full path
instead of `${basedir}`.

Read this short summary of [typical mistakes](https://github.com/tpc2/qulice/wiki/mistakes)
you may encounter in your project.
Qulice can't catch them, that's why this wiki page...

#### Proxy

In order to download schemas required for XML validation you might need proxy
setup. Maven proxy is not supported, but standard [JVM proxy](https://docs.oracle.com/javase/8/docs/technotes/guides/net/proxies.html)
works fine. To use it just add `-Dhttp.proxyHost=HOST -Dhttp.proxyPort=PORT`
to your `MAVEN_OPTS` environment variable or to Maven command, e.g.
`mvn clean verify -Dhttp.proxyHost=HOST -Dhttp.proxyPort=PORT`.

## How to contribute

Fork repository, make changes, send us a pull request. We will review
your changes and apply them to the `master` branch shortly, provided
they don't violate our quality standards. To avoid frustration, before
sending us your pull request please run full Maven build:

> mvn clean install -Pqulice

Keep in mind that JDK7 and Maven 3.1.0 are the lowest versions you may use.

## Got questions?

If you have questions or general suggestions, don't hesitate to submit
a new [Github issue](https://github.com/tpc2/qulice/issues/new).
