#!/bin/sh

# IP address where Chat Server is visible
hostname=10.0.0.1
# internal name of the server - same as Openfire setting
serverName=10.0.0.1
username=admin
password=admin


java -Dserver.host=$hostname  -Dserver.hostname=$serverName -Dusername=$username -Dpassword=$password -Dconsole.encoding=UTF-8 -jar chat-client.jar
