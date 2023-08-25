//
// Created by londe on 29/07/2023.
//

#include "TaskDataList.h"



TaskDataList::TaskDataList() : probeGPIOPort(UINT8_MAX) {}

TaskDataList::TaskDataList(char *taskIdentifier, uint8_t probePort) {
    TaskDataList::taskIdentifier = taskIdentifier;
    TaskDataList::probeGPIOPort = probePort;
    TaskDataList::dataSaver = TaskDataSaver(TaskDataList::taskIdentifier);
    for (auto &dhtData: TaskDataList::data)
        dhtData = defaultDHTData;
}


void TaskDataList::store(DHTData collectedData) {
    TaskDataList::data[0] = collectedData;
    for (int i = DATASPACE - 1; i > 0; i--)
        TaskDataList::data[i] = TaskDataList::data[i - 1];
    TaskDataList::data[0] = defaultDHTData;
    TaskDataList::dataSaver.save(&collectedData);
}

uint32_t TaskDataList::getNumberOfDataInBuff() {
    for (int i = 1; i < DATASPACE; i++) {
        DHTData *currentData = &TaskDataList::data[i];
        if (currentData->empty) return i;
    }
    return DATASPACE;
}


uint32_t TaskDataList::getNumberOfData() {
    return TaskDataList::dataSaver.getNumberOfDataSaved();
}

DHTData *TaskDataList::getDataInBuffer() {
    return TaskDataList::data;
}

TaskDataSaver *TaskDataList::getTaskDataSaver() {
    return &(TaskDataList::dataSaver);
}

uint8_t TaskDataList::getGPIOPortOfProbeAssignedToThisTask() const{
    return TaskDataList::probeGPIOPort;
}

bool TaskDataList::isEmpty() const {
    return TaskDataList::probeGPIOPort == UINT8_MAX;
}
