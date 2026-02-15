#include "common.h"
#include "chunk.h"
#include "debug.h"


int main(int argc, const char* argv[]) {
    Chunk chunk;
    initChunk(&chunk);

    for (int i = 0; i < 256; i++) {
        writeConstant(&chunk, i, 1);
    }

    writeConstant(&chunk, 300.25, 2);

    writeChunk(&chunk, OP_RETURN, 2);

    disassembleChunk(&chunk, "test chunk");

    freeChunk(&chunk);
    return 0;
}