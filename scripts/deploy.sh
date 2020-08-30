#!/bin/bash

set -e
ssh-keyscan -H $IP >>~/.ssh/known_hosts
scp target/api#scholarx.war $USER_NAME@$IP:$DEPLOY_PATH
