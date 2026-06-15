#!/bin/bash

# This runs all the tests built by snmalloc and installed
# prefixed with perf-* or func-*

DATE=$(date +%m%d_%H%M%S)
TEST_LOG="${HOME}/snmalloc_${DATE}.log"
TEST_BIN_PATH="@TEST_BIN_PATH@"

touch "${TEST_LOG}"

function print_message() {
    echo "$1" 2>&1 | tee -a "${TEST_LOG}"
}

print_message "******************" 
print_message "SNMALLOC TEST LOG:" 
print_message "******************" 
print_message "" 

for f in "${TEST_BIN_PATH}/"*; do
    print_message "## Testing $(basename ${f})"
    start=$(date +%s)
    output=$("${f}" 2>&1 )
    end=$(date +%s)
    ret=$?
    elapsed=$(( (end - start) ))
    print_message "${output}"
    print_message "Result: ${ret}" 
    print_message "Time: ${elapsed}s"
done

print_message "TESTING DONE!" 
print_message "Total tests: $(grep -c 'Result' ${TEST_LOG})"
print_message "Failed tests: $(grep 'Result' ${TEST_LOG} | grep -c '[C,c]heck [F,f]ail' ${TEST_LOG})"
print_message "Out from testing saved in ${TEST_LOG}" 
