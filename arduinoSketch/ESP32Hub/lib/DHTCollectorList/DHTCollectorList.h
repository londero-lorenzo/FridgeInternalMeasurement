


#define MAX_NO_PROBES 6

#ifndef DHT_PROBE_LIST_H

#define DHT_PROBE_LIST_H

#include "DHTCollector.h"
//#include "../TaskList/Task.h"

class DHTCollectorList {
private:
    DHTCollector collectorProbes[MAX_NO_PROBES];

public:
    DHTCollectorList();

    void add(DHTCollector *probe);

    uint8_t getNumberOfProbes();

    uint8_t getPositionOfProbeByGPIOPort(uint8_t GPIOPort);

    DHTCollector *getAllProbes();

    DHTCollector *getProbeAt(uint8_t index);
};


#endif
