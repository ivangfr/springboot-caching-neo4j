#!/usr/bin/env bash

#if [ "$1" = "native" ];
#then
#  ./mvnw clean spring-boot:build-image --projects restaurant-api -DskipTests
#else
  ./mvnw clean compile jib:dockerBuild --projects restaurant-api
#fi
