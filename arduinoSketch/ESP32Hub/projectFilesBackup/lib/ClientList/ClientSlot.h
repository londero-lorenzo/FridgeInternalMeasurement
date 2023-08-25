//
// Created by Londero Lorenzo on ~ April 2023.
//
#include <WiFi.h>


#include "../utils.h"
//#include "../TaskList/Task.h"
#include "../TaskList/TaskList.h"


#define SOCKET_ERROR                (-1  + '0')
#define SOCKET_NOT_AVALIABLE        (-2  + '0')
#define DISCONNECTED                (-3  + '0')
#define DISCONNECTED_INACTIVITY     (-4  + '0')

#define COMMAND_TIMEOUT 60 //(30 seconds 'cause 500ms of delay)

#define COMMAND_IDENTIFIER          (1  + '0')
#define IDENTIFIER                  "FRIDGE_1"
#define COMMANDS_AUTHORIZED         (2  + '0')
#define COMMANDS_NOT_AUTHORIZED     (3  + '0')
#define CREATE_DHT_TASK             (4  + '0')
#define DELETE_DHT_TASK             (5  + '0')
#define GET_DATA_FROM_BUFFER        (6  + '0')


#define HTTP_REQUEST_HEADER "GET / HTTP/"


// viene verificato che il file header per il client non sia già stato definito
#ifndef SOCKET_TCP_CLIENT_H

// viene definito l'identificatore per questo file header associato all'oggetto a cui fa riferimento
#define SOCKET_TCP_CLIENT_H

namespace Socket {
    /**
     * Classe Client utilizzata per le comunicazioni TCP.
     */
    class ClientSlot {
        // vengono dichiarate le variabili private associate a questo oggetto
    private:
// variabile tipo WiFiClient utilizzata per avere un riferimento alla struttura Socket
        WiFiClient remoteClient{};

        TaskList *taskList{};

        // buffer di caratteri utilizzato per essere riempito con i dati in ricezione
        uint8_t receivingBuffer[1024]{};

        TaskHandle_t taskHandle = nullptr;

        static void backGroundProcess(void *pvParameters);

        void refreshBuffer();

        bool _isEmpty = true;

        bool _isAuthorized = false;

        bool askForAuthorization();

        // vengono dichiarate le variabili pubbliche associate a questo oggetto
    public:

        ClientSlot();

        // viene dichiarato il costruttore pubblico
        explicit ClientSlot(WiFiServer *server, TaskList *taskList);

        void send(char data);

        // viene dichiarato il metodo void per permettere d'inviare dati al server
        void send(const char *data);

        void sendHTMLWebSite();

        // viene dichiarato il metodo void per permettere la ricezione dei dati dal server
        char *receive();

        void run();

        // viene dichiarato il metodo per ottenere l'indirizzo ip di questo socket
        String getIp();

        TaskHandle_t getTaskHandle();

        DHTCollector *getProbeAt(uint8_t index);

        // viene dichiarato il metodo per chiudere il socket
        void close();

        bool isConnected();

        bool isEmpty();

        bool isAuthorized();

        void send(int data);
    };
}
#endif //SOCKET_TCP_CLIENT_H

