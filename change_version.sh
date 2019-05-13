#!/bin/bash
shopt -s expand_aliases
if [ ! -n "$1" ] ;then
	echo "Please enter a version"
 	exit 1
else
  	echo "The updated version is $1 !"
fi

currentVersion=`sed -n '/<project /,/<packaging/p' pom.xml | grep version | cut -d '>' -f2 | cut -d '<' -f1`
echo "The current version is $currentVersion"

if [ `uname` == "Darwin" ] ;then
 	echo "This is OS X"
 	alias sed='sed -i ""'
else
 	echo "This is Linux"
 	alias sed='sed -i'
fi

echo "Change version in root pom.xml ===>"
sed "/<project /,/<packaging/ s/<version>.*<\/version>/<version>$1<\/version>/" pom.xml

echo "Change version in subproject pom ===>"
for filename in `find . -name "pom.xml" -mindepth 2`;do
    echo "Deal with $filename"
    sed "/<parent>/,/<\/parent>/ s/<version>.*<\/version>/<version>$1<\/version>/" $filename
done

echo "Change version in gradle.properties"
for filename in `find . -name "gradle.properties" -mindepth 3`;do
    echo "Deal with $filename"
    sed "s/sofaVersion=.*/sofaVersion=$1/g" $filename
done

