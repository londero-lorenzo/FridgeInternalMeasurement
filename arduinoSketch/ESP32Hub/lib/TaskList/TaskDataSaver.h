//
// Created by londe on 29/07/2023.
//

#ifndef ESP32HUB_TASKDATASAVER_H
#define ESP32HUB_TASKDATASAVER_H

#include "../FileHandler/FileHandler.h"
#include "../DHTCollectorList/DHTCollector.h"
#define CS_PIN 5

class TaskDataSaver {
private:
    uint64_t NumberOfDataSaved;
    String taskIdentifier;
    String dataStorageDirectory;
    SDMaster fileSystem;
    FileHandler taskDataFile;
public:
    TaskDataSaver();

    void setSDFileSystem(SDMaster &sdMaster, const String& root);

    //void checkForDataFile();

    explicit TaskDataSaver(const char *taskIdentifier);

    uint64_t readNumberOfDataSaved();

    uint64_t getNumberOfDataSaved() const;

    void save(DHTData *data);
};


#endif //ESP32HUB_TASKDATASAVER_H
