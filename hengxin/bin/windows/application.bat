set MIPA_HOME=.
set MIPA_CLASSPATH=%MIPA_HOME%\build\classes\;%CLASSPATH%
cd ..\..
java -cp "%MIPA_CLASSPATH%" net.sourceforge.mipa.Application predicate_scp.xml 1
