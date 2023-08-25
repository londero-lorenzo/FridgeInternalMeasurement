//
// Created by londe on 29/07/2023.
//

#ifndef ESP32HUB_TASKDATALIST_H
#define ESP32HUB_TASKDATALIST_H


#include "TaskDataSaver.h"
//#include "TaskList.h"
//#include "../DHTCollectorList/DHTCollector.h"

#define AUTONOMY_OF_COLLECTION_WITHOUT_SAVING_HH 1
#define MIN_TIME_DELAY                          (30000) //in ms to allow a correct reading by all probes



#define DATASPACE 10//(((AUTONOMY_OF_COLLECTION_WITHOUT_SAVING_HH * 3600) * 1000)/MIN_TIME_DELAY)

class TaskDataList {
private:
    TaskDataSaver dataSaver;
    char *taskIdentifier{};
    uint8_t probeGPIOPort;
    DHTData data[DATASPACE]{};
public:
    TaskDataList();

    explicit TaskDataList(char *taskIdentifier, uint8_t probePort);

    void store(DHTData collectedData);

    DHTData *getDataInBuffer();

    uint32_t getNumberOfDataInBuff();

    uint32_t getNumberOfData();

    TaskDataSaver *getTaskDataSaver();

    uint8_t getGPIOPortOfProbeAssignedToThisTask() const;

    bool isEmpty() const;
};


#endif //ESP32HUB_TASKDATALIST_H
