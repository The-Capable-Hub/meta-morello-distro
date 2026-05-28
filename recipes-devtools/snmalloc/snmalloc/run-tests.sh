#!/bin/bash

# This runs all the tests built by snmalloc and installed
# prefixed with perf-* or func-*

DATE=$(date +%m%d_%H%M%S)
TEST_LOG="${HOME}/snmalloc_${DATE}.log"
TEST_BIN_PATH=@TEST_BIN_PATH@

touch "${TEST_LOG}"

function print_message() {
    echo "$1" | tee -a "${TEST_LOG}"
}

print_message "******************" 
print_message "SNMALLOC TEST LOG:" 
print_message "******************" 
print_message "" 

for f in "${TEST_BIN_PATH}/"*; do
    print_message "Testing $(basename ${f})" 
    print_message $(${f})
    print_message "Result: $?" 
done

print_message "TESTING DONE!" 
