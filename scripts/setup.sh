#!/bin/bash

cd src/main/resources || exit
# Replace environment variables in property files
envsubst <application.yml.example> application.yml
