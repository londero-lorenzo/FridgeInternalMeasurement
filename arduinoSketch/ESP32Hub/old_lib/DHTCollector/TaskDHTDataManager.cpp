//
// Created by londe on 27/07/2023.
//

#include "TaskDHTDataManager.h"

TaskDHTDataManager::TaskDHTDataManager() = default;


void TaskDHTDataManager::store(DHTCollector *dhtCollector) {
    TaskDHTDataManager::taskDhtDataList.collectFromProbe(dhtCollector);

    // TODO TaskDHTData has dhtCOllector pointer = NULL

}


bool TaskDHTDataManager::removeStoredDataTask() {
    //bool result = TaskDHTDataManager::saveOnSD(taskDhtData);
    //if (!result) return result;
    //for (auto &taskDataStruct: TaskDHTDataManager::taskDhtData)
    //    taskDataStruct = TaskDHTData{};
    return true;
}

bool TaskDHTDataManager::saveOnSD(TaskDHTData *taskDhtData) {
    // TODO: save all dht data struct on SD
    return true;
}

char *TaskDHTDataManager::getIdentifier() {
    return TaskDHTDataManager::taskIdentifier;
}

void TaskDHTDataManager::setTaskIdentifier(char * taskIdentifier) {
    TaskDHTDataManager::taskIdentifier = taskIdentifier;
    TaskDHTDataManager::taskDhtDataList = TaskDHTDataList(taskIdentifier);
}

