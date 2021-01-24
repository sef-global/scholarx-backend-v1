#!/bin/bash

set -e
ssh-keyscan -H $IP >>~/.ssh/known_hosts
scp /app/target/api#scholarx.jar $USER_NAME@$IP:$DEPLOY_PATH
scp /auth-server/target/auth.jar $USER_NAME@$IP:$DEPLOY_PATH
