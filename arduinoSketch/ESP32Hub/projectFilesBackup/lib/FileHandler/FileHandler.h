//
// Created by londe on 04/08/2023.
//

#ifndef ESP32HUB_FILEHANDLER_H
#define ESP32HUB_FILEHANDLER_H
#include "FS.h"
#include "SD.h"
#include "SPI.h"
#include "../utils.h"
class FileHandler {
private:
    SDFS *FileSystem;
    File targetFile;
    String root;
public:
    FileHandler();
    explicit FileHandler(SDFS *FileSystem, String root, String fileName);

    uint64_t getNumberOfLines();

    int available();

    void write(String dataToWrite);

    void close();
};


#endif //ESP32HUB_FILEHANDLER_H
