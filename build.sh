#!/bin/bash
if [ "$1" = "clean" ]; then
   find . -name \*.class -exec rm {} \;
else
   find . -name \*.java | xargs javac && java bin4j.Main
fi