<img src="http://img.qulice.com/logo-big.png" width="200px" height="55px"/>

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
      <version>0.3</version>
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
Qulice can't catch them, that's why this wiki pages...
