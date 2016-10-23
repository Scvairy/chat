@echo off

rem IP address where Chat Server is visible
set hostname=10.0.0.1

rem internal name of the server - same as Openfire setting
set serverName=10.0.0.1
set username=admin
set password=admin


@start java -Dserver.host=%hostname%   -Dserver.hostname=%serverName%  -Dusername=%username% -Dpassword=%password% -Dconsole.encoding=CP866 -jar chat-client.jar
