//
// Created by londe on 21/07/2023.
//

#include <cstdlib>
#include <ctime>
#include <WString.h>
#include <random>
//#include "freertos/task.h"
#include "TaskList.h"
#include "../utils.h"
//#include "../ClientList/ClientSlot.h"



TaskList::TaskList(DHTCollectorList *probeCollectorList): storageDataSDFileSystem(nullptr) {
    for (auto &probesTimer: TaskList::tasks)
        probesTimer = Task();
    TaskList::probeCollectorList = probeCollectorList;
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

bool TaskList::checkForAnotherIdenticalIDTask(const char *taskIdentifier) {
    for (auto &probesTimer: TaskList::tasks) {
        if (*probesTimer.getIdentifier() == *taskIdentifier) return true;
    }
    return false;
}


// TODO: remember that, it's more useful if you do only one scan for all connected clients. (so static)
char *TaskList::createNewTask(int timeDelay) {
    char taskIdentifier[MAX_IDENTIFIER_LENGTH];
    do {
        randomString(taskIdentifier, MAX_IDENTIFIER_LENGTH);
    } while (checkForAnotherIdenticalIDTask(taskIdentifier));
    Task newTask(taskIdentifier, TaskList::probeCollectorList,timeDelay);

    DEBUG_PRINT("Creation task with identifier ")
    DEBUG_PRINT(newTask.getIdentifier())
    DEBUG_PRINTLN("...")
    for (auto &i: TaskList::tasks) {
        Task *probesTimer = &i;
        if (probesTimer->getTimeDelay() == timeDelay) return probesTimer->getIdentifier();
        if (probesTimer->isEmpty()) {
            i = newTask;
            i.setSDFileSystem(TaskList::storageDataSDFileSystem, TaskList::storageDataRoot);
            DEBUG_PRINTLN("Task created!")
            return i.getIdentifier();
        }
    }
    DEBUG_PRINTLN("Task not created! (too many tasks)")
    newTask.getIdentifier()[0] = TASK_NOT_CREATED_FULL;
    return newTask.getIdentifier();
}

int TaskList::removeProbesTimerTask(const char *taskIdentifier) {
    for (auto &probesTimer: TaskList::tasks) {
        if (String(probesTimer.getIdentifier()) == String(taskIdentifier)) {
            if (probesTimer.remove()){
                probesTimer = Task();
                return TASK_REMOVED;
            } else return TASK_NOT_REMOVED_NOT_SAVED;
        }
    }
    return TASK_NOT_REMOVED_NOT_FOUND;
}

Task *TaskList::getTasks() {
    return TaskList::tasks;
}

void TaskList::run() {
    char *prefix = (char *) "probesTimerBackGroundProcess";
    xTaskCreate(TaskList::backGroundProcess, prefix, 16384, (void *) this, 5, &(TaskList::taskHandle));
}

void TaskList::backGroundProcess(void *pvParameters) {
    auto *taskList = (TaskList *) pvParameters;
    Task *currentTask;
    TickType_t xLastWakeTime;
    const TickType_t xFrequency = 10;
    xLastWakeTime = xTaskGetTickCount();
    uint32_t counter = 0;
    while (true) {
        currentTask = taskList->getTasks();
        for (int i = 0; i < MAX_NO_TASKS; i++) {
            if (currentTask->isEmpty()) continue;
            currentTask->checkForCollection(counter, xFrequency);
            currentTask++;
        }
        vTaskDelayUntil(&xLastWakeTime, xFrequency)
        counter++;
    }
}

Task *TaskList::getTask(char *taskIdentifier) {
    for (auto &selectedTask: TaskList::tasks)
        if (String(selectedTask.getIdentifier()) == String(taskIdentifier))
            return &selectedTask;
    return nullptr;
}

DHTCollector *TaskList::getProbeAt(uint8_t index) {
    return TaskList::probeCollectorList->getProbeAt(index);
}


void TaskList::setSDFileSystem(SDFS &fs, const char* root) {
    TaskList::storageDataSDFileSystem = fs;
    TaskList::storageDataRoot = root;

}


/*
void TaskList::collect(char*taskIdentifier) {
    DEBUG_PRINT("Collection from task: ")
    DEBUG_PRINTLN(taskIdentifier)
    TaskList::probeCollectorList->collect(taskIdentifier);
}
*/

/*
 * TODO: timelapse % timeDelay == 0 -> scan
 */
