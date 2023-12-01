#!/bin/bash
# check-config-server-started.sh

apt-get update -y

yes | apt-get install curl

curlResult=$(curl --location --silent --request GET -o /dev/null -I -w "%{http_code}" 'http://config-server:8888/actuator/health')

echo "result status code:" "$curlResult"

while [[ ! $curlResult == "200" ]]; do
  >&2 echo "Config server is not up yet!" "$curlResult"
  sleep 2
  curlResult=$(curl --location --silent --request GET -o /dev/null -I -w "%{http_code}" 'http://config-server:8888/actuator/health')
done

echo "result status code:" "$curlResult"
