#!/bin/bash
shopt -s globstar
cp ./plugin/**/build/libs/*-SNAPSHOT.jar "$1/"
