@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  mibs-cabinet-renew startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Add default JVM options here. You can also use JAVA_OPTS and MIBS_CABINET_RENEW_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto init

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto init

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:init
@rem Get command-line arguments, handling Windows variants

if not "%OS%" == "Windows_NT" goto win9xME_args

:win9xME_args
@rem Slurp the command line arguments.
set CMD_LINE_ARGS=
set _SKIP=2

:win9xME_args_slurp
if "x%~1" == "x" goto execute

set CMD_LINE_ARGS=%*

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\mibs-cabinet-renew.jar;%APP_HOME%\lib\dcm4che-hl7-5.13.0.jar;%APP_HOME%\lib\dcm4che-imageio-5.13.0.jar;%APP_HOME%\lib\dcm4che-tool-deidentify-5.13.0.jar;%APP_HOME%\lib\dcm4che-tool-json2index-5.13.0.jar;%APP_HOME%\lib\dcm4che-tool-jpg2dcm-5.13.0.jar;%APP_HOME%\lib\dcm4che-core-5.13.0.jar;%APP_HOME%\lib\dcm4che-tool-ianscp-5.13.0.jar;%APP_HOME%\lib\dcm4che-tool-syslog-5.13.0.jar;%APP_HOME%\lib\dcm4che-deident-5.13.0.jar;%APP_HOME%\lib\weasis-opencv-core-3.0.0-RC1.jar;%APP_HOME%\lib\dcm4che-imageio-opencv-5.13.0.jar;%APP_HOME%\lib\dcm4che-audit-5.13.0.jar;%APP_HOME%\lib\dcm4che-tool-mppsscp-5.13.0.jar;%APP_HOME%\lib\dcm4che-net-audit-5.13.0.jar;%APP_HOME%\lib\dcm4che-tool-dcmvalidate-5.13.0.jar;%APP_HOME%\lib\dcm4che-tool-json2rst-5.13.0.jar;%APP_HOME%\lib\dcm4che-image-5.13.0.jar;%APP_HOME%\lib\dcm4che-tool-ianscu-5.13.0.jar;%APP_HOME%\lib\dcm4che-json-5.13.0.jar;%APP_HOME%\lib\slf4j-api-1.7.5.jar;%APP_HOME%\lib\dcm4che-tool-hl7pix-5.13.0.jar;%APP_HOME%\lib\dcm4che-conf-api-hl7-5.13.0.jar;%APP_HOME%\lib\dcm4che-emf-5.13.0.jar;%APP_HOME%\lib\dcm4che-tool-stowrs-5.13.0.jar;%APP_HOME%\lib\dcm4che-tool-dcmldap-5.13.0.jar;%APP_HOME%\lib\dcm4che-tool-dcmdir-5.13.0.jar;%APP_HOME%\lib\dcm4che-tool-storescu-5.13.0.jar;%APP_HOME%\lib\dcm4che-tool-emf2sf-5.13.0.jar;%APP_HOME%\lib\dcm4che-conf-ldap-imageio-5.13.0.jar;%APP_HOME%\lib\dcm4che-tool-hl7rcv-5.13.0.jar;%APP_HOME%\lib\commons-cli-1.2.jar;%APP_HOME%\lib\dcm4che-conf-ldap-5.13.0.jar;%APP_HOME%\lib\dcm4che-tool-xml2hl7-5.13.0.jar;%APP_HOME%\lib\dcm4che-tool-getscu-5.13.0.jar;%APP_HOME%\lib\dcm4che-tool-common-5.13.0.jar;%APP_HOME%\lib\dcm4che-net-hl7-5.13.0.jar;%APP_HOME%\lib\dcm4che-tool-findscu-5.13.0.jar;%APP_HOME%\lib\dcm4che-tool-dcm2dcm-5.13.0.jar;%APP_HOME%\lib\jai_imageio-1.2-pre-dr-b04.jar;%APP_HOME%\lib\dcm4che-tool-swappxdata-5.13.0.jar;%APP_HOME%\lib\dcm4che-tool-stgcmtscu-5.13.0.jar;%APP_HOME%\lib\dcm4che-tool-json2dcm-5.13.0.jar;%APP_HOME%\lib\dcm4che-tool-mppsscu-5.13.0.jar;%APP_HOME%\lib\dcm4che-soundex-5.13.0.jar;%APP_HOME%\lib\slf4j-log4j12-1.7.5.jar;%APP_HOME%\lib\dcm4che-tool-storescp-5.13.0.jar;%APP_HOME%\lib\dcm4che-conf-api-5.13.0.jar;%APP_HOME%\lib\dcm4che-tool-hl72xml-5.13.0.jar;%APP_HOME%\lib\dcm4che-tool-dcm2json-5.13.0.jar;%APP_HOME%\lib\dcm4che-imageio-rle-5.13.0.jar;%APP_HOME%\lib\dcm4che-tool-mkkos-5.13.0.jar;%APP_HOME%\lib\dcm4che-conf-ldap-hl7-5.13.0.jar;%APP_HOME%\lib\dcm4che-conf-ldap-audit-5.13.0.jar;%APP_HOME%\lib\dcm4che-tool-movescu-5.13.0.jar;%APP_HOME%\lib\dcm4che-tool-ihe-modality-5.13.0.jar;%APP_HOME%\lib\dcm4che-dcmr-5.13.0.jar;%APP_HOME%\lib\dcm4che-tool-syslogd-5.13.0.jar;%APP_HOME%\lib\dcm4che-tool-dcmqrscp-5.13.0.jar;%APP_HOME%\lib\dcm4che-mime-5.13.0.jar;%APP_HOME%\lib\dcm4che-net-5.13.0.jar;%APP_HOME%\lib\dcm4che-tool-xml2dcm-5.13.0.jar;%APP_HOME%\lib\javax.json-1.0.4.jar;%APP_HOME%\lib\dcm4che-tool-dcmdump-5.13.0.jar;%APP_HOME%\lib\dcm4che-tool-hl7snd-5.13.0.jar;%APP_HOME%\lib\clibwrapper_jiio-1.2-pre-dr-b04.jar;%APP_HOME%\lib\dcm4che-tool-dcm2jpg-5.13.0.jar;%APP_HOME%\lib\log4j-1.2.17.jar;%APP_HOME%\lib\dcm4che-tool-dcm2xml-5.13.0.jar;%APP_HOME%\lib\jcifs-1.3.19.jar;%APP_HOME%\lib\guava-23.0.jar;%APP_HOME%\lib\commons-io-2.6.jar;%APP_HOME%\lib\commons-lang3-3.0.jar;%APP_HOME%\lib\jsr305-1.3.9.jar;%APP_HOME%\lib\error_prone_annotations-2.0.18.jar;%APP_HOME%\lib\j2objc-annotations-1.1.jar;%APP_HOME%\lib\animal-sniffer-annotations-1.14.jar

@rem Execute mibs-cabinet-renew
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %MIBS_CABINET_RENEW_OPTS%  -classpath "%CLASSPATH%" com.mibs.cabinet.DicomHandler %CMD_LINE_ARGS%

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable MIBS_CABINET_RENEW_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%MIBS_CABINET_RENEW_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
