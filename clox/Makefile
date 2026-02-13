CC = gcc
CFLAGS = -Wall -Wextra -g -std=c99

SOURCES = $(wildcard *.c)

OBJECTS = $(SOURCES:.c=.o)

TARGET = clox

all: $(TARGET)

$(TARGET): $(OBJECTS)
	$(CC) $(CFLAGS) -o $@ $^

%.o: %.c
	$(CC) $(CFLAGS) -c $< -o $@

clean:
	rm -f $(OBJECTS) $(TARGET)

.PHONY: all clean