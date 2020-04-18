#include <unistd.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <netinet/in.h>
#include <pthread.h>
#include "../inc/thread.h"
#include "../inc/queue.h"
#include "../inc/socket.h"
#include "../inc/mutex.h"
#include "../inc/map.h"

#define MAX_CLIENTS 50
#define MAX_ROOMS   20

struct client {
    int id;
    int room_id;
    char name[32];
    char host[32];
    char port[32];
    int listen_port;
    struct sockaddr addr;
    socklen_t addrlen;
} * clients[MAX_CLIENTS];

typedef struct client * client_t;

map_t client_map;

struct room {
    int id;
    char name[32];
} * rooms[MAX_ROOMS];

typedef struct massege {
    int from;
    int type; // 0 - from server to room / 1 - from client to room / 2 - from server to client
    int to;
    char* str;
} massege;

void client_thread(void*);
void sender_thread(void*);
void command_handler(int, char*);
void send_msg(int, int, char*);
void join_room (int, char*);
int  get_info(int);
void set_name(int, char*, int);
void send_help(int);
void send_rooms(int);
void send_users(int);

enum CLIENT_STATE{
    READY,
    ANNOUNCE,
    LISTEN,
    PORT,
    SEED,
    LEECH,
    LOOK,
    GETFILE,
    OK,
    NOK,
    DESCONNECTED,
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
    thread_create(&sender_thread, NULL);
    while(1) {  //wait for each incoming connection and launch the client thread
        struct client *tmp = malloc(sizeof(struct client));
        socket_accept(&tmp->id, &tmp->addr, &tmp->addrlen);
        clients[tmp->id] = tmp;
        getnameinfo(&tmp->addr, tmp->addrlen,
            tmp->host, sizeof(tmp->host),
            tmp->port, sizeof(tmp->port),
            0);
        thread_create(&client_thread, tmp);
    }
    socket_close();
    return 0;
}

//receiving and handling the client messages 
void client_thread (void* arg) {
    client_t client = (client_t)arg;
    int client_id = client->id;
    //if (get_info(client_id)) { return; }

    //send_msg(client_id, 2, "Welcome");
    printf("Client Connected %s(%d) FROM %s:%s\n",  clients[client_id]->name, client_id,
                                                    clients[client_id]->host,
                                                    clients[client_id]->port);
    
    //handle client input using state machine
    enum CLIENT_STATE state = READY;
    char buffer[1024];
    while(1) {
    /*
    < look [$Criterion1 $Criterion2 …]
    > list [$Filename1 $Length1 $PieceSize1 $Key1 $Filename2 $Length2 $PieceSize2 $Key2 …]
    < look [filename=”file_a.dat” filesize>”1048576”]
    > list [file_a.dat 2097152 1024 8905e92afeb80fc7722ec89eb0bf0966]
    < getfile $Key
    > peers $Key [$IP1:$Port1 $IP2:$Port2 …]
    < getfile 8905e92afeb80fc7722ec89eb0bf0966
    > peers 8905e92afeb80fc7722ec89eb0bf0966 [1.1.1.2:2222 1.1.1.3:3333]
    */
        switch (state) {
            case READY: {
                printf("READY\n");
                if (socket_recv_word(client_id, buffer, sizeof(buffer)) > 0) {
                    state = DESCONNECTED;
                    break;
                }
                switch (buffer[0]) {
                    case 'a': state = ANNOUNCE; break;
                    case 'l': state = LOOK;     break;
                    case 'g': state = GETFILE;  break;
                    default : state = READY;    break;
                } 
                //sscanf(strstr(buffer,"seed") , "seed %s",str1);
                //sscanf(strstr(buffer,"leech"), "leech %s",str2);
                
            } break;
            case ANNOUNCE: {
                printf("ANNOUNCE\n");
                if (socket_recv_word(client_id, buffer, sizeof(buffer)) > 0) {
                    state = DESCONNECTED;
                    break;
                }
                state = PORT;
            } break;
            case PORT: {
                printf("PORT\n");
                if (socket_recv_word(client_id, buffer, sizeof(buffer)) > 0) {
                    state = DESCONNECTED;
                    break;
                }
                sscanf(buffer,"%d",&(client->listen_port));
                printf("%d\n", client->listen_port);
                state = SEED;
            } break;
            case SEED: {
                printf("SEED\n");
                do {
                    if (socket_recv_word(client_id, buffer, sizeof(buffer)) > 0) {
                        state = DESCONNECTED;
                        break;
                    }
                } while (strstr(buffer,"]") == NULL);
                state = LEECH;
            } break;
            case LEECH: {
                printf("LEECH\n");
                do {
                    if (socket_recv_word(client_id, buffer, sizeof(buffer)) > 0) {
                        state = DESCONNECTED;
                        break;
                    }
                } while (strstr(buffer,"]") == NULL);
                state = OK;
            } break;
            case XXX: {
                printf("XXX\n");
                if (socket_recv_word(client_id, buffer, sizeof(buffer)) > 0) {
                    state = DESCONNECTED;
                    break;
                }

            } break;
            case OK: {
                printf("OK\n");
                send_msg(client_id, 2, "OK");
                state = READY;
            } break;
            case NOK: {
                printf("NOK\n");
                send_msg(client_id, 2, "NOK");
                state = READY;
            } break;
            case DESCONNECTED: {
                printf("DESCONNECTED\n");
                printf("Client Disconnected %s(%d)\n", clients[client_id]->name ,client_id);
                free(clients[client_id]);
                clients[client_id]=NULL;
                return;
            } break;
            default: { /* NOK */ }
        }
        printf("%s\n", buffer);
    }
}

