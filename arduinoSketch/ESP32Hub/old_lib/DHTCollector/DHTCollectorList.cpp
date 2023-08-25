
#include "DHTCollectorList.h"
//#include "../ClientList/ClientSlot.h"

DHTCollectorList::DHTCollectorList() {
    for (auto &collectorProbe: DHTCollectorList::collectorProbes)
        collectorProbe = DHTCollector();
}

void DHTCollectorList::add(DHTCollector *probe) {
    for (int i = 0; i < MAX_NO_PROBES; i++) {
        DHTCollector *DHTProbe = &DHTCollectorList::collectorProbes[i];
        if (DHTProbe->isEmpty()) {
            DEBUG_PRINT("Adding DHT [Description: ")
            DEBUG_PRINT(probe->getDescription())
            DEBUG_PRINT("; GPIO:")
            DEBUG_PRINT(probe->getGPIOPort())
            DEBUG_PRINT("; Status:")
            if (probe->isOnline()) DEBUG_PRINT(" ONLINE") else DEBUG_PRINT(" OFFLINE")
            DEBUG_PRINT("] at slot ")
            DEBUG_PRINTLN(i)
            DHTCollectorList::collectorProbes[i] = *probe;

            break;
        }
    }

}


int DHTCollectorList::getNumberOfProbes() {
    int counter = 0;
    for (auto &collectorProbe: DHTCollectorList::collectorProbes)
        if (collectorProbe.isEmpty())
            counter++;
    return counter;
}


void DHTCollectorList::collect(char *taskIdentifier) {
    char *prefix = (char *) "DHTCollectionBackGroundProcess";
    DEBUG_PRINTLN("Creating TASK-DHT linker...")
    // this variable is set to static in order to avoid the destruction of itself when passed in the thread
    static Task_DHTCollectorLinker linker = {.dhtCollectorList = (void *) this, .taskIdentifier = taskIdentifier};
    DEBUG_PRINT("Linker created with id: ")
    DEBUG_PRINTLN(linker.taskIdentifier)
    DEBUG_PRINTLN("Creating DHT collector background process...")
    xTaskCreate(DHTCollectorList::backGroundProcess, prefix, 4096, (void *) &linker, 5,
                &(DHTCollectorList::taskHandle));
}

void DHTCollectorList::backGroundProcess(void *pvParameters) {
    DEBUG_PRINTLN("Background process created")
    auto *linker = (Task_DHTCollectorLinker *) pvParameters;
    auto *dhtCollectorList = (DHTCollectorList *) linker->dhtCollectorList;
    char *taskIdentifier = linker->taskIdentifier;
    for (int i = 0; i < MAX_NO_PROBES; i++) {
        DHTCollector *dhtCollector = &dhtCollectorList->getAllProbes()[i];
        if (dhtCollector->isOnline())
            dhtCollectorList->getTaskDHTDataManager()->store(dhtCollector);

        //TODO DO STORAGE
    }
    vTaskDelete(dhtCollectorList->getTaskHandle());
}

DHTCollector *DHTCollectorList::getAllProbes() {
    return DHTCollectorList::collectorProbes;
}

TaskHandle_t DHTCollectorList::getTaskHandle() {
    return DHTCollectorList::taskHandle;
}



