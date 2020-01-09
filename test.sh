#!/bin/sh -e

jar_base=org.apache.sling.api-2.22.1-SNAPSHOT.jar

function log {

    echo "|-----------------------------|"
    echo "| ${@}"
    echo "|-----------------------------|"

}

function run_build {
  log "Building jar #${1}"
  rm -rf ${1}-${jar_base} ${1}
  mkdir ${1}
  mvn -q clean install -Drat.skip=true
  mv target/${jar_base} ${1}-${jar_base}
  pushd ${1}
  jar xf ../${1}-${jar_base}
  popd
}

run_build 1
run_build 2

log "Generating diff"

diff -ur 1 2

if [ $? -eq 0 ];
    log "SUCCESS"
else
    log "FAILURE, please check above diff"
fi
