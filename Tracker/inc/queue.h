// C program for array implementation of queue
#include <stdio.h>
#include <stdlib.h>
#include <limits.h>
#include <string.h>
 
// A structure to represent a queue
struct elem {
	void* data; // pointer to any type of variables
	struct elem *next;
};

struct queue
{
    struct elem *first;
    struct elem *last;
};

struct queue _queue;

// 
void queue_init() {
	_queue.first = NULL;
	_queue.last  = NULL;
}

// Queue is empty when size is 0
int queue_empty() {
	return (_queue.first == NULL);
}
 
// Function to add an item to the queue.  
// It changes rear and size
void queue_push(void* data) {
	if(queue_empty(_queue)) {
		_queue.first = malloc(sizeof(struct elem));
		_queue.last  = _queue.first;
	} else {
		_queue.last->next = malloc(sizeof(struct elem));
		_queue.last = _queue.last->next;
	}
    _queue.last->data = data;
    _queue.last->next = NULL;
}
 
// Function to remove an item from queue. 
// It changes front and size
void queue_pop(void** dataptr) {
    if (queue_empty()) return;
    *dataptr = _queue.first->data;
    struct elem *curr = _queue.first;
    _queue.first = _queue.first->next;
    if (_queue.first == NULL) _queue.last = NULL;
    free(curr);
}