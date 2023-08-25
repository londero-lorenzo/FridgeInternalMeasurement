
#ifndef TASK_H

#define TASK_H

#include "TaskDataList.h"
#include "../DHTCollectorList/DHTCollectorList.h"
#include "../FileHandler/TaskLoader.h"

#define MAX_IDENTIFIER_LENGTH 5


class Task {
private:
    int timeDelay{};
    char *identifier;
    TaskDataList *probesDataList;
    uint8_t numberOfProbesAssociated;
    DHTCollectorList *probeCollectorList;
    TaskHandle_t taskHandle = nullptr;
    bool _isEmpty;
    bool _activationTask;
    bool _forceCollection;
    bool _isInLoadingPhase;
    uint64_t startMillisTime{}; //keep all resume times
    uint32_t startCounterTime{};
    uint32_t timeToLiveInLoop{};
    TaskLoader taskLoader;
    FileHandler taskInfoFile;

public:
    Task();

    explicit Task(const char taskIdentifier[MAX_IDENTIFIER_LENGTH], DHTCollectorList *dhtCollector, int timeDelay);

    explicit Task(SDMaster &sdMaster, String pathOnDisk);

    void checkForCollection(uint32_t loopCounter, TickType_t xFrequency);

    void collectData();

    static void backGroundProcess(void *pvParameters);

    void setSDFileSystem(SDMaster &sdMaster, const String& root);

    void setDHTCollectors(DHTCollectorList *collectorList);

    void saveTaskInformation();

    char *getIdentifier();

    int getTimeDelay() const;

    uint32_t getTimeToLiveInLoop(uint32_t loopCounter, TickType_t xFrequency);

    uint64_t getTimeToLiveInMillis() const;

    DHTCollectorList *getDHTCollectorList();

    TaskDataList *getTaskDataList();

    TaskHandle_t getTaskHandle();

    uint8_t getNumberOfProbes();

    bool remove();

    bool isEmpty() const;

    bool isActivated() const;

    struct TaskInfo getTaskInfo();
};

#endif