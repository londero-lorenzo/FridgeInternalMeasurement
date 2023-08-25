
#define MAX_NO_CLIENTS 1

#ifndef SOCKET_TCP_CLIENT_LIST_H

#include "ClientSlot.h"

#define SOCKET_TCP_CLIENT_H
namespace Socket{
  class ClientList{
    private:
      ClientSlot clients[MAX_NO_CLIENTS];
      void checkForConnections();
    
    public:
      ClientList();
      void add(ClientSlot *clientToAdd);
      void remove(ClientSlot clientToRemove);
      int getNumberOfClients();
      ClientSlot getClientByIndex(int index);
      ClientSlot* getAllClients();
  };
}
#endif