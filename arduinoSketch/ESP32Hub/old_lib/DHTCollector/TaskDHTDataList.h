//
// Created by londe on 27/07/2023.
//

#ifndef ESP32HUB_TASKDHTDATALIST_H
#define ESP32HUB_TASKDHTDATALIST_H

#include "TaskDHTData.h"
//#include "DHTCollectorList.h"


class TaskDHTDataList {
private:
    char* taskIdentifier{};

    TaskDHTData taskDHTData[3];

    void checkForNewProbes(DHTCollector *probe);

    TaskDHTData getTaskDHTDataFromProbe(DHTCollector *probe);

public:
    explicit TaskDHTDataList(char * taskIdentifier);

    TaskDHTDataList();

    void collectFromProbe(DHTCollector *probe);
};


#endif //ESP32HUB_TASKDHTDATALIST_H
