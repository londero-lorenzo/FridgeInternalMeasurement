


#include "Task.h"

Task::Task() {
    Task::_isEmpty = true;
    Task::identifier = nullptr;
    Task::probesDataList = nullptr;
    Task::_isInLoadingPhase = false;
    Task::_activationTask = false;
    Task::_forceCollection = false;
    Task::probeCollectorList = nullptr;
    Task::numberOfProbesAssociated = 0;
}

Task::Task(SDMaster &sdMaster, String pathOnDisk){
    Task::_isInLoadingPhase = true;
    Task::taskLoader = TaskLoader(sdMaster, pathOnDisk);
    TaskInfo loadedTaskInfo = Task::taskLoader.getTaskInfo();

    Task::identifier = (char *) malloc((loadedTaskInfo.sizeOfIdentifier + 1) * sizeof(char));
    for (int i = 0; i < loadedTaskInfo.sizeOfIdentifier; i++)
        *(Task::identifier + i) = *(loadedTaskInfo.identifier + i);
    Task::identifier[loadedTaskInfo.sizeOfIdentifier] = '\0';

    Task::timeDelay = loadedTaskInfo.timeDelay;
    Task::numberOfProbesAssociated = loadedTaskInfo.numberOfProbesAssociated;
    Task::probesDataList = (TaskDataList *) malloc((Task::numberOfProbesAssociated) * sizeof(TaskDataList));
    Task::probeCollectorList = (DHTCollectorList *) malloc((Task::numberOfProbesAssociated) * sizeof(DHTCollectorList));
    Task::_isEmpty = false;
    Task::_activationTask = loadedTaskInfo.activationTask;
    Task::_forceCollection = loadedTaskInfo.forceCollection;


    // TODO: check the utility
    Task::startMillisTime = loadedTaskInfo.startMillisTime;
    Task::startCounterTime = loadedTaskInfo.startCounterTime;
    Task::timeToLiveInLoop = loadedTaskInfo.timeToLiveInLoop;
}

Task::Task(const char taskIdentifier[MAX_IDENTIFIER_LENGTH], DHTCollectorList *dhtCollector, int timeDelay) {
    Task::identifier = (char*) malloc((MAX_IDENTIFIER_LENGTH + 1) * sizeof(char));
    for (int i = 0; i < MAX_IDENTIFIER_LENGTH; i++)
        *(Task::identifier + i) = taskIdentifier[i];
    Task::identifier[MAX_IDENTIFIER_LENGTH] = '\0';
    Task::probeCollectorList = dhtCollector;
    Task::timeDelay = timeDelay;
    Task::numberOfProbesAssociated = Task::probeCollectorList->getNumberOfProbes();
    Task::probesDataList = (TaskDataList *) malloc( Task::numberOfProbesAssociated * sizeof(TaskDataList));
    for (int i = 0; i < MAX_NO_PROBES; i++)
        if (Task::probeCollectorList->getProbeAt(i)->isOnline())
            *(Task::probesDataList + i) = TaskDataList(Task::identifier,
                                                   Task::probeCollectorList->getProbeAt(i)->getGPIOPort());
    Task::_isEmpty = false;
    Task::_isInLoadingPhase = false;
    Task::_activationTask = true;
    Task::_forceCollection = false;
    Task::timeToLiveInLoop = -1;
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

uint32_t Task::getTimeToLiveInLoop(uint32_t loopCounter, TickType_t xFrequency) {
    if (Task::timeToLiveInLoop == -1) {
        struct timeval timeVal{};
        gettimeofday(&timeVal, nullptr);
        Task::startMillisTime = (timeVal.tv_sec * 1000LL + (timeVal.tv_usec / 1000LL)); //TODO: get time in ms
        Task::startCounterTime = loopCounter * xFrequency;
    }
    Task::timeToLiveInLoop = loopCounter * xFrequency - Task::startCounterTime;
    return Task::timeToLiveInLoop;
}

uint64_t Task::getTimeToLiveInMillis() const {
    return Task::startMillisTime + Task::timeToLiveInLoop;
}


bool Task::isEmpty() const {
    return Task::_isEmpty;
}

bool Task::isActivated() const {
    return Task::_activationTask;
}



void Task::backGroundProcess(void *pvParameters) {
    auto *task = (Task *) pvParameters;
    DEBUG_PRINTLN("Background process created")
    struct tm timeInfo{};
    getLocalTime(&timeInfo);
    char timeStringBuff[50];
    strftime(timeStringBuff, sizeof(timeStringBuff), "%Y/%m/%d-%H:%M:%S", &timeInfo);
    for (int i = 0; i < MAX_NO_PROBES; i++) {
        DHTCollector *dhtCollector = &(task->getDHTCollectorList()->getAllProbes()[i]);
        if (dhtCollector->isOnline()) {
            DEBUG_PRINT("Task ID: ")
            DEBUG_PRINTLN(task->getIdentifier());
            task->getTaskDataList()[i].store(dhtCollector->collect(timeStringBuff));
        }


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
    if (Task::getTimeToLiveInLoop(loopCounter, xFrequency) % Task::timeDelay == 0 ||
        (_forceCollection && _activationTask)) {
        if (!Task::_activationTask) {
            if (!_forceCollection)
                _forceCollection = true;
        } else {
            _forceCollection = false;
            Task::collectData();
            Task::saveTaskInformation();
        }
    }
}

TaskDataList *Task::getTaskDataList() {
    return Task::probesDataList;
}

void Task::setDHTCollectors(DHTCollectorList *collectorList) {

}


void Task::setSDFileSystem(SDMaster &sdMaster, const String &root) {
    //if (newFS.cardType() == CARD_NONE) {DEBUG_PRINTLN("Error detected on SD card!\nMay SDFS class is not initialized!")}
    String taskRoot = String(root) + Task::identifier;
    sdMaster.mkdir(taskRoot);
    for (int i = 0; i < MAX_NO_PROBES; i++) {
        TaskDataList *taskData = Task::probesDataList + i;
        if (!taskData->isEmpty()) {
            Task::taskInfoFile = FileHandler(sdMaster, taskRoot, TASK_INFO_FILE_NAME, FILE_WRITE);
            String dataRoot = taskRoot + '/' + taskData->getGPIOPortOfProbeAssignedToThisTask();
            sdMaster.mkdir(taskRoot);
            taskData->getTaskDataSaver()->setSDFileSystem(sdMaster, dataRoot);
        }
    }
}


uint8_t Task::getNumberOfProbes() {
    return MAX_NO_PROBES;
}

struct TaskInfo Task::getTaskInfo() {
    struct TaskInfo taskInfo = {
            .sizeOfIdentifier = String(Task::getIdentifier()).length(),
            .identifier = Task::getIdentifier(),
            .timeDelay = Task::timeDelay,
            .activationTask = Task::_activationTask,
            .forceCollection = Task::_forceCollection,
            .startMillisTime = Task::startMillisTime,
            .timeToLiveInLoop = Task::timeToLiveInLoop,
            .numberOfProbesAssociated = Task::probeCollectorList->getNumberOfProbes()
    };
    return taskInfo;
}

void Task::saveTaskInformation() {
    Task::taskLoader.saveTaskInfo(Task::getTaskInfo());
}
