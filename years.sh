#!/bin/sh

grep -L -r 2011-`date +%Y` --exclude-dir ".git" --exclude ".*" --exclude-dir "est" --exclude "*.yml" --exclude "*.md" --exclude-dir "target" --exclude-dir "it" --exclude-dir ".idea" --exclude "*.iml" . | egrep -v "(qulice-pmd/src/test/resources|qulice-checkstyle/src/test/resources|qulice-xml/src/test/resources|src/site/resources/CNAME|qulice-codenarc/src/main/resources/com/qulice/codenarc/rules.txt|qulice-gradle-plugin/src/main/resources/META-INF/gradle-plugins/qulice-plugin.properties|years.sh)" || exit 0 && echo "Files above have wrong years in copyrights" && exit 1

