#!/usr/bin/env bash

ROOT_DIR=$(cd `dirname  $0 `  && pwd )

cd $ROOT_DIR/FlightReservationClient/
mvn clean verify exec:java