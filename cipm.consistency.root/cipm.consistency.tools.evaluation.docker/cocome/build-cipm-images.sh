#!/bin/bash

cd "cipm-consistency"
docker build -t "cipm/consistency" .

cd "../cipm-cocome-load"
docker build -t "cipm/cocome-load" .