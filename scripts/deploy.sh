#!/bin/bash

set -e
ssh-keyscan -H $IP >>~/.ssh/known_hosts
scp /target/app.jar $USER_NAME@$IP:$DEPLOY_PATH
