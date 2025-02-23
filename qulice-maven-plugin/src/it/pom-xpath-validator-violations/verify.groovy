/**
 *
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

def log = new File(basedir, 'build.log')
assert !log.text.contains('pom.xml don\'t match the xpath query [/pom:project/pom:build/pom:plugins/' +
    'pom:plugin[pom:artifactId=\'qulice-maven-plugin\']/pom:artifactId/text()]')
assert log.text.contains('pom.xml don\'t match the xpath query [/pom:project/pom:dependencies/' +
    'pom:dependency[pom:artifactId=\'commons-io\']/pom:version[.=\'1.2.5\']/text()]')
