#!/bin/bash
set -o errexit -o nounset -o pipefail
cd "`dirname $0`/.."

clojure -M:dev:test -m user