
echo setting PATH, JAVA HOME
export PATH=/bin:/usr/bin:/usr/local/bin:/usr/local/sbin:/usr/ccs/bin:/home/nightly/bin

echo $HOME

export BUILD_HOME="/home/nightly"

export JAVA_HOME=/home/nightly/jdk
export JAVAC=${JAVA_HOME}/bin/javac

export MAVEN_OPTS=-Xmx512m
export MAVEN="/home/nightly/maven/bin/maven --nobanner --quiet"
export CVS="/usr/bin/cvs -d :pserver:guest@cvs.mmbase.org:/var/cvs"
export ANT_HOME=/usr/ant
antcommand="/usr/bin/ant"
# Ant sucks incredibly. This classapth should not be necessary, but really, it is (not with ant 1.7)
export CLASSPATH=${BUILD_HOME}/.ant/lib/ant-apache-log4j.jar:${BUILD_HOME}/.ant/lib/log4j-1.2.13.jar


export FILTER="/home/nightly/bin/filterlog"


