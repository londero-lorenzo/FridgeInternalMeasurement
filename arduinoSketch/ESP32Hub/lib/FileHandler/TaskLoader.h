//
// Created by londe on 07/08/2023.
//

#ifndef ESP32HUB_TASKLOADER_H
#define ESP32HUB_TASKLOADER_H

#include "../utils.h"
#include "FileHandler.h"

#define TASK_INFO_FILE_NAME "task.inf"


#define TASK_IDENTIFIER_SIZE_INFO_KEY            "IDS:"
#define TASK_IDENTIFIER_INFO_KEY                 "ID:"
#define TIME_DELAY_INFO_KEY                      "TD:"
#define ACTIVATION_TASK_INFO_KEY                 "AC:"
#define FORCE_COLLECTION_INFO_KEY                "FC:"
#define START_MILLIS_TIME_INFO_KEY               "SM:"
#define START_COUNTER_INFO_KEY                   "SC:"
#define TIME_TO_LIVE_IN_LOOP_INFO_KEY            "TL:"
#define NUMBER_OF_PROBES_ASSOCIATED_INFO_KEY     "NOP:"

struct TaskInfo{
    unsigned int sizeOfIdentifier;
    char *identifier;
    int timeDelay;
    bool activationTask;
    bool forceCollection;
    uint64_t startMillisTime;
    uint32_t startCounterTime;
    uint32_t timeToLiveInLoop;
    uint8_t numberOfProbesAssociated;
    //uint8_t *GPIOProbesPort;
};

static struct TaskInfo defaultTaskInfo = {.sizeOfIdentifier = 0, .identifier = nullptr, .timeDelay = -1, .activationTask = false,
        .forceCollection = false, .startMillisTime = 0, .startCounterTime = 0, .timeToLiveInLoop = 0, .numberOfProbesAssociated = 0/*, .GPIOProbesPort = 0*/};

class TaskLoader {
private:
    String pathInfoTaskFile;
    FileHandler InfoTaskFile;
    SDMaster sdMaster;
public:
    TaskLoader();
    explicit TaskLoader(SDMaster &sdMaster, String &taskRoot);
    void saveTaskInfo(TaskInfo taskInfo);
    TaskInfo getTaskInfo();
};


#endif //ESP32HUB_TASKLOADER_H
