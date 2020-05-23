#include <unistd.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <netinet/in.h>
#include <pthread.h>
#include <assert.h>
#include <gmodule.h>
#include "../inc/thread.h"
#include "../inc/queue.h"
#include "../inc/socket.h"
#include "../inc/mutex.h"
#include "../inc/map.h"
#include "../inc/mapped_list.h"

#define MAX_CLIENTS 50
#define MAX_ROOMS   20

struct client {
    int id;
    char host[32];
    char port[32];
    char ip_port[32];
    int listen_port;
    struct sockaddr addr;
    socklen_t addrlen;
} * clients[MAX_CLIENTS];

typedef struct client * client_t;

map_t client_map;
map_t file_map;
mapped_list_t file_peers;
mapped_list_t file_names;

typedef struct message {
    int to;
    char* str;
    int EOL;
} message;

typedef struct file {
    char key[64];
    char name[64];
    int length;
    int piece;
} *file_t;

void client_thread(void*);
void sender_thread(void*);
void send_msg(int, int, char*);

enum CLIENT_STATE{
    READY,
    ANNOUNCE,
    UPDATE,
    LISTEN,
    PORT,
    OPTIONS,
    SEED,
    LEECH,
    LOOK,
    GETFILE,
    OK,
    NOK,
    EOL,
    DISCONNECTED,
    XXX
};

int main(int argc, char const *argv[]) {
    if (argc < 2) {
        printf("USAGE : %s PORT\n", argv[0]);
        exit(-1);
    }
    // create server socket
    socket_server(argv[1]);
    queue_init();
    client_map = map_new();
    file_map = map_new();
    file_peers = mapped_list_new();
    file_names = mapped_list_new();
    thread_create(&sender_thread, NULL);
    while(1) {  //wait for each incoming connection and launch the client thread
        struct client *tmp = malloc(sizeof(struct client));
        socket_accept(&tmp->id, &tmp->addr, &tmp->addrlen);
        clients[tmp->id] = tmp;
        getnameinfo(&tmp->addr, tmp->addrlen,
            tmp->host, sizeof(tmp->host),
            tmp->port, sizeof(tmp->port),
            0);
        strcpy(tmp->ip_port,tmp->host);
        strcat(tmp->ip_port,":");
        strcat(tmp->ip_port,tmp->port);
        map_insert(client_map,tmp->ip_port,tmp);
        thread_create(&client_thread, tmp);
    }
    socket_close();
    return 0;
}

//receiving and handling the client messages 
void client_thread (void* arg) {
    client_t client = (client_t)arg;
    int client_id = client->id;
    printf("Client Connected (%d) FROM %s\n", client_id,
                                            clients[client_id]->ip_port);
    //handle client input using state machine
    enum CLIENT_STATE state = READY;
    char buffer[1024];
    while(1) {
    /*
    < look [$Criterion1 $Criterion2 …]
    > list [$Filename1 $Length1 $PieceSize1 $Key1 $Filename2 $Length2 $PieceSize2 $Key2 …]
    < look [filename="file_a.dat" filesize>”1048576”]
    > list [file_a.dat 2097152 1024 8905e92afeb80fc7722ec89eb0bf0966]
    < getfile $Key
    > peers $Key [$IP1:$Port1 $IP2:$Port2 …]
    < getfile 8905e92afeb80fc7722ec89eb0bf0966
    > peers 8905e92afeb80fc7722ec89eb0bf0966 [1.1.1.2:2222 1.1.1.3:3333]
    */printf("Buffer %s \n", buffer);
        switch (state) {
            case READY: {
                printf("READY\n");
                if (socket_recv_word(client_id, buffer, sizeof(buffer)) > 0) {
                    state = DISCONNECTED;
                    break;
                }                
                switch (buffer[0]) {
                    case 'a': state = ANNOUNCE; break;
                    case 'u': state = UPDATE;   break;
                    case 'l': state = LOOK;     break;
                    case 'g': state = GETFILE;  break;
                    default : state = NOK;      break;
                }                 
            } break;
            case ANNOUNCE: {
                printf("ANNOUNCE\n");
                if (socket_recv_word(client_id, buffer, sizeof(buffer)) > 0) {
                    state = DISCONNECTED;
                    break;
                }
                state = PORT;
            } break;
            case UPDATE: {
                printf("UPDATE\n");
                state = OPTIONS;
            } break;
            case PORT: {
                printf("PORT\n");
                if (socket_recv_word(client_id, buffer, sizeof(buffer)) > 0) {
                    state = DISCONNECTED;
                    break;
                }
                sscanf(buffer,"%d",&(client->listen_port));
                printf("%d\n", client->listen_port);
                state = OPTIONS;
            } break;
            case OPTIONS: {
                printf("OPTIONS\n");
                if (socket_recv_word(client_id, buffer, sizeof(buffer)) > 0) {
                    state = DISCONNECTED;
                    break;
                }
                switch (buffer[0]) {
                    case 's' : state = SEED;  break;
                    case 'l' : state = LEECH; break;
                    case '\0': state = OK;    break;
                    default  : state = NOK;   break;
                }
                // remove "[" from socket buffer
                if ((state==SEED||state==LEECH)&&socket_recv_char(client_id, buffer, 1) > 0) {
                    state = DISCONNECTED;
                    break;
                }
            } break;
            case SEED: {
                printf("SEED\n");
                file_t tmp = malloc(sizeof(struct file));
                if (socket_recv_word(client_id, buffer, sizeof(buffer)) > 0) {
                    state = DISCONNECTED;
                    break;
                }
                strcpy(tmp->name,buffer);
                if (socket_recv_word(client_id, buffer, sizeof(buffer)) > 0) {
                    state = DISCONNECTED;
                    break;
                }
                sscanf(buffer,"%d",&(tmp->length));
                if (socket_recv_word(client_id, buffer, sizeof(buffer)) > 0) {
                    state = DISCONNECTED;
                    break;
                }
                sscanf(buffer,"%d",&(tmp->piece));
                if (socket_recv_word(client_id, buffer, sizeof(buffer)) > 0) {
                    state = DISCONNECTED;
                    break;
                }
                strcpy(tmp->key,buffer);
                state = SEED;
                if (tmp->key[strlen(tmp->key)-1]==']') {
                    state = OPTIONS;
                    tmp->key[strlen(tmp->key)-1] = '\0';
                }
                //verify if already in the list
                map_insert(file_map,tmp->key,tmp);
                list_t l = mapped_list_get(file_names, tmp->name);
                if(l == NULL){
                    mapped_list_add(file_names, tmp->name, tmp);
                }
                l = mapped_list_get(file_peers, tmp->key);
                int ok = 1;
                while (l != NULL) {
                    if (((client_t)(l->data))->id == client->id) {
                        ok = 0;    
                        break;
                    }
                    l = mapped_list_next(l);
                }
                if(ok)
                    mapped_list_add(file_peers, tmp->key, client);
            } break;
            case LEECH: {
                printf("LEECH\n");
                if (socket_recv_word(client_id, buffer, sizeof(buffer)) > 0) {
                    state = DISCONNECTED;
                    break;
                }
                state = LEECH;
                if (buffer[strlen(buffer)-1]==']') {
                    state = OPTIONS;
                }
            } break;
            case LOOK: {
                printf("LOOK\n");
                if (socket_recv_word(client_id, buffer, sizeof(buffer)) > 0) {
                    state = DISCONNECTED;
                    break;
                }
                char filename[64];
                sscanf(buffer,"[filename=\"%[^\"]\"]",filename);
                printf("%s\n", filename);
                send_msg(client_id, 1, "list [");
                int first = 1;
                list_t l = mapped_list_get(file_names, filename);
                while (l != NULL) {
                    char msg[255] = {0};
                    if (first == 0)
                        send_msg(client_id, 1, " ");
                    sprintf(msg,"%s %d %d %s", ((file_t)(l->data))->name, ((file_t)(l->data))->length,
                                               ((file_t)(l->data))->piece, ((file_t)(l->data))->key);
                    send_msg(client_id, 1, msg);
                    l = mapped_list_next(l);
                    first = 0;
                }
                send_msg(client_id, 2, "]");
                state = EOL;
            } break;
            case GETFILE: {
                printf("GETFILE\n");
                if (socket_recv_word(client_id, buffer, sizeof(buffer)) > 0) {
                    state = DISCONNECTED;
                    break;
                }
                printf("%s\n", buffer);
                send_msg(client_id, 1, "peers ");
                send_msg(client_id, 1, buffer);
                send_msg(client_id, 1, " [");
                int first = 1;
                list_t l = mapped_list_get(file_peers, buffer);
                while (l != NULL) {
                    if (((client_t)(l->data))->id != client->id) {
                        if (first == 0) send_msg(client_id, 1, " ");
                        send_msg(client_id, 1, ((client_t)(l->data))->host);
                        send_msg(client_id, 1, ":");
                        char str[32];
                        sprintf(str, "%d", ((client_t)(l->data))->listen_port);
                        send_msg(client_id, 1, str);
                        first = 0;
                    }
                    l = mapped_list_next(l);
                }
                send_msg(client_id, 2, "]");
                state = EOL;
            } break;
            case XXX: {
                printf("XXX\n");
                if (socket_recv_word(client_id, buffer, sizeof(buffer)) > 0) {
                    state = DISCONNECTED;
                    break;
                }
            } break;
            case OK: {
                printf("OK\n");
                send_msg(client_id, 2, "OK");
                state = EOL;
            } break;
            case NOK: {
                printf("NOK\n");
                send_msg(client_id, 2, "NOK");
                state = EOL;
            } break;
            case EOL: {
                socket_EOL=0;
                printf("EOL\n");
                state = READY;
            } break;
            case DISCONNECTED: {
                printf("DISCONNECTED\n");
                printf("Client Disconnected (%d)\n", client_id);
                free(clients[client_id]);
                clients[client_id]=NULL;
                return;
            } break;
            default: { /* NOK */ }
        }
        //printf("%s\n", buffer);
    }
}

// sending messages to clients using thread-safe queue
//(this thread handles all messages sent by the server)
void sender_thread (void* arg) {
    message* msg;
    while(1) {
        mutex_lock();
        if(queue_empty()) {
            mutex_unlock();
            thread_sleep(0.5);
        } else {
            queue_pop((void *)&msg);
            mutex_unlock();
            socket_send(msg->to, msg->str, msg->EOL);
            free(msg->str);
            free(msg);
        }
    }
}

// add msg to queue to be sent
void send_msg(int to, int type, char* buffer) {
    message* msg = malloc(sizeof(message));
    msg->to = to;
    msg->str  = malloc(sizeof(char)*(strlen(buffer)+1));
    msg->EOL = type-1;
    strcpy(msg->str, buffer);
    mutex_lock();
    queue_push(msg);
    mutex_unlock();
}

