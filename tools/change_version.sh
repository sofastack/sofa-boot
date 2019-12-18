#!/bin/bash
shopt -s expand_aliases
if [ ! -n "$1" ] ;then
	echo "Please input a version as first argument, e.g., 3.2.2"
 	exit 1
else
  	echo "Version to update is $1"
fi

currentVersion=`sed -n '/<project /,/<packaging/p' pom.xml | grep version | cut -d '>' -f2 | cut -d '<' -f1`
echo "Current version is $currentVersion"

if [ `uname` == "Darwin" ] ;then
 	alias sed='sed -i ""'
else
 	alias sed='sed -i'
fi

echo "Change version in root pom.xml ===>"
sed "/<project /,/<packaging/ s/<version>.*<\/version>/<version>$1<\/version>/" pom.xml

echo "Change version in subproject pom ===>"
for filename in $(find ./sofa-boot-project ! -path '*/sofaboot-dependencies/*' -name "pom.xml"); do
    echo "Dealing with sofa-boot-project${filename:1}"
    sed "/<parent>/,/<\/parent>/ s/<version>.*<\/version>/<version>$1<\/version>/" $filename
done

echo "Change version in gradle.properties"
for filename in $(find . -name "gradle.properties" -mindepth 3); do
    echo "Dealing with $filename"
    sed "s/sofaVersion=.*/sofaVersion=$1/g" $filename
done
