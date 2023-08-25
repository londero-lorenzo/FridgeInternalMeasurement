//
// Created by londe on 21/07/2023.
//

#ifndef SERVER_PROBESTIMERLIST_H
#define SERVER_PROBESTIMERLIST_H

#define MAX_NO_TASKS                            (4)

#define TASK_CREATED                            (40)  // in order to avoid conflict between task identifier printable chars
#define TASK_NOT_CREATED_FULL                   (41)
#define TASK_NOT_CREATED_NOT_INT                (42)
#define TASK_NOT_CREATED_MIN_TIME_DELAY         (43)

#define TASK_REMOVED                            (51  + '0')
#define TASK_NOT_REMOVED_NOT_FOUND              (52  + '0')
#define TASK_NOT_REMOVED_NOT_SAVED              (53  + '0')

#define WAITING_FOR_RESPONSE                    (126)

//#include <utils.h>
#include <freertos/FreeRTOS.h>
#include <freertos/task.h>
#include "Task.h"
//#include "../DHTCollectorList/DHTCollectorList.h"
class TaskList {
private:
    Task tasks[MAX_NO_TASKS];
    TaskHandle_t taskHandle = nullptr;
    SDMaster storageDataSDFileSystem;
    String storageDataRoot{};
    DHTCollectorList *probeCollectorList{};

    static void backGroundProcess(void *pvParameters);

public:
    explicit TaskList(DHTCollectorList *probeCollectorList);

    bool checkForAnotherIdenticalIDTask(const char* taskIdentifier);

    char* createNewTask(int timeDelay);

    bool addNewTask(Task &createdTask);

    int removeProbesTimerTask(const char * taskIdentifier);

    void loadFromDisk();

    void run();

    Task *getTasks();

    DHTCollector *getProbeAt(uint8_t index);

    Task *getTask(char *taskIdentifier);

    void setSDFileSystem(SDMaster &sdMaster, const char* root);



};

#endif //SERVER_PROBESTIMERLIST_H
