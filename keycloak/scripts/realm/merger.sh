#!/bin/bash

REALM_DIRS=("connect" "internal")

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
echo "SCRIPT_DIR $SCRIPT_DIR"

CONFIG_DIR="$SCRIPT_DIR/../../config"
OUTPUT="$CONFIG_DIR/realms.json"

echo "Creating realms file at: $OUTPUT"
mkdir -p "$(dirname "$OUTPUT")"

echo "[" > "$OUTPUT"

COUNT=0
TOTAL=${#REALM_DIRS[@]}

for DIR in "${REALM_DIRS[@]}"; do

    REALM_CONFIG_FILE="$CONFIG_DIR/$DIR/realm.json"

    if [ -f "$REALM_CONFIG_FILE" ]; then

        echo "Merging $REALM_CONFIG_FILE"

        REALM_CONFIG_DATA=$(<"$REALM_CONFIG_FILE")

        echo "  $REALM_CONFIG_DATA" >> "$OUTPUT"
        COUNT=$((COUNT + 1))

        if [ "$COUNT" -lt "$TOTAL" ]; then
            echo "," >> "$OUTPUT"
        fi
    fi
done

echo "]" >> "$OUTPUT"

echo "Full realms configuration JSON generated at: $OUTPUT"
