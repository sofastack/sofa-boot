JAVA_VERSION=`java -version 2>&1 |awk 'NR==1{ gsub(/"/,""); print $3 }'`

if [[ $JAVA_VERSION =~ '1.8' ]]; then
    mvn test
else
    mvn test -Pjdk11
fi

