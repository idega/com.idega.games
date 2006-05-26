@echo off
javac -classpath %CLASSPATH%;golden_0_2_3.jar;. %1

ECHO Compile game source code (.java) into bytecode (.class)
ECHO Usage: Tutorial2_1 [game_source_code]
ECHO e.g: Tutorial2_1 Tutorial6.java
pause