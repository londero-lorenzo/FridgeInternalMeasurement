//
// Created by Londero Lorenzo on ~ April 2023.
//
#include "ClientSlot.h"
#include "freertos/task.h"
#include <cstdio>

namespace Socket {


    ClientSlot::ClientSlot() = default;

    /**
     * Costruttore pubblico, utilizzato dalla parte server una volta che viene captato un tentativo di connessione.
     * @param ClientOnServerSide Struttura alla quale fa riferimento il socket lato server
     */
    ClientSlot::ClientSlot(WiFiServer *server, TaskList *taskList) {
        ClientSlot::remoteClient = server->available();
        ClientSlot::taskList = taskList;
        ClientSlot::_isEmpty = false;
    }

    void ClientSlot::send(int data) {
        ClientSlot::remoteClient.write(data);
    }

    void ClientSlot::send(char data) {
        ClientSlot::remoteClient.write(data);
    }


    /**
     * Metodo utilizzato per permettere l'invio dei dati al server.
     * @param data Insieme dei caratteri da inviare al server
     */
    void ClientSlot::send(const char *data) {
        DEBUG_PRINTLN("***************************************")
        DEBUG_PRINT("> Transmission to ")
        DEBUG_PRINT(ClientSlot::getIp())
        DEBUG_PRINTLN("... <")
        DEBUG_PRINTLN("Connection status:")
        DEBUG_PRINT("--> Connected: ")
        DEBUG_PRINTLN(ClientSlot::remoteClient.connected())
        DEBUG_PRINT("--> FileDescriptionSocket [fd()]: ")
        DEBUG_PRINTLN(ClientSlot::remoteClient.fd())
        DEBUG_PRINT("Data to send: ")
        DEBUG_PRINTLN(data)
        // viene richiamato il metodo interno per permettere l'invio dei dati
        size_t bytesSent = ClientSlot::remoteClient.write(data);
        // viene verificato che non siano insorti errori durante l'invio dei dati
        if (bytesSent <= 0) {
            DEBUG_PRINTLN("Error sending data.")
            DEBUG_PRINT("Error code: ")
            DEBUG_PRINTLN(bytesSent)
            ClientSlot::close();
            return;
        }
        DEBUG_PRINT("Bytes sent: ")
        DEBUG_PRINTLN(bytesSent)
        DEBUG_PRINTLN("> End of trasmission <")
        DEBUG_PRINTLN("***************************************")
    }

    void ClientSlot::refreshBuffer() {
        memset(&ClientSlot::receivingBuffer[0], '\0', sizeof(ClientSlot::receivingBuffer));
    }

    /**
     * Metodo utilizzato per ricevere la sequenza di caratteri in ricezione dal server.
     * @return La sequenza di caratteri contenuti nel buffer del client
     */
    char *ClientSlot::receive() {
        int timeOutCommandCounter = 0;
        while (true) {
            ClientSlot::refreshBuffer();
            if (ClientSlot::isConnected() && ClientSlot::remoteClient.available()) {
                DEBUG_PRINTLN("***************************************")
                DEBUG_PRINT("> Reception from ")
                DEBUG_PRINT(ClientSlot::getIp())
                DEBUG_PRINTLN("... <")
                // viene richiamato il metodo interno per permettere la ricezione dei dati
                int bytesReceived = ClientSlot::remoteClient.read(ClientSlot::receivingBuffer,
                                                                  sizeof(ClientSlot::receivingBuffer));
                // viene verificato che non siano insorti errori durante la ricezione dei dati
                if (bytesReceived == SOCKET_ERROR) {
                    DEBUG_PRINTLN("Error sending data.")
                    ClientSlot::close();
                    return (char *) (SOCKET_ERROR);
                } else {
                    DEBUG_PRINT("Bytes received: ")
                    DEBUG_PRINTLN(bytesReceived)
                    DEBUG_PRINT("Data received: ")
                    for (int i = 0; i < bytesReceived; i++) {
                        DEBUG_PRINT((char) ClientSlot::receivingBuffer[i])
                    }
                    DEBUG_PRINTLN("")
                    DEBUG_PRINTLN("> End of reception <")
                    DEBUG_PRINTLN("***************************************")
                }
                return (char *) ClientSlot::receivingBuffer;
            }
            if (!ClientSlot::isConnected()) {
                DEBUG_PRINTLN("4 ")
                DEBUG_PRINT("Client ")
                DEBUG_PRINT(ClientSlot::getIp())
                DEBUG_PRINT(" disconnected!")
                vTaskDelete(ClientSlot::getTaskHandle());
                return (char *) (DISCONNECTED);
            }

            vTaskDelay(500);
            timeOutCommandCounter++;
            if (((timeOutCommandCounter >= COMMAND_TIMEOUT) && !ClientSlot::isAuthorized()) ||
                ((timeOutCommandCounter >= COMMAND_TIMEOUT * 3) && ClientSlot::isAuthorized())) {
                DEBUG_PRINT("Client ")
                DEBUG_PRINT(ClientSlot::getIp())
                DEBUG_PRINTLN(" disconnected due to inactivity...")
                ClientSlot::close();
                return (char *) (DISCONNECTED_INACTIVITY);
            }
        }
    }

    bool ClientSlot::askForAuthorization() {
        while (true) {
            if (!ClientSlot::isConnected()) break;
            ClientSlot::send(COMMAND_IDENTIFIER);
            String identifierReceived = (char *) ClientSlot::receive();
            if (identifierReceived.indexOf(HTTP_REQUEST_HEADER) >= 0) {
                DEBUG_PRINTLN("3 ")
                ClientSlot::sendHTMLWebSite();
                return false;
            }
            DEBUG_PRINT("Identifier received: ")
            DEBUG_PRINTLN(identifierReceived)
            DEBUG_PRINT("Client ")
            DEBUG_PRINT(ClientSlot::getIp())
            if (identifierReceived == IDENTIFIER) {
                DEBUG_PRINTLN(" is authorized")
                ClientSlot::_isAuthorized = true;
                ClientSlot::send(COMMANDS_AUTHORIZED);
                return true;
            } else {
                DEBUG_PRINTLN(" is not authorized")
                ClientSlot::send(COMMANDS_NOT_AUTHORIZED);
                return false;
            }
        }
        return false;
    }

    void ClientSlot::sendHTMLWebSite() {
        ClientSlot::send((char *) "HTTP/1.1 200 OK");
        ClientSlot::send(0xd0a);
        ClientSlot::send((char *) "Connection: close");
        ClientSlot::send(0xd0a);
        ClientSlot::send(0xd0a);
        ClientSlot::send((char *) "<!DOCTYPE html><html>");
        ClientSlot::send(0xd0a);
        ClientSlot::send(
                (char *) R"(<head><meta http-equiv="refresh" content="0; URL='https://www.malignani.ud.it/'" /></head>)");
        ClientSlot::send(0xd0a);
        ClientSlot::send((char *) "</html>");
        ClientSlot::send(0xd0a);
        vTaskDelay(500);
        ClientSlot::close();
    }

