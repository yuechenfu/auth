#!/bin/sh
./gradlew web:bootJar

scp web/build/libs/web-0.0.1-SNAPSHOT.jar root@172.168.2.16:/root/freitx/server/auth/auth.jar
ssh root@172.168.2.16 << remotessh
cd /root/freitx/server/auth
docker stop freitx_auth
docker run --privileged --rm -d -it --name freitx_auth --network mynetwork -p 9880:9880 -v /root/freitx/server/auth:/freitx_auth -w /freitx_auth openjdk java -jar auth.jar
exit
remotessh
