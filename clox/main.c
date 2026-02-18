#include <stdio.h>
#include <time.h>

#include "common.h"
#include "chunk.h"
#include "debug.h"
#include "vm.h"

int main(int argc, const char* argv[]) {
    initVM();
    Chunk chunk;
    initChunk(&chunk);

    printf("Generating benchmark bytecode...\n");

    int constIdx1 = addConstant(&chunk, 1.2);
    int constIdx2 = addConstant(&chunk, 3.4);
    int constIdx3 = addConstant(&chunk, 5.6);

    int iterations = 1;
    
    for (int i = 0; i < iterations; i++) {
        writeConstant(&chunk, 1.2, 1);
        writeConstant(&chunk, 3.4, 1);
        writeChunk(&chunk, OP_ADD, 1);

        writeConstant(&chunk, 5.6, 1);

        writeChunk(&chunk, OP_DIVIDE, 1);

        writeChunk(&chunk, OP_NEGATE, 1);
    }

    writeChunk(&chunk, OP_RETURN, 1);

    printf("Benchmarking %d iterations...\n", iterations);
    
    clock_t start = clock();
    interpret(&chunk);
    clock_t end = clock();

    double cpu_time_used = ((double) (end - start)) / CLOCKS_PER_SEC;
    
    printf("\n--- Results ---\n");
    printf("Time taken: %.4f seconds\n", cpu_time_used);
    printf("Ops per sec: %.2f million\n", iterations / cpu_time_used / 1000000.0);

    freeVM();
    freeChunk(&chunk);
    return 0;
}