#include "common.h"
#include "chunk.h"
#include "debug.h"
#include "vm.h"


int main(int argc, const char* argv[]) {
    initVM();

    Chunk chunk;
    initChunk(&chunk);

    // for (int i = 0; i < 256; i++) {
    //     writeConstant(&chunk, i, 1);
    // }
    
    // writeConstant(&chunk, 300.25, 3);
    // writeConstant(&chunk, 88.25, 3);
    // writeConstant(&chunk, 39.21, 2);

    // -((1.2 + 3.4) / 5.6)
    writeConstant(&chunk, 1.2, 2);
    writeConstant(&chunk, 3.4, 100);
    writeChunk(&chunk, OP_ADD, 123);

    writeConstant(&chunk, 5.6, 123);
    writeChunk(&chunk, OP_DIVIDE, 123);

    writeChunk(&chunk, OP_NEGATE, 123);

    writeChunk(&chunk, OP_RETURN, 2);

    disassembleChunk(&chunk, "test chunk");
    interpret(&chunk);

    freeVM();

    freeChunk(&chunk);
    return 0;
}