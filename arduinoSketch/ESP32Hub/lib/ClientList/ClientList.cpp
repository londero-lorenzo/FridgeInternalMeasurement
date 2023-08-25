
#include "ClientList.h"
namespace Socket{
  ClientList::ClientList(){
    for (int i = 0; i < MAX_NO_CLIENTS; i++)
      ClientList::clients[i] = ClientSlot();
  }

  void ClientList::add(ClientSlot *clientToAdd)
  {
    ClientList::checkForConnections();
    DEBUG_PRINTLN("***************************************");
    DEBUG_PRINT("Receiving new connection from: ");
    DEBUG_PRINTLN(clientToAdd->getIp());
    for (int i = 0; i < MAX_NO_CLIENTS; i++)
    {
      ClientSlot *clientSelected = &ClientList::clients[i];
      if (!clientSelected->isEmpty()){
        if (clientSelected->getIp() == clientToAdd->getIp())
        {
          DEBUG_PRINTLN("Another connection is using this ip address!")
          clientToAdd->close();
          DEBUG_PRINTLN("Connection closed...");
          return;
        }
      }else{
          DEBUG_PRINT("Adding client request at slot: ");
          DEBUG_PRINTLN(i);
          ClientList::clients[i] = *clientToAdd;
          ClientList::clients[i].run();
          DEBUG_PRINTLN("***************************************");
          return;
      }
    }
    DEBUG_PRINTLN("Unable to handle another connection (maximum number of clients reached)!");
    clientToAdd->close();
    DEBUG_PRINTLN("Connection closed...");
  } 

  void ClientList::remove(ClientSlot clientToRemove)
  {
    ClientSlot newClientList[MAX_NO_CLIENTS];
    int notEmptyClientsCounter = 0;

    DEBUG_PRINT("Removing ")
    DEBUG_PRINTLN(clientToRemove.getIp());
    for (int i = 0; i < MAX_NO_CLIENTS; i++)
    {
      ClientSlot clientSelected = ClientList::clients[i];
      if (clientSelected.isEmpty()) {continue;}
      DEBUG_PRINT("Client selected IP: ")
      DEBUG_PRINTLN(clientSelected.getIp());
      DEBUG_PRINT("Is to remove: "); if (!(clientSelected.isEmpty() && clientSelected.getIp() != clientToRemove.getIp())){  DEBUG_PRINTLN("True");  } else {  DEBUG_PRINTLN("False");  }
      if (clientSelected.isEmpty() && clientSelected.getIp() != clientToRemove.getIp())
      {
        newClientList[notEmptyClientsCounter] = clientSelected;
        notEmptyClientsCounter++;
      }
      if (clientSelected.getIp() == clientToRemove.getIp()) ClientList::clients[i].close();
    }
    for (int i = 0; i < MAX_NO_CLIENTS; i++)
    {
      if (i < notEmptyClientsCounter)
      {
        ClientList::clients[i] = newClientList[i];
      } else
      {
        ClientList::clients[i] = ClientSlot();
      }
    }
  }



  void ClientList::checkForConnections()
  {
    DEBUG_PRINTLN("***************************************");
    DEBUG_PRINTLN("Connection slot status:")
    for (int i = 0; i < MAX_NO_CLIENTS; i++)
    { 
      ClientSlot *clientSelected = &ClientList::clients[i];
      DEBUG_PRINTLN(" ");
      DEBUG_PRINT("Slot ");
      DEBUG_PRINT(i);
      DEBUG_PRINT(": ")
      if (clientSelected->isEmpty()){  DEBUG_PRINTLN("empty");  }else{  DEBUG_PRINTLN("occupied");  }
      if (!clientSelected->isEmpty()){
        DEBUG_PRINT("Client: ");
        if (clientSelected->isConnected()){  DEBUG_PRINTLN("connected");  }else{  DEBUG_PRINTLN("disconnected"); ClientList::clients[i] = ClientSlot();  }
        if (clientSelected->isConnected()){  DEBUG_PRINT("IP address: "); DEBUG_PRINTLN(clientSelected->getIp());  }
      }
    }
    DEBUG_PRINTLN("***************************************");
  }

  int ClientList::getNumberOfClients()
  {
    int counter = 0;
    for (int i = 0; i < MAX_NO_CLIENTS; i++)
    {
      if (!ClientList::clients[i].isEmpty())
      {
        counter++;
      }
    }
    return counter;
  }

  ClientSlot ClientList::getClientByIndex(int index)
  {
    return ClientList::clients[index];
  }

  ClientSlot* ClientList::getAllClients()
  {
    return ClientList::clients;
  }
}
