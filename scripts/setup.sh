#!/bin/bash

cd app/src/main/resources || exit
# Replace environment variables in property files
envsubst <application.yml.example> application.yml
cd ../../../../
cd auth-server/src/main/resources || exit
# Replace environment variables in property files
envsubst <application.yml.example> application.yml
