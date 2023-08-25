//
// Created by londe on 21/07/2023.
//

#include <cstdlib>
#include <ctime>
#include <WString.h>
#include <random>
//#include "freertos/task.h"
#include "ProbesTimerList.h"
#include "../utils.h"
//#include "../ClientList/ClientSlot.h"



ProbesTimerList::ProbesTimerList(DHTCollectorList *probeCollector) {
    for (auto &probesTimer: ProbesTimerList::probesTimers)
        probesTimer = ProbesTimer();
    ProbesTimerList::probeCollectorList = probeCollector;
}


void randomString(char *stringDestination, size_t length) {
    const char charset[] = "0123456789"
                           "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    DEBUG_PRINT("Address of stringDestination ->")
    DEBUG_PRINTLN((unsigned int) stringDestination, HEX)
    while (length-- > 0) {
        size_t index = random(0, (long) strlen(charset));
        *stringDestination++ = charset[index];
    }

    *stringDestination = '\0'; //added right ending of destination c-string
}

bool ProbesTimerList::checkForAnotherIdenticalIDTask(const char *taskIdentifier) {
    for (auto &probesTimer: ProbesTimerList::probesTimers) {
        if (*probesTimer.getIdentifier() == *taskIdentifier) return true;
    }
    return false;
}


// TODO: remember that, it's more useful if you do only one scan for all connected clients. (so static)
char *ProbesTimerList::createNewProbesTimerTask(int timeDelay) {
    ProbesTimer newProbesTimer(timeDelay);
    do {
        randomString(newProbesTimer.getIdentifier(), MAX_IDENTIFIER_LENGTH);
    } while (checkForAnotherIdenticalIDTask(newProbesTimer.getIdentifier()));
    DEBUG_PRINT("Creation task with identifier ")
    DEBUG_PRINT(newProbesTimer.getIdentifier())
    DEBUG_PRINTLN("...")
    for (auto &i: ProbesTimerList::probesTimers) {
        ProbesTimer *probesTimer = &i;
        if (probesTimer->getTimeDelay() == timeDelay) return probesTimer->getIdentifier();
        if (probesTimer->isEmpty()) {
            i = newProbesTimer;
            DEBUG_PRINTLN("Task created!")
            return i.getIdentifier();
        }
    }
    DEBUG_PRINTLN("Task not created! (too many tasks)")
    newProbesTimer.getIdentifier()[0] = TASK_NOT_CREATED_FULL;
    return newProbesTimer.getIdentifier();
}

int ProbesTimerList::removeProbesTimerTask(const char *taskIdentifier) {
    for (auto &probesTimer: ProbesTimerList::probesTimers) {
        if (String(probesTimer.getIdentifier()) == String(taskIdentifier)) {
            if (ProbesTimerList::probeCollectorList->getTaskDHTDataManager()->removeStoredDataTask()){
            probesTimer = ProbesTimer();
            return TASK_REMOVED;
            } else return TASK_NOT_REMOVED_NOT_SAVED;
        }
    }
    return TASK_NOT_REMOVED_NOT_FOUND;
}

ProbesTimer *ProbesTimerList::getProbesTimers() {
    return ProbesTimerList::probesTimers;
}

void ProbesTimerList::run() {
    char *prefix = (char *) "probesTimerBackGroundProcess";
    xTaskCreate(ProbesTimerList::backGroundProcess, prefix, 4096, (void *) this, 5, &(ProbesTimerList::taskHandle));
}

void ProbesTimerList::backGroundProcess(void *pvParameters) {
    auto *probesTimerList = (ProbesTimerList *) pvParameters;
    ProbesTimer *probesTimer;
    TickType_t xLastWakeTime;
    const TickType_t xFrequency = 10;
    xLastWakeTime = xTaskGetTickCount();
    int counter = 0;
    while (true) {
        probesTimer = probesTimerList->getProbesTimers();
        for (int i = 0; i < MAX_NO_PROBES_TIMER_TASK; i++) {
            if (probesTimer->isEmpty()) continue;
            if (counter % (int(probesTimer->getTimeDelay() / xFrequency)) == 0)
                probesTimerList->collect(probesTimer->getIdentifier());
            probesTimer++;
        }
        vTaskDelayUntil(&xLastWakeTime, xFrequency)
        counter++;
    }
}

void ProbesTimerList::collect(char*taskIdentifier) {
    DEBUG_PRINT("Collection from task: ")
    DEBUG_PRINTLN(taskIdentifier)
    ProbesTimerList::probeCollectorList->collect(taskIdentifier);
}


/*
 * TODO: timelapse % timeDelay == 0 -> scan
 */





