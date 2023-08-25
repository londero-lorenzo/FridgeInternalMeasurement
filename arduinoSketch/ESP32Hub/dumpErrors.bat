@echo off

if NOT "%~1"=="" goto SETTING_VIA_ARG

goto SETTING_VIA_PROMPT


:SETTING_VIA_ARG
set "backtrace=%~1"
goto DUMP

:SETTING_VIA_PROMPT
set /p backtrace= "Enter the backtrace sequence: "
goto DUMP


:DUMP
echo Dumping error lines:
echo 
echo
C:\Users\londe\.platformio\packages\toolchain-xtensa-esp32\bin\xtensa-esp32-elf-addr2line.exe -fe D:\Programming\FridgeInternalMeasurement\arduinoSketch\ESP32Hub\.pio\build\esp32doit-devkit-v1\firmware.elf %backtrace%
