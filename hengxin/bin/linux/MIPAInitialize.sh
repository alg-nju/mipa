#! /bin/sh

MIPA_HOME="."
MIPA_CLASSPATH="$MIPA_HOME/build/classes/:$CLASSPATH"
cd ../..
java -cp "$MIPA_CLASSPATH" net.sourceforge.mipa.MIPAInitialize
