@echo off
java -classpath %CLASSPATH%;golden_0_2_3.jar;. %1

ECHO Run game bytecode (.class)
ECHO Usage: Tutorial2_2 [game_byte_code]
ECHO e.g: Tutorial2_2 Tutorial6
pause