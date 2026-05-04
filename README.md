# Checkstyle and PMD in One Maven Plugin

[![EO principles respected here](https://www.elegantobjects.org/badge.svg)](https://www.elegantobjects.org)
[![DevOps By Rultor.com](https://www.rultor.com/b/yegor256/qulice)](https://www.rultor.com/p/yegor256/qulice)
[![We recommend IntelliJ IDEA](https://www.elegantobjects.org/intellij-idea.svg)](https://www.jetbrains.com/idea/)

[![mvn](https://github.com/yegor256/qulice/actions/workflows/mvn.yml/badge.svg?branch=master)](https://github.com/yegor256/qulice/actions/workflows/mvn.yml)
[![PDD status](https://www.0pdd.com/svg?name=yegor256/qulice)](https://www.0pdd.com/p?name=yegor256/qulice)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.qulice/qulice/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.qulice/qulice)
[![codecov](https://codecov.io/gh/yegor256/qulice/branch/master/graph/badge.svg)](https://codecov.io/gh/yegor256/qulice)
[![Hits-of-Code](https://hitsofcode.com/github/yegor256/qulice)](https://hitsofcode.com/view/github/yegor256/qulice)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/yegor256/qulice/blob/master/LICENSE.txt)

Qulice is a static analysis quality control instrument for Java
projects. It combines all the best static analysis instruments
and pre-configure them, including
[Checkstyle](https://checkstyle.sourceforge.io/),
[PMD](https://pmd.github.io/), and
[ErrorProne](https://errorprone.info/).
You don't need to use and configure them individually any more.

ErrorProne runs in a forked `javac` process spawned by Qulice, so no
extra JVM flags are required in your project. Suppress individual checks
with `@SuppressWarnings("CheckName")` (the standard ErrorProne mechanism)
or skip whole paths via an `errorprone:` exclude, e.g.
`<exclude>errorprone:.*/generated/.*</exclude>`.

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
      <version>0.27.6</version>
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

## Architecture

Qulice aggregates [Checkstyle](https://checkstyle.sourceforge.io/),
  [PMD](https://pmd.github.io/), and
  [ErrorProne](https://errorprone.info/) behind a single
  [Maven](https://maven.apache.org/) `verify`-phase goal, `check`.
Unlike running each tool as a separate Maven plugin with its own
  configuration file, Qulice requires no per-tool setup in `pom.xml`.
The bundled rule sets are locked: all adopting projects share the same
  [Checkstyle](https://checkstyle.sourceforge.io/) and
  [PMD](https://pmd.github.io/) configuration.
This differs from [Spotless](https://github.com/diffplug/spotless)
  or the [Checkstyle Maven Plugin][csmp] used standalone, both of
  which allow per-project rule customization.

The [Checkstyle](https://checkstyle.sourceforge.io/) configuration
  (`checks.xml`) and [PMD](https://pmd.github.io/) ruleset
  (`ruleset.xml`) ship as classpath resources inside the Qulice JAR
  and cannot be overridden by the adopting project.
Projects may suppress specific checks only via the `<excludes>`
  parameter; they cannot add or redefine rules.
This trades flexibility for consistency: code review style expectations
  are identical across every repository that uses Qulice.

The `com.qulice.spi` package defines the extension boundary between
  Maven and the linting logic.
`Environment` exposes project structure (base directory, classpath,
  source files, exclude patterns) without leaking Maven internals,
  enabling validators to be unit-tested without a Maven runtime.
`ResourceValidator` accepts a `Collection<File>` and returns
  `Collection<Violation>`; `Validator` consumes the full `Environment`
  for checks that require broader project context.

[Checkstyle](https://checkstyle.sourceforge.io/),
  [PMD](https://pmd.github.io/), and
  [ErrorProne](https://errorprone.info/) run concurrently inside a
  five-thread `ExecutorService` in `CheckMojo`.
Each validator is submitted as a `Callable<Collection<Violation>>`
  and results are collected via `Future`.
A configurable timeout (default ten minutes, overridable via
  `qulice.check-timeout`) prevents a hung validator from blocking
  the build indefinitely.

[ErrorProne](https://errorprone.info/) requires `--add-exports` and
  `--add-opens` JVM flags to access internal `jdk.compiler` APIs.
Rather than injecting those flags into Maven's own JVM (which
  would require a `.mvn/jvm.config` in every adopting project),
  Qulice forks a separate `javac` process via
  [Jaxec](https://github.com/yegor256/jaxec).
The flags apply only to the forked process; Maven's JVM is unchanged.

After parallel linting, five Maven-specific validators run
  sequentially: `PomXpathValidator` checks `pom.xml` against
  user-supplied [XPath](https://www.w3.org/TR/xpath/) expressions;
  `EnforcerValidator` delegates to [maven-enforcer-plugin][mep] to
  verify JDK and Maven versions; `DependenciesValidator` uses
  [maven-dependency-analyzer][mda] to find unused or undeclared
  compile-scope dependencies; `DuplicateFinderValidator` detects
  duplicate classes across classpath JARs; and `SnapshotsValidator`
  rejects SNAPSHOT dependencies in production builds.

Both [Checkstyle](https://checkstyle.sourceforge.io/) and
  [PMD](https://pmd.github.io/) are extended with custom rules.
The `com.qulice.checkstyle` package contains roughly forty custom
  `AbstractCheck` implementations (e.g., `ConstructorsOrderCheck`,
  `IfThenThrowElseCheck`) covering constraints absent from the
  standard [Checkstyle](https://checkstyle.sourceforge.io/) catalog.
The `com.qulice.pmd.rules` package adds custom `AbstractJavaRule`
  implementations (e.g., `UnitTestContainsTooManyAssertsRule`,
  `ProhibitPlainJunitAssertionsRule`) that replace upstream
  [PMD](https://pmd.github.io/) rules with known false-positive
  defects filed in the upstream tracker.

[csmp]: https://maven.apache.org/plugins/maven-checkstyle-plugin/
[mep]: https://maven.apache.org/plugins/maven-enforcer-plugin/
[mda]: https://maven.apache.org/shared/maven-dependency-analyzer/

## How to contribute

Fork repository, make changes, send us a pull request. We will review
your changes and apply them to the `master` branch shortly, provided
they don't violate our quality standards. To avoid frustration, before
sending us your pull request please run full Maven build:

```bash
mvn clean install -Pqulice
```

Keep in mind that JDK 17+ and Maven 3.8+ are the lowest versions you may use.
