#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"

export PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD=true

echo "[1/3] compile ..."
mvn -q clean test-compile -DskipTests

echo "[2/3] copy deps ..."
mvn -q dependency:copy-dependencies \
       -DincludeScope=compile \
       -DincludeScope=test \
       -DoutputDirectory=target/dependency

CP="target/classes:target/test-classes"
for j in target/dependency/*.jar; do CP="$CP:$j"; done

echo "[3/3] run TestRunner $1 ..."
java -ea -cp "$CP" api.tests.TestRunner "${1:-}"