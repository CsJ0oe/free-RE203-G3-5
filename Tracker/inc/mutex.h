#include <pthread.h>

//declaring the mutex as a global variable
pthread_mutex_t _mutex = PTHREAD_MUTEX_INITIALIZER;

void mutex_lock() {
	if (pthread_mutex_lock(&_mutex) != 0) {
		perror("mutex_lock");
		exit(-1);
	}
}

void mutex_unlock() {
	if (pthread_mutex_unlock(&_mutex) != 0) {
		perror("mutex_unlock");
		exit(-1);
	}
}
