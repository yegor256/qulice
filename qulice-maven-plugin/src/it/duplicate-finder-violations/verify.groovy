/**
 *
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

def log = new File(basedir, 'build.log')
assert log.text.contains('Found duplicate and different classes in ' +
    '[org.apache.xmlgraphics:batik-ext:1.7, xml-apis:xml-apis:1.4.01]')
