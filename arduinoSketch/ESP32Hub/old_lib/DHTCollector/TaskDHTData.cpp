//
// Created by londe on 27/07/2023.
//

#include "TaskDHTData.h"

TaskDHTData::TaskDHTData(char *taskIdentifier) {
    for (auto &dhtData: TaskDHTData::dataCollected) {
        dhtData = defaultDHTData;
    }
    TaskDHTData::dhtCollector = nullptr;
}

void TaskDHTData::collect() {
    for (auto &taskDHTData: TaskDHTData::dataCollected) {
        if (taskDHTData.isEmpty) {
            DHTData dhtData = dhtCollector->collect();
            DEBUG_PRINTLN("***************************************")
            DEBUG_PRINT("> Collecting data from DHT allocated at GPIO port ") DEBUG_PRINT(dhtCollector->getGPIOPort()) DEBUG_PRINTLN("... <")
            DEBUG_PRINT("Description: ") DEBUG_PRINTLN(dhtCollector->getDescription())
            DEBUG_PRINT("Task: ") DEBUG_PRINTLN(TaskDHTData::taskIdentifier);
            DEBUG_PRINT("Temperature: ") DEBUG_PRINTLN(dhtData.temperature)
            DEBUG_PRINT("Humidity: ") DEBUG_PRINTLN(dhtData.humidity)
            DEBUG_PRINT("Heat index: ") DEBUG_PRINTLN(dhtData.heatIndex)
            DEBUG_PRINTLN("***************************************")
            taskDHTData = dhtData;
            break;
        }
    }
}

bool TaskDHTData::isEmpty() {
    if (TaskDHTData::dhtCollector == nullptr) return true;
    return false;
}


void TaskDHTData::setDHTCollector(DHTCollector *dhtProbe) {
    TaskDHTData::dhtCollector = dhtProbe;
}

int TaskDHTData::getDHTCollectorGPIOPort() {
    return TaskDHTData::dhtCollector->getGPIOPort();
}

TaskDHTData::TaskDHTData() = default;


