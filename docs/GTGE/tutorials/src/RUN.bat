@echo off
ECHO COMPILES ALL TUTORIALS SOURCE CODES
@echo on

javac -classpath %CLASSPATH%;golden_0_2_3.jar;. *.java

@echo off
ECHO '
ECHO RUN TUTORIALS LAUNCHER
@echo on

java -classpath %CLASSPATH%;golden_0_2_3.jar;. Launcher

pause