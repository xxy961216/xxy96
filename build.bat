@echo off
 
set java7dir=c:\_SOFTWARE\jdk1.7.0_95\
set javac=%java7dir%\bin\javac.exe
set classpath="."

echo *** Compiling source files ***
call %javac% -classpath %classpath% *.java
