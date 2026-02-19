@echo off
set PATH=C:\msys64\mingw64\bin;%PATH%
setlocal

echo ============================
echo Building MyCompiler (Windows)
echo ============================

REM --- Paths ---
set SRC=src
set BUILD=build
set BIN=bin

REM --- Create folders if missing ---
if not exist %BUILD% mkdir %BUILD%
if not exist %BIN% mkdir %BIN%

REM --- Generate parser with Bison ---
echo [1/2] Running Bison...
bison -d %SRC%\parser\parser.y -o %BUILD%\parser.c
if errorlevel 1 (
    echo Bison failed.
    exit /b 1
)

REM --- Compile with GCC ---
echo [2/2] Compiling...
gcc -Isrc ^
  %BUILD%\parser.c ^
  %SRC%\parser\lexer_adapter.c ^
  %SRC%\lexer\lexer.c ^
  %SRC%\lexer\handlers.c ^
  %SRC%\lexer\sym_handlers.c ^
  %SRC%\utils\error.c ^
  %SRC%\utils\token_printers.c ^
  %SRC%\main.c ^
  -o %BIN%\MyCompiler.exe

if errorlevel 1 (
    echo Compilation failed.
    exit /b 1
)

echo ============================
echo Build successful! 🎉
echo Output: %BIN%\MyCompiler.exe
echo ============================

endlocal
pause
