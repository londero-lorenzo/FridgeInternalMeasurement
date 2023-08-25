


#define MAX_NO_PROBES 6

#ifndef DHT_PROBE_LIST_H

#define DHT_PROBE_LIST_H

#include "DHTCollector.h"
#include "../TaskList/Task.h"
typedef struct {
    void *dhtCollectorList;
    char *taskIdentifier;
} Task_DHTCollectorLinker;


class DHTCollectorList {
private:
    DHTCollector collectorProbes[MAX_NO_PROBES];

    TaskHandle_t taskHandle = nullptr;

    static void backGroundProcess(void *pvParameters);

public:
    DHTCollectorList();

    void add(DHTCollector *probe);

    void collect(char *taskIdentifier);

    int getNumberOfProbes();

    DHTCollector *getAllProbes();

    TaskHandle_t getTaskHandle();
};


#endif