// sending messages to clients using thread-safe queue
//(this thread handles all masseges sent by the server)

void sender_thread (void* arg) {
    massege* msg;
    char buffer[256];
    while(1) {
        mutex_lock();
        if(queue_empty()) {
            mutex_unlock();
            thread_sleep(0.5);
        } else {
            queue_pop((void *)&msg);
            mutex_unlock();
            switch (msg->type) {
                case 0: {
                    for(int id=0; id<MAX_CLIENTS; id++)
                        if (clients[id] != NULL && clients[id]->room_id == msg->to && id != msg->from)
                            socket_send(id, msg->str);
                } break;
                case 1: {
                    snprintf(buffer, sizeof(buffer), "%s : %s", clients[msg->from]->name, msg->str);
                    for(int id=0; id<MAX_CLIENTS; id++)
                        if (clients[id] != NULL && clients[id]->room_id == msg->to)
                            socket_send(id, buffer);
                } break;
                case 2: {
                    socket_send(msg->from, msg->str);   
                } break;
            }
            free(msg->str);
            free(msg);
        }
    }
}


//handling client messages
void command_handler (int client_id, char* buffer) {

    //printf("DEBUG %s(%d)[%s] : %s\n",   clients[client_id]->name, client_id,
    //                                  rooms[clients[client_id]->room_id]->name,
    //                                    buffer);
    /*
    < announce listen $Port seed [$Filename1 $Length1 $PieceSize1 $Key1 $Filename2 $Length2 $PieceSize2 $Key2 …] leech [$Key3 $Key4 …]
    > ok
    < announce listen 2222 seed [file_a.dat 2097152 1024 8905e92afeb80fc7722ec89eb0bf0966 file_b.dat 3145728 1536 330a57722ec8b0bf09669a2b35f88e9e]
    > ok
    < look [$Criterion1 $Criterion2 …]
    > list [$Filename1 $Length1 $PieceSize1 $Key1 $Filename2 $Length2 $PieceSize2 $Key2 …]
    < look [filename=”file_a.dat” filesize>”1048576”]
    > list [file_a.dat 2097152 1024 8905e92afeb80fc7722ec89eb0bf0966]
    < getfile $Key
    > peers $Key [$IP1:$Port1 $IP2:$Port2 …]
    < getfile 8905e92afeb80fc7722ec89eb0bf0966
    > peers 8905e92afeb80fc7722ec89eb0bf0966 [1.1.1.2:2222 1.1.1.3:3333]
    */


/*
        buffer[5] = '\0';
        if (strcmp(buffer, "/join")==0)          join_room(client_id, buffer+6);  // client typed a command to change room
        else if (strcmp(buffer, "/name")==0)     set_name (client_id, buffer+6, 0);
        else if (strcmp(buffer, "/list")==0) {
            buffer+=6;
            buffer[5]='\0';
            if      (strcmp(buffer, "rooms")==0) send_rooms(client_id);
            else if (strcmp(buffer, "users")==0) send_users(client_id);
            else {
                send_msg(client_id, 2, "Unknown Command");
                send_help(client_id);
            }
        } else {
            send_msg(client_id, 2, "Unknown Command");
            send_help(client_id);
        }
    send_msg(client_id, 1, buffer);

*/
}

// add msg to queue to be sent
void send_msg(int from, int type, char* buffer) {
    massege* msg = malloc(sizeof(massege));
    msg->from = from;
    msg->type = type;
    msg->to   = clients[from]->room_id;
    msg->str  = malloc(sizeof(char)*(strlen(buffer)+1));
    strcpy(msg->str, buffer);
    mutex_lock();
    queue_push(msg);
    mutex_unlock();
}

// called when client uses /join command
/*void join_room (int client_id, char* room) {
    if (strlen(room) == 0) {
        send_msg(client_id, 2, "Unknown Room");
        send_help(client_id);
        return;
    }
    int room_id = -1;
    for (int id = 0; id < MAX_ROOMS; ++id) { // search for requested room
        if (rooms[id]!=NULL && strcmp(room, rooms[id]->name)==0) room_id = id;
    }
    if (room_id < 0) { //create a room if requested room not found 
        for (room_id = 0; room_id < MAX_ROOMS && rooms[room_id]!=NULL; ++room_id) {}
        rooms[room_id] = malloc(sizeof(struct room));
        rooms[room_id]->id = room_id;
        strcpy(rooms[room_id]->name, room);
    }
    
    char buffer[256];
    snprintf(buffer, sizeof(buffer), "%s left your room", clients[client_id]->name);
    send_msg(client_id, 0, buffer);

    // change client's room 
    clients[client_id]->room_id = room_id;

    snprintf(buffer, sizeof(buffer), "%s joined your room", clients[client_id]->name);
    send_msg(client_id, 0, buffer);

    snprintf(buffer, sizeof(buffer), "Joining \"%s\"", room);
    send_msg(client_id, 2, buffer);

    // send new list of users
    send_users(client_id);
}*/

//get client connection info from socket and nickname
int get_info (int client_id) {
    //get client connection info from socket
    getnameinfo(&clients[client_id]->addr, clients[client_id]->addrlen,
            clients[client_id]->host, sizeof(clients[client_id]->host),
            clients[client_id]->port, sizeof(clients[client_id]->port),
            0);
    //receiving client nickname
    /*char buffer[32];
    if (socket_recv(client_id, buffer, sizeof(buffer))) {
        printf("Login Failed (%d) FROM %s:%s\n",  client_id,
                                                    clients[client_id]->host,
                                                    clients[client_id]->port);
        free(clients[client_id]);
        clients[client_id]=NULL;
        return 1;
    }
    */
    set_name(client_id, "peer", 1);
    return 0;
}

// set name for client
void set_name(int client_id, char* in_buffer, int join) { // join = 1 : if the client just connected
    /*int found = 0;
    for(int id=0; id<MAX_CLIENTS; id++) {
        if (clients[id] != NULL && id != client_id && strcmp(clients[id]->name, in_buffer) == 0)
            found = 1;
    }
    */
    char name[32];
    strcpy(name, in_buffer);
    /*if (found) {
        send_msg(client_id, 2, "Name Exists (adding random numbers)");
        srand(time(0));
        snprintf(name, sizeof(name), "%s%d%d%d%d", in_buffer, rand()%10, rand()%10, rand()%10, rand()%10);
    }
    if (!join) {
        char buffer[256];
        snprintf(buffer, sizeof(buffer), "%s changed name to %s", clients[client_id]->name, name);
        send_msg(client_id, 0, buffer);
    }
    */
    strcpy(clients[client_id]->name, name);
    /*send_msg(client_id, 2, "Name Changed");*/
}

//send commands help
/*void send_help (int client_id) {
    send_msg(client_id, 2, "to change room use :\n\t\t/join room_name");
    send_msg(client_id, 2, "to list available rooms use :\n\t\t/list rooms");
    send_msg(client_id, 2, "to list users in current room use :\n\t\t/list users");
}

// send list of rooms
void send_rooms (int client_id) {
    send_msg(client_id, 2, "Available Rooms :");
    for(int id=0; id<MAX_ROOMS; id++) {
        if (rooms[id] != NULL) {
            char buffer[32+8];
            snprintf(buffer, sizeof(buffer), "\t\t%s", rooms[id]->name);
            send_msg(client_id, 2, buffer);
        }
    }
}

// send list of users in the current room
void send_users (int client_id) {
    send_msg(client_id, 2, "Current Room Users :");
    for(int id=0; id<MAX_CLIENTS; id++) {
        if (clients[id] != NULL && clients[id]->room_id == clients[client_id]->room_id) {
            char buffer[32+8];
            snprintf(buffer, sizeof(buffer), "\t\t%s", clients[id]->name);
            send_msg(client_id, 2, buffer);
        }
    }
}

*/
