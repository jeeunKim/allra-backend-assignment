#!/bin/bash

# exit on errors
set -e

LOGFILE="/home/ubuntu/deploy.log"
ERRLOG="/home/ubuntu/deploy_err.log"
DEPLOY_PATH="/home/ubuntu/app"

echo ">>> ($(date)) deployment script start" >> "${LOGFILE}"

# Maven artifact (target/*.jar) 찾기
BUILD_JAR=$(ls ${DEPLOY_PATH}/target/*.jar 2>/dev/null || true)

if [ -z "$BUILD_JAR" ]; then
  echo ">>> ERROR: No jar found in ${DEPLOY_PATH}/target" >> "${LOGFILE}"
  exit 1
fi

JAR_NAME=$(basename "$BUILD_JAR")
echo ">>> build 파일명: $JAR_NAME" >> "${LOGFILE}"

# 배포 디렉터리 존재 확인 및 생성
if [ ! -d "${DEPLOY_PATH}" ]; then
  echo ">>> creating deploy directory ${DEPLOY_PATH}" >> "${LOGFILE}"
  mkdir -p "${DEPLOY_PATH}"
  chown ubuntu:ubuntu "${DEPLOY_PATH}" || true
fi

echo ">>> build 파일 복사" >> "${LOGFILE}"
cp -f "$BUILD_JAR" "${DEPLOY_PATH}/"

# 기존 실행중인 동일 JAR 프로세스만 종료 (안전하게)
DEPLOY_JAR_PATH="${DEPLOY_PATH}/${JAR_NAME}"
echo ">>> Looking for running process of ${JAR_NAME}" >> "${LOGFILE}"

# pids of processes that are running this exact jar
PIDS=$(pgrep -f "java .*${JAR_NAME}" || true)

if [ -n "$PIDS" ]; then
  echo ">>> Found running PIDs: $PIDS" >> "${LOGFILE}"
  echo ">>> Sending SIGTERM to running process(es)" >> "${LOGFILE}"
  echo "$PIDS" | xargs -r kill -15
  # give them a moment to stop gracefully
  sleep 5

  # if still alive, force kill
  STILL_ALIVE=$(pgrep -f "java .*${JAR_NAME}" || true)
  if [ -n "$STILL_ALIVE" ]; then
    echo ">>> Force killing remaining PIDs: $STILL_ALIVE" >> "${LOGFILE}"
    echo "$STILL_ALIVE" | xargs -r kill -9
  fi
else
  echo ">>> No running process for ${JAR_NAME} found" >> "${LOGFILE}"
fi

echo ">>> DEPLOY_JAR 배포" >> "${LOGFILE}"
echo ">>> $DEPLOY_JAR_PATH 를 실행합니다" >> "${LOGFILE}"

# run as ubuntu user (ensure permissions) - adjust runas in appspec if needed
chown ubuntu:ubuntu "${DEPLOY_JAR_PATH}" || true
chmod 644 "${DEPLOY_JAR_PATH}" || true

# start application in background, redirect logs
nohup java -jar "${DEPLOY_JAR_PATH}" >> "${LOGFILE}" 2>> "${ERRLOG}" &

echo ">>> ($(date)) deployment script end" >> "${LOGFILE}"
