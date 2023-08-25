//
// Created by londe on 21/07/2023.
//

#ifndef SERVER_PROBESTIMERLIST_H
#define SERVER_PROBESTIMERLIST_H

#define MAX_NO_PROBES_TIMER_TASK                (4)
#define MIN_TIME_DELAY                          (5000) //in ms to allow a correct reading by all probes

#define TASK_CREATED                            (40)  // in order to avoid conflict between task identifier printable chars
#define TASK_NOT_CREATED_FULL                   (41)
#define TASK_NOT_CREATED_NOT_INT                (42)
#define TASK_NOT_CREATED_MIN_TIME_DELAY         (43)

#define TASK_REMOVED                            (51  + '0')
#define TASK_NOT_REMOVED_NOT_FOUND              (52  + '0')
#define TASK_NOT_REMOVED_NOT_SAVED              (53  + '0')

//#include <utils.h>
#include <freertos/FreeRTOS.h>
#include <freertos/task.h>
#include "ProbesTimer.h"
#include "../DHTCollectorList/DHTCollectorList.h"
class ProbesTimerList {
private:
    ProbesTimer probesTimers[MAX_NO_PROBES_TIMER_TASK];
    TaskHandle_t taskHandle = nullptr;

    DHTCollectorList *probeCollectorList;

    static void backGroundProcess(void *pvParameters);

public:

    explicit ProbesTimerList(DHTCollectorList *probeCollector);

    bool checkForAnotherIdenticalIDTask(const char* taskIdentifier);

    char* createNewProbesTimerTask(int timeDelay);

    int removeProbesTimerTask(const char * taskIdentifier);

    void run();

    void collect(char *taskIdentifier);

    ProbesTimer *getProbesTimers();

};

#endif //SERVER_PROBESTIMERLIST_H