    void ClientSlot::backGroundProcess(void *pvParameters) {
        auto *client = (ClientSlot *) pvParameters;
        if (!client->askForAuthorization()) client->close();
        char *command;
        while (true) {
            DEBUG_PRINT("1 ")
            command = client->receive();
            DEBUG_PRINTLN("2: command")
            DEBUG_PRINT("Command received: ")
            DEBUG_PRINTLN(*command)
            switch (*command) {
                case CREATE_DHT_TASK: {
                    char *ptr;
                    client->send(WAITING_FOR_RESPONSE);
                    long timeDelay = strtol((char *) client->receive(), &ptr, 10);
                    if (timeDelay) {
                        if (timeDelay >= MIN_TIME_DELAY) {
                            char *result = client->taskList->createNewTask(timeDelay);
                            if (*result >= '0') {
                                client->send(TASK_CREATED);
                                client->send(result);
                            }else client->send(*result); //SEND THE ERROR STORED IN THE FIRST CHAR BYTE
                        } else client->send(TASK_NOT_CREATED_MIN_TIME_DELAY);
                    } else client->send(TASK_NOT_CREATED_NOT_INT);
                    break;
                }
                case DELETE_DHT_TASK: {
                    char *taskIdentifier = (char *) client->receive();
                    client->send(client->taskList->removeProbesTimerTask(taskIdentifier));
                    break;
                }
                case GET_DATA_FROM_BUFFER:{
                    char *taskIdentifier = (char *) client->receive();
                    Task *selectedTask = client->taskList->getTask(taskIdentifier);
                    DEBUG_PRINT("Sending data from task selected: ")
                    if (selectedTask != nullptr){
                        DEBUG_PRINTLN(selectedTask -> getIdentifier())
                        for (int i = 0; i < selectedTask->getNumberOfProbes(); i++) {
                            DEBUG_PRINT("Getting data from probe at slot: ") DEBUG_PRINT(i) DEBUG_PRINTLN("...")
                            DHTCollector * currentDHTCollector= client->taskList->getProbeAt(i);
                            DEBUG_PRINT("Probe description: ")
                            DEBUG_PRINTLN(currentDHTCollector->getDescription())
                            DEBUG_PRINT("Probe GPIO port:")
                            DEBUG_PRINTLN(currentDHTCollector->getGPIOPort())
                            for (uint32_t e = 1; e < selectedTask->getTaskDataList()[i].getNumberOfDataInBuff(); e++) {
                                DHTData *selectedData = &(selectedTask->getTaskDataList()->getDataInBuffer()[e]);
                                String rawDataToSend;
                                DEBUG_PRINT("Data to send: ")
                                //rawDataToSend = String("G:") + selectedData->GPIOPort;
                                rawDataToSend = rawDataToSend + String("I:") + selectedData->timeIndex;
                                rawDataToSend = rawDataToSend + String(";T:") + selectedData->temperature;
                                rawDataToSend = rawDataToSend + String(";U:") + selectedData->humidity;
                                rawDataToSend = rawDataToSend + String(LINE_SEPARATOR);
                                DEBUG_PRINT(rawDataToSend)
                                client->send(rawDataToSend.c_str());
                            }
                        }
                    } else DEBUG_PRINTLN("No task with this identifier found!")
                }
                default: {
                    //CLIENT ERROR
                }
            }


            if (*command <= SOCKET_ERROR) break;
            DEBUG_PRINTLN("5 ")
            vTaskDelay(5);
        }
        DEBUG_PRINTLN("6 ")
        vTaskDelete(client->getTaskHandle());
    }

    void ClientSlot::run() {
        char *prefix = (char *) "Process_";
        String taskTitle = prefix + ClientSlot::getIp();
        taskTitle[strlen(prefix) + strlen(ClientSlot::getIp().c_str()) + 1] = '\0';
        xTaskCreate(ClientSlot::backGroundProcess, taskTitle.c_str(), 16384 , (void *) this, 5,
                    &(ClientSlot::taskHandle));
    }

    /**
     * Metodo utilizzato per ottenere l'indirizzo ip del client
     * @return Sequenza di caratteri che identificano l'indirizzo ip del client
     */
    String ClientSlot::getIp() {
        return ClientSlot::remoteClient.remoteIP().toString();
    }

    TaskHandle_t ClientSlot::getTaskHandle() {
        return ClientSlot::taskHandle;
    }

    /**
     * Metodo utilizzato per chiudere il socket.
     */
    void ClientSlot::close() {
        DEBUG_PRINTLN("***************************************")
        DEBUG_PRINT("Disconnecting client ")
        DEBUG_PRINTLN(ClientSlot::getIp())
        ClientSlot::remoteClient.read();
        //vTaskDelay(500);
        ClientSlot::remoteClient.flush();
        //vTaskDelay(500);
        ClientSlot::remoteClient.stop();
        //vTaskDelay(500);
        if (!ClientSlot::isConnected()) {DEBUG_PRINTLN("Client disconnected successfully!")}
        else {
            DEBUG_PRINTLN("Client disconnection failed!")
        }
        //Client::remoteClient.stop();
        //vTaskDelay(500);
        DEBUG_PRINTLN("***************************************")
        vTaskDelete(ClientSlot::getTaskHandle());
    }

    bool ClientSlot::isConnected() {
        return ClientSlot::remoteClient.connected() && !ClientSlot::isEmpty();
    }

    bool ClientSlot::isEmpty() {
        return ClientSlot::_isEmpty;
    }

    bool ClientSlot::isAuthorized() {
        return ClientSlot::_isAuthorized;
    }
}
