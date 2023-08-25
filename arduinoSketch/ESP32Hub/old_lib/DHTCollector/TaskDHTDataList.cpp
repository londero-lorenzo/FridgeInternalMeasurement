//
// Created by londe on 27/07/2023.
//

#include "TaskDHTDataList.h"

TaskDHTDataList::TaskDHTDataList(char *taskIdentifier) {
    TaskDHTDataList::taskIdentifier = taskIdentifier;
    for (auto &currentDHTData: TaskDHTDataList::taskDHTData) {
        currentDHTData = TaskDHTData(TaskDHTDataList::taskIdentifier);
    }
}


void TaskDHTDataList::collectFromProbe(DHTCollector *probe) {
    TaskDHTDataList::checkForNewProbes(probe);
    TaskDHTDataList::getTaskDHTDataFromProbe(probe).collect();
}

void TaskDHTDataList::checkForNewProbes(DHTCollector *probe) {
    for (auto &currentDHTData: TaskDHTDataList::taskDHTData) {
        if (currentDHTData.isEmpty()) {
            currentDHTData.setDHTCollector(probe);
            break;
        }
    }
}

TaskDHTData TaskDHTDataList::getTaskDHTDataFromProbe(DHTCollector *probe) {
    for (auto &currentDHTData: TaskDHTDataList::taskDHTData) {
        if (currentDHTData.getDHTCollectorGPIOPort() == probe->getGPIOPort()) {
            return currentDHTData;
        }
    }
    return TaskDHTData{};
}

TaskDHTDataList::TaskDHTDataList() = default;


