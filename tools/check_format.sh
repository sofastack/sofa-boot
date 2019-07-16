#!/bin/sh

BASEDIR=$(dirname $0)

cd ${BASEDIR}

# make sure git has no un commit files
if [ -n "$(git status --untracked-files=no --porcelain)" ]; then
   echo "Please commit your change before run this shell, un commit files:"
   git status --untracked-files=no --porcelain
   exit -1
fi