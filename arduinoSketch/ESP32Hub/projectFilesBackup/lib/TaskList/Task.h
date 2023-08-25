
#ifndef TASK_H

#define TASK_H

#include "TaskDataList.h"
#include "../DHTCollectorList/DHTCollectorList.h"

#define MAX_IDENTIFIER_LENGTH 5
#define TASK_INFO_FILE_NAME "task.inf"

class Task {
private:
    int timeDelay{};
    char identifier[MAX_IDENTIFIER_LENGTH + 1]{};
    TaskDataList probesDataList[MAX_NO_PROBES]{};
    DHTCollectorList *probeCollectorList;
    TaskHandle_t taskHandle = nullptr;
    bool _isEmpty;
    bool _activationTask;
    uint32_t startCounterTime{};
    uint32_t timeToLive{};
    FileHandler taskInfoFile;

public:
    Task();

    explicit Task(const char taskIdentifier[MAX_IDENTIFIER_LENGTH], DHTCollectorList *dhtCollector, int timeDelay);

    void checkForCollection(uint32_t loopCounter, TickType_t xFrequency);

    void collectData();

    static void backGroundProcess(void *pvParameters);

    void setSDFileSystem(SDFS &newFS, const String& root);

    char *getIdentifier();

    int getTimeDelay() const;

    uint32_t getTimeToLive(uint32_t loopCounter, TickType_t xFrequency);

    DHTCollectorList *getDHTCollectorList();

    TaskDataList *getTaskDataList();

    TaskHandle_t getTaskHandle();

    uint8_t getNumberOfProbes();

    bool remove();

    bool isEmpty() const;
};

#endif