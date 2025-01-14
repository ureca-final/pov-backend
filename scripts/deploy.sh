#!/bin/bash

HOME=/home/ubuntu
REPOSITORY=pov-backend

cd $HOME/$REPOSITORY

echo "> Git Pull"
git pull

echo "> 프로젝트 Build 시작"
./gradlew build

echo "> HOME 에 Build 파일 복사"
sudo cp $HOME/$REPOSITORY/build/libs/*.jar $HOME/

echo "> 현재 구동 중인 애플리케이션 pid 확인"
CURRENT_PID=$(lsof -t -i :8080)

echo "> 현재 구동 중인 애플리케이션 pid: $CURRENT_PID"

if [ -z "$CURRENT_PID" ]; then
  echo "> 현재 구동 중인 애플리케이션이 없습니다."
else
  echo "> kill -15 $CURRENT_PID"
  sudo kill -15 $CURRENT_PID
  sleep 5
fi

echo "> 애플리케이션 배포 시작"
JAR_NAME=$(ls -tr $HOME/ | grep jar | tail -n 1)

echo "> JAR Name: $JAR_NAME"

# 애플리케이션 실행
nohup java -jar $HOME/$JAR_NAME > $HOME/nohup.out 2>&1 &