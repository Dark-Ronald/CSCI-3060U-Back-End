@echo off
SETLOCAL enabledelayedexpansion
cd /d "%~dp0Script Transaction Sessions\Day %1\sessions"
for %%f in (*) do (
	"%~dp0FrontEnd"\FrontEndFileInput.exe "%~dp0current_user_accounts.txt" "%~dp0available items.txt" "%~dp0Daily Transaction Files" < "%cd%\%%f"
)
set toplevel=%~dp0
Rem cd 
java -cp "%~dp0BackEnd\out\production\BackEnd" classes.main "%toplevel%current_user_accounts.txt" "%toplevel%available items.txt" "%toplevel%Daily Transaction Files" "script"

ENDLOCAL