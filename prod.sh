#!/bin/sh
./gradlew web:bootJar

scp web/build/libs/web-0.0.1-SNAPSHOT.jar root@64.188.16.34:/root/autossav/server/auth/auth.jar
ssh root@64.188.16.34 << remotessh
cd /root/autossav/server/auth
docker stop autossav_auth
docker run --rm -d -it --name autossav_auth --network net -p 9980:8087 -v /root/autossav/server/auth:/autossav_auth -w /autossav_auth openjdk java -jar auth.jar
exit
remotessh