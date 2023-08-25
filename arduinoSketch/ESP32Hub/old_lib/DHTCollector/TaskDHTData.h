//
// Created by londe on 27/07/2023.
//

#ifndef ESP32HUB_TASKDHTDATA_H
#define ESP32HUB_TASKDHTDATA_H

#include "DHTCollector.h"

#define AUTONOMY_OF_COLLECTION_WITHOUT_SAVING_HH 1


class TaskDHTData {
private:
    DHTCollector *dhtCollector{};
    DHTData dataCollected[AUTONOMY_OF_COLLECTION_WITHOUT_SAVING_HH * 60]{};
    char *taskIdentifier{};
public:

    TaskDHTData();

    explicit TaskDHTData(char *taskIdentifier);

    void collect();


    int getDHTCollectorGPIOPort();

    bool isEmpty();

    void setDHTCollector(DHTCollector *dhtProbe);
};


#endif //ESP32HUB_TASKDHTDATA_H
