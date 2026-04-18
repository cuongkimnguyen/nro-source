@echo off
cd /d "C:\Users\Administrator\Downloads\ngocrongkame\NROKAME"
set "JAVA_HOME=C:\Program Files\Java\jdk-19"
call .\mvnw.cmd -Dmaven.ext.class.path="C:\Program Files\NetBeans-25\netbeans\java\maven-nblib\netbeans-eventspy.jar" --no-transfer-progress clean install