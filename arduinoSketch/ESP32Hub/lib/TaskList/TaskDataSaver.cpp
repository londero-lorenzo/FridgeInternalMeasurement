//
// Created by londe on 29/07/2023.
//

#include "TaskDataSaver.h"

#define DATA_FILE "readings.csv"

TaskDataSaver::TaskDataSaver(const char *taskIdentifier) {
    TaskDataSaver::taskIdentifier = taskIdentifier;
    TaskDataSaver::NumberOfDataSaved = -1;
}


uint64_t TaskDataSaver::readNumberOfDataSaved() {
    return TaskDataSaver::taskDataFile.getNumberOfLines();
}

uint64_t TaskDataSaver::getNumberOfDataSaved() const {
    return TaskDataSaver::NumberOfDataSaved;
}

TaskDataSaver::TaskDataSaver() {
    TaskDataSaver::taskIdentifier = "";
    TaskDataSaver::NumberOfDataSaved = -1;
    TaskDataSaver::fileSystem = SDMaster();
}

void TaskDataSaver::save(DHTData *data) {
    TaskDataSaver::NumberOfDataSaved = TaskDataSaver::NumberOfDataSaved + 1;
    String rawDataToSave;
    DEBUG_PRINT("Data to save: ")
    rawDataToSave += String("I:") + data->timeIndex;
    rawDataToSave += String(";T:") + data->temperature;
    rawDataToSave += String(";H:") + data->humidity;
    //rawDataToSave += String(";O:") + data->doorIsOpen;
    rawDataToSave += String("\n");
    DEBUG_PRINT(rawDataToSave)
    TaskDataSaver::taskDataFile.write(rawDataToSave);
    // TODO: WRITE INTO FILE
}
/*
void TaskDataSaver::checkForDataFile() {

    //"/Fridge32/t"

    if (TaskDataSaver::fileSystem.mkdir((TaskDataSaver::dataStorageDirectory).c_str())){

        TaskDataSaver::taskDataFile = TaskDataSaver::fileSystem.open((TaskDataSaver::dataStorageDirectory + DATA_FILE).c_str(), FILE_APPEND);
        if (taskDataFile){
            DEBUG_PRINT("Task data file created at: ") DEBUG_PRINTLN(TaskDataSaver::dataStorageDirectory  + DATA_FILE)
        } else DEBUG_PRINT("Failed to create data file at: ") DEBUG_PRINTLN(TaskDataSaver::dataStorageDirectory + DATA_FILE)
        //TaskDataSaver::fileSystem.rmdir((TaskDataSaver::dataStorageDirectory).c_str());
        //DEBUG_PRINT("Task directory destroyed at: ") DEBUG_PRINTLN(TaskDataSaver::dataStorageDirectory)
    }
}*/

void TaskDataSaver::setSDFileSystem(SDMaster &sdMaster, const String& root) {
    TaskDataSaver::fileSystem = sdMaster;
    TaskDataSaver::dataStorageDirectory = root;
    DEBUG_PRINTLN("SD File System load!")
    DEBUG_PRINT("SD card type:")
    DEBUG_PRINTLN(TaskDataSaver::fileSystem.cardType())
    DEBUG_PRINT("Attempt to create folder at: ")
    DEBUG_PRINTLN(TaskDataSaver::dataStorageDirectory.c_str())
    if (TaskDataSaver::fileSystem.mkdir(TaskDataSaver::dataStorageDirectory)) {
        DEBUG_PRINT("Task directory created at: ") DEBUG_PRINTLN(TaskDataSaver::dataStorageDirectory)
        TaskDataSaver::taskDataFile = FileHandler(TaskDataSaver::fileSystem, root, String(DATA_FILE), FILE_APPEND);
    }


    TaskDataSaver::NumberOfDataSaved = TaskDataSaver::readNumberOfDataSaved();
}


/*
    *
    * Folder creator with readings file
    *
    *
   int const n = 15, offset = 65, separator = 8;
   int internalSeparatorOffset = 0;
   //the first +1 is for the first '/', while the second +1 is for the right ending of c-strings ('\0')
   char a[n + n/separator + 1 + 1] = {'\0'};
   for (int i = offset; i < offset + n; i++) {
       if ((i - offset) % separator == 0) {
           a[i - offset + internalSeparatorOffset] = '/';
           internalSeparatorOffset++;
       }
       a[i - offset + internalSeparatorOffset] = char (i);

       if (TaskDataSaver::fileSystem.mkdir(a)){
           DEBUG_PRINT("Task directory created at: ") DEBUG_PRINTLN(a)
           if (TaskDataSaver::fileSystem.open((String(a) + '/' + DATA_FILE).c_str(), FILE_WRITE)){
               DEBUG_PRINT("Task data file created at: ") DEBUG_PRINTLN(String(a) + '/' + DATA_FILE)
           } else {
               DEBUG_PRINT("Failed to create data file at: ") DEBUG_PRINTLN(String(a) + '/' + DATA_FILE)
           }
           if ((i - offset) % separator == 0){
               DEBUG_PRINT("Task directory maintained at: ") DEBUG_PRINTLN(a)
           }else{
               TaskDataSaver::fileSystem.remove((String(a) + '/' + DATA_FILE).c_str());
               TaskDataSaver::fileSystem.rmdir(a);
               DEBUG_PRINT("Task directory destroyed at: ") DEBUG_PRINTLN(a)
           }
       }else DEBUG_PRINT("Unable create task directory at: ") DEBUG_PRINTLN(a)
       vTaskDelay(100);
   }

*/

