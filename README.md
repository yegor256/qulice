<img src="http://img.qulice.com/logo.svg" width="200px" height="55px"/>

[![Made By Teamed.io](http://img.teamed.io/btn.svg)](http://www.teamed.io)
[![DevOps By Rultor.com](http://www.rultor.com/b/teamed/qulice)](http://www.rultor.com/p/teamed/qulice)

[![Build Status](https://travis-ci.org/teamed/qulice.svg?branch=master)](https://travis-ci.org/teamed/qulice)
[![Build status](https://ci.appveyor.com/api/projects/status/k8vw7rjdq06olx3b/branch/master?svg=true)](https://ci.appveyor.com/project/yegor256/qulice/branch/master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.qulice/qulice/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.qulice/qulice)

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

Read this short summary of [typical mistakes](https://github.com/tpc2/qulice/wiki/mistakes)
you may encounter in your project.
Qulice can't catch them, that's why this wiki page...

## How to contribute

Fork repository, make changes, send us a pull request. We will review
your changes and apply them to the `master` branch shortly, provided
they don't violate our quality standards. To avoid frustration, before
sending us your pull request please run full Maven build:

> mvn clean install -Pqulice

Keep in mind that JDK7 is the lowest version you may use.

## Got questions?

If you have questions or general suggestions, don't hesitate to submit
a new [Github issue](https://github.com/tpc2/qulice/issues/new).
