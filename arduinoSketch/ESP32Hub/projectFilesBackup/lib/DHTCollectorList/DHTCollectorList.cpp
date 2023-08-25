
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


DHTCollector *DHTCollectorList::getAllProbes() {
    return DHTCollectorList::collectorProbes;
}

DHTCollector *DHTCollectorList::getProbeAt(uint8_t index) {
    return &DHTCollectorList::collectorProbes[index];
}

uint8_t DHTCollectorList::getPositionOfProbeByGPIOPort(uint8_t GPIOPort) {
    for (int i = 0; i < MAX_NO_PROBES; i++)
        if (DHTCollectorList::collectorProbes[i].getGPIOPort() == GPIOPort)
            return i;
    return UINT8_MAX;
}





