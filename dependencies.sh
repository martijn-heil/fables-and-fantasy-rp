#!/bin/bash
for f in ./*/src/main/resources/plugin.yml; do < "$f" sed 's/@.*@//g' | yq -MP '.depend'; done | grep -vE '^((- Fables)|(\[\]))' | sort | uniq
