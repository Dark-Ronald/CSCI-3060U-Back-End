@echo off
SETLOCAL enabledelayedexpansion
cd /d "%~dp0Script Transaction Sessions"
set back=%cd%
set /A day = 1
for /d %%c in (*) do (
	"%~dp0DailyScript.bat" !day!
	set /A day+=1
	cd %back%
)
cd "%~dp0"
ENDLOCAL