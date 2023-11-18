#!/bin/bash
shopt -s globstar
new_version="$1"
sed -Ei "s/org\\.bukkit\\.craftbukkit.v([A-z0-9]+)/org.bukkit.craftbukkit.v$new_version/g" ./plugin/**/src/**/*.kt
