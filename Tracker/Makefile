FLAGS= -Wall $(shell pkg-config --libs --cflags glib-2.0) -pthread -lglib-2.0

all: bin/tracker

clean:
	rm -fr bin/* obj/*

run: bin/tracker
	./bin/tracker 8767

bin/tracker: obj/tracker.o
	gcc obj/tracker.o -o bin/tracker $(FLAGS)

obj/tracker.o: src/tracker.c inc/socket.h inc/thread.h inc/queue.h inc/mutex.h inc/map.h
	gcc -c src/tracker.c -o obj/tracker.o $(FLAGS)
