


#include "Task.h"

Task::Task() {
    Task::_isEmpty = true;
    Task::_activationTask = false;
    Task::probeCollectorList = nullptr;
}

Task::Task(const char taskIdentifier[MAX_IDENTIFIER_LENGTH], DHTCollectorList *dhtCollector, int timeDelay) {
    for (int i = 0; i < MAX_IDENTIFIER_LENGTH; i++)
        Task::identifier[i] = taskIdentifier[i];
    Task::identifier[MAX_IDENTIFIER_LENGTH] = '\0';
    Task::probeCollectorList = dhtCollector;
    Task::timeDelay = timeDelay;
    for (int i = 0; i < MAX_NO_PROBES; i++)
        if (Task::probeCollectorList->getProbeAt(i)->isOnline())
            Task::probesDataList[i] = TaskDataList(Task::identifier, Task::probeCollectorList->getProbeAt(i)->getGPIOPort());
    Task::_isEmpty = false;
    Task::_activationTask = true;
    Task::timeToLive = -1;
}

void Task::collectData() {
    char *prefix = (char *) "CollectionBackGroundProcess";
    DEBUG_PRINTLN("Creating collector background process...")
    xTaskCreate(Task::backGroundProcess, prefix, 16384, (void *) this, 5,
                &(Task::taskHandle));
}



char *Task::getIdentifier() {
    return &Task::identifier[0];
}

int Task::getTimeDelay() const {
    return Task::timeDelay;
}

uint32_t Task::getTimeToLive(uint32_t loopCounter, TickType_t xFrequency) {
    if (Task::timeToLive == -1) {
        Task::startCounterTime = loopCounter * xFrequency; //TODO: get time in ms
    }
    Task::timeToLive = loopCounter * xFrequency - Task::startCounterTime;
    return Task::timeToLive;
}

bool Task::isEmpty() const {
    return Task::_isEmpty;
}

void Task::backGroundProcess(void *pvParameters) {
    auto *task = (Task *) pvParameters;
    DEBUG_PRINTLN("Background process created")
    for (int i = 0; i < MAX_NO_PROBES; i++) {
        DHTCollector *dhtCollector = &(task->getDHTCollectorList()->getAllProbes()[i]);
        if (dhtCollector->isOnline()) {
            DEBUG_PRINT("Task ID: ") DEBUG_PRINTLN(task->getIdentifier());
            task->getTaskDataList()[i].store(dhtCollector->collect(task->getTaskDataList()[i].getNumberOfData())); }

        //TODO DO STORAGE
    }
    vTaskDelete(task->getTaskHandle());
}

TaskHandle_t Task::getTaskHandle() {
    return Task::taskHandle;
}

DHTCollectorList *Task::getDHTCollectorList() {
    return Task::probeCollectorList;
}

bool Task::remove() {
    Task::_activationTask = false;
    vTaskDelay(250);
    Task::_isEmpty = true;
    Task::taskInfoFile.close();
    // TODO: CHECK DATA ON FILE (optional)
    // CLOSE ALSO TaskDataSaver.h
    return true;
}

void Task::checkForCollection(uint32_t loopCounter, TickType_t xFrequency) {
    if (Task::getTimeToLive(loopCounter, xFrequency) % Task::timeDelay == (int) !Task::_activationTask)
        Task::collectData();
}

TaskDataList *Task::getTaskDataList() {
    return Task::probesDataList;
}

void Task::setSDFileSystem(SDFS &newFS, const String& root){
    newFS.mkdir(String(root) + Task::identifier);
    for (auto & i : Task::probesDataList) {
        if (!i.isEmpty()) {
            String newRoot = String(root) + Task::identifier + '/' + i.getGPIOPortOfProbeAssignedToThisTask();
            newFS.mkdir(newRoot);
            Task::taskInfoFile = FileHandler(&newFS,newRoot + '/' + TASK_INFO_FILE_NAME, FILE_WRITE);
            String rawInfoToSave;
            rawInfoToSave += "ID:" + String(Task::identifier) + '\n';
            rawInfoToSave += "TD:" + String(Task::timeDelay) + '\n';
            rawInfoToSave += "AC" + String(Task::_activationTask) + '\n';
            rawInfoToSave +=
            Task::taskInfoFile.write(rawInfoToSave);
            i.getTaskDataSaver()->setSDFileSystem(newFS, newRoot + '/');
        }
    }
}


uint8_t Task::getNumberOfProbes() {
    return MAX_NO_PROBES;
}