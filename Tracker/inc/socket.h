#include <sys/socket.h>
#include <sys/types.h>
#include <netdb.h>
#include <errno.h>

//create a server socket

int socket_id; //declaring the socket as a global variable
int socket_EOL = 0;
void socket_server(const char* port) {
	int status;
	struct addrinfo hints, *result, *rp;

	memset(&hints, 0, sizeof(struct addrinfo));
    hints.ai_family = AF_INET;       /* Allow IPv4 */
    hints.ai_socktype = SOCK_STREAM; /* TCP socket */
    hints.ai_flags = AI_PASSIVE;     /* For wildcard IP address */
    hints.ai_protocol = 0;           /* Any protocol */
    hints.ai_addrlen = 0;
    hints.ai_canonname = NULL;
    hints.ai_addr = NULL;
    hints.ai_next = NULL;

    status = getaddrinfo(NULL, port, &hints, &result);
    if (status != 0) { perror("getaddrinfo error"); exit(-1); }

    /* getaddrinfo() returns a list of address structures.
       Try each address until we successfully bind(2).
       If socket(2) (or bind(2)) fails, we (close the socket
       and) try the next address. */

    for (rp = result; rp != NULL; rp = rp->ai_next) {
        socket_id = socket(rp->ai_family, rp->ai_socktype,
                rp->ai_protocol);
        if (socket_id == -1)
            continue;
        
        if (bind(socket_id, rp->ai_addr, rp->ai_addrlen) == 0)
            break;                  /* Success */
        
        close(socket_id);
        socket_id = -1;
    }

    if (rp == NULL)               /* No address succeeded */
        { perror("bind"); exit(-1); }

    if(listen(socket_id,5) != 0)
        { perror("listen"); exit(-1); }

}

//creating the client socket

int socket_client(const char* host, const char* port) {
    int status;
    struct addrinfo hints, *result, *rp;

    /* Obtain address(es) matching host/port */
    memset(&hints, 0, sizeof(struct addrinfo));
    hints.ai_family = AF_INET;       /* Allow IPv4 */
    hints.ai_socktype = SOCK_STREAM; /* TCP socket */
    hints.ai_flags = AI_PASSIVE;     /* For wildcard IP address */
    hints.ai_protocol = 0;           /* Any protocol */
    hints.ai_addrlen = 0;
    hints.ai_canonname = NULL;
    hints.ai_addr = NULL;
    hints.ai_next = NULL;

    status = getaddrinfo(host, port, &hints, &result);
    if (status != 0) { perror("getaddrinfo error"); return(errno); }

    /* getaddrinfo() returns a list of address structures.
          Try each address until we successfully connect(2).
          If socket(2) (or connect(2)) fails, we (close the socket
          and) try the next address. */

    for (rp = result; rp != NULL; rp = rp->ai_next) {
        socket_id = socket(rp->ai_family, rp->ai_socktype,
                     rp->ai_protocol);
        if (socket_id == -1)
            continue;

        if (connect(socket_id, rp->ai_addr, rp->ai_addrlen) != -1)
            break;                  /* Success */

        close(socket_id);
        socket_id = -1;
    }

    if (rp == NULL)               /* No address succeeded */
        { perror("connect"); return(errno); }
    return 0;
}


void socket_close() {
	shutdown(socket_id, SHUT_RDWR);
	close(socket_id);
}
// wait for incoming connection
void socket_accept(int *id, struct sockaddr* addr, socklen_t* addrlen ) {
    *addrlen = sizeof(*addr);
    if((*id = accept(socket_id, addr, addrlen)) < 0)
        { perror("accept"); exit(-1); }
}

//receive messages
int socket_recv(int client_id, char* buffer, int buffer_len) {
    int res = -1, pos = 0;
    // recv until newline
    while   (   pos < buffer_len &&
                (res = recv(client_id, buffer+pos, 1, 0)) == 1 &&
                buffer[pos] != '\n'
            ) pos++;
	//int res = recv(client_id, buffer, buffer_len-1, 0);
	if (res >  0) { buffer[pos]='\0'; }
    if (res <  0) { perror("socket_recv"); return(1); }
    if (res == 0) { close(client_id); return 1;} // Client disconnected
    return 0;
}

//receive words
int socket_recv_word(int client_id, char* buffer, int buffer_len) {
    if (socket_EOL==1) {
        buffer[0]='\0';
        socket_EOL = 0;
        return 0;
    }
    int res = -1, pos = 0;
    // recv until newline
    while   (   pos < buffer_len &&
                (res = recv(client_id, buffer+pos, 1, 0)) == 1 &&
                buffer[pos] != '\n' && buffer[pos] != '\r' &&
                buffer[pos] != '\t' && buffer[pos] != ' '

            ) pos++;
    if (buffer[pos]==' ')   socket_EOL = 0;
    else                    socket_EOL = 1;
    //int res = recv(client_id, buffer, buffer_len-1, 0);
    if (res >  0) { buffer[pos]='\0'; }
    if (res <  0) { perror("socket_recv"); return(1); }
    if (res == 0) { close(client_id); return 1;} // Client disconnected
    return 0;
}

//receive chars
int socket_recv_char(int client_id, char* buffer, int buffer_len) {
    int res = -1, pos = 0;
    // recv until newline
    while   (   pos < buffer_len &&
                (res = recv(client_id, buffer+pos, 1, 0)) == 1 &&
                buffer[pos] != '\n' && buffer[pos] != '\r' &&
                buffer[pos] != '\t' && buffer[pos] != ' '

            ) pos++;
    //int res = recv(client_id, buffer, buffer_len-1, 0);
    if (res >  0) { buffer[pos]='\0'; }
    if (res <  0) { perror("socket_recv"); return(1); }
    if (res == 0) { close(client_id); return 1;} // Client disconnected
    return 0;
}

//sending messages
int socket_send(int client_id, char* in_buffer) {
    char buffer[256], *buffer_ptr;
    strcpy(buffer, in_buffer);
    buffer[strlen(in_buffer)]  ='\n';
    buffer[strlen(in_buffer)+1]='\0';
    buffer_ptr = buffer;
    while (strlen(buffer_ptr) > 0) {
        int res = send(client_id, buffer_ptr, strlen(buffer), 0);
        if (res <  0) { perror("socket_send"); exit(-1); }
        //if (res == 0) { close(client_id); return 1;} // Client disconnected
        buffer_ptr += res;
    }
    return 0;
}

// check if socket is connected
int socket_ready() {
    return (socket_id > -1);
}