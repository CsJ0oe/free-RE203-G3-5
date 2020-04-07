#include <pthread.h>

// function to create a thread 
void thread_create(void (*func) (void *), int* arg) {
	pthread_t thread;
    pthread_create(&thread, NULL, (void *) func, (void *) arg );
    pthread_detach(thread);
}

void thread_sleep(float sec) {
	sleep(1); //TODO
}