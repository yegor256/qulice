<img src="http://img.qulice.com/logo-big.png" width="200px" height="55px"/>

[![Build Status](https://travis-ci.org/teamed/qulice.svg?branch=master)](https://travis-ci.org/teamed/qulice)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.qulice/qulice/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.qulice/qulice)

Qulice is a static analysis quality control instrument for Java
projects. It combines all the best static analysis instruments
and pre-configure them. You don't need to use and configure them
individually any more.

Read more at [www.qulice.com](http://www.qulice.com).

Just add this plugin to your `pom.xml`:

```xml
<build>
  <plugins>
    <plugin>
      <groupId>com.qulice</groupId>
      <artifactId>qulice-maven-plugin</artifactId>
      <version>0.8</version>
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

Read this short summary of [typical mistakes](https://github.com/tpc2/qulice/wiki/mistakes)
you may encounter in your project.
Qulice can't catch them, that's why this wiki page...

## How to contribute

Fork repository, make changes, send us a pull request. We will review
your changes and apply them to the `master` branch shortly, provided
they don't violate our quality standards. To avoid frustration, before
sending us your pull request please run full Maven build:

> mvn clean install -Pqulice

## Got questions?

If you have questions or general suggestions, don't hesitate to submit
a new [Github issue](https://github.com/tpc2/qulice/issues/new).


[![Bitdeli Badge](https://d2weczhvl823v0.cloudfront.net/tpc2/qulice/trend.png)](https://bitdeli.com/free "Bitdeli Badge")

