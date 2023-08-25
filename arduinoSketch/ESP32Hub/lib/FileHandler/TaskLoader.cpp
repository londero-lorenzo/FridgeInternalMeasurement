//
// Created by londe on 07/08/2023.
//

#include "TaskLoader.h"

#include <utility>

TaskLoader::TaskLoader(){
    TaskLoader::pathInfoTaskFile = "";
    TaskLoader::InfoTaskFile = FileHandler();
}

TaskLoader::TaskLoader(SDMaster &sdMaster, String &taskRoot) {
    TaskLoader::sdMaster = sdMaster;
    TaskLoader::pathInfoTaskFile = taskRoot;
    TaskLoader::InfoTaskFile = FileHandler(TaskLoader::sdMaster, TaskLoader::pathInfoTaskFile, TASK_INFO_FILE_NAME,
                                           FILE_READ);
}

TaskInfo TaskLoader::getTaskInfo() {
    struct TaskInfo taskInfo = defaultTaskInfo;
    String line;
    String infoKey;
    String taskData;
    int indexOfKeySeparator;
    do{
        line = TaskLoader::InfoTaskFile.getNextLine();
        indexOfKeySeparator = line.indexOf(':');
        infoKey = line.substring(0, indexOfKeySeparator);
        taskData = line.substring(indexOfKeySeparator, line.length() - 1);
        if (infoKey == TASK_IDENTIFIER_SIZE_INFO_KEY)
            taskInfo.sizeOfIdentifier = taskData.toInt();
        else if (infoKey == TASK_IDENTIFIER_INFO_KEY)
            taskInfo.identifier = taskData.begin();
        else if (infoKey == TIME_DELAY_INFO_KEY)
            taskInfo.timeDelay = taskData.toInt();
        else if (infoKey == ACTIVATION_TASK_INFO_KEY)
            taskInfo.activationTask = (bool) taskData.toInt();
        else if (infoKey == FORCE_COLLECTION_INFO_KEY)
            taskInfo.forceCollection = (bool) taskData.toInt();
        else if (infoKey == START_MILLIS_TIME_INFO_KEY)
            taskInfo.startMillisTime = taskData.toInt();
        else if (infoKey == START_COUNTER_INFO_KEY)
            taskInfo.startCounterTime = taskData.toInt();
        else if (infoKey == TIME_TO_LIVE_IN_LOOP_INFO_KEY)
            taskInfo.timeToLiveInLoop = taskData.toInt();
        else if (infoKey == NUMBER_OF_PROBES_ASSOCIATED_INFO_KEY)
            taskInfo.numberOfProbesAssociated = taskData.toInt();
    } while (line!="");

    return taskInfo;
}

void TaskLoader::saveTaskInfo(TaskInfo taskInfo) {
    String info;
    info += TASK_IDENTIFIER_SIZE_INFO_KEY           + String(taskInfo.sizeOfIdentifier)         + LINE_SEPARATOR;
    info += TASK_IDENTIFIER_INFO_KEY                + String(taskInfo.identifier)          + LINE_SEPARATOR;
    info += TIME_DELAY_INFO_KEY                     + String(taskInfo.timeDelay)                + LINE_SEPARATOR;
    info += ACTIVATION_TASK_INFO_KEY                + String(taskInfo.activationTask)           + LINE_SEPARATOR;
    info += FORCE_COLLECTION_INFO_KEY               + String(taskInfo.forceCollection)          + LINE_SEPARATOR;
    info += START_MILLIS_TIME_INFO_KEY              + String(taskInfo.startMillisTime)          + LINE_SEPARATOR;
    info += START_COUNTER_INFO_KEY                  + String(taskInfo.startCounterTime)         + LINE_SEPARATOR;
    info += TIME_TO_LIVE_IN_LOOP_INFO_KEY           + String(taskInfo.timeToLiveInLoop)         + LINE_SEPARATOR;
    info += NUMBER_OF_PROBES_ASSOCIATED_INFO_KEY    + String(taskInfo.numberOfProbesAssociated) + LINE_SEPARATOR;
    TaskLoader::InfoTaskFile.write(info);
}


