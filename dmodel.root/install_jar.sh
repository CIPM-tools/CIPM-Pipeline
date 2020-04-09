#!/bin/bash

repo="/Users/david/Desktop/Dynamic Approach/Implementation/git/dModel/dmodel.root/mvn-supplemental/mvn-repo"

echo -n "Enter the path of the JAR file: "
read jar

echo -n "Enter the group for the artifact: "
read group

echo -n "Enter the id for the artifact: "
read id

echo -n "Enter the version for the artifact: "
read version

mvn org.apache.maven.plugins:maven-install-plugin:2.3.1:install-file -Dfile="$jar" -DgroupId=$group -DartifactId=$id -Dpackaging=jar -Dversion=$version -DlocalRepositoryPath="$repo"

echo "compile '$group:$id:$version'"