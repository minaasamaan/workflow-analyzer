#!/bin/sh
cd $(dirname $0)

cd ..

mvn -s private-settings.xml clean package
ret=$?
if [ $ret -ne 0 ]; then
  exit $ret
fi
rm -rf target

exit
