#!/bin/bash

cd src/main/resources || exit
# Replace environment variables in property files
envsubst <application.properties.example >application.properties
