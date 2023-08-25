//
// Created by londe on 27/07/2023.
//

#ifndef TASK_DHT_DATA_MANAGER_H
#define TASK_DHT_DATA_MANAGER_H


//#include "../TaskList/ProbesTimerList.h"
#include "TaskDHTDataList.h"


/*
// struct for one probe
typedef struct {
    DHTCollector *dhtCollector;
    DHTData probeData[AUTONOMY_OF_COLLECTION_WITHOUT_SAVING_HH * 60];
}TaskDHTData;
 */


class TaskDHTDataManager {
private:
    char *taskIdentifier{};
    TaskDHTDataList taskDhtDataList;
public:
    TaskDHTDataManager();
    //explicit TaskDHTDataManager(char *taskIdentifier);

    void store(DHTCollector *dhtCollector);

    bool saveOnSD(TaskDHTData *taskDhtData);

    bool removeStoredDataTask();

    void setTaskIdentifier(char * taskIdentifier);

    char *getIdentifier();
};


#endif //TASK_DHT_DATA_MANAGER_H
