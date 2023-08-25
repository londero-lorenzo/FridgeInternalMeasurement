//
// Created by londe on 04/08/2023.
//

#include "FileHandler.h"


FileHandler::FileHandler() {
    FileHandler::FileSystem = nullptr;
    FileHandler::targetFile = File();
    FileHandler::root = "";
}


FileHandler::FileHandler(fs::SDFS *FileSystem, String root, String fileName) {
    FileHandler::FileSystem = FileSystem;
    FileHandler::root = root;
    String path;
    path = root + (root.endsWith("/") ? fileName : ('/' + fileName));
    FileHandler::targetFile = FileHandler::FileSystem->open(path, FILE_WRITE);
    if (FileHandler::targetFile) {
        DEBUG_PRINT("Task file created at: ")
        DEBUG_PRINTLN(path)
    } else {
        DEBUG_PRINT("Failed to create file at: ")
        DEBUG_PRINTLN(path)
    }
}


uint64_t FileHandler::getNumberOfLines() {
    String allDataString = FileHandler::targetFile.readString();
    uint64_t indexOfTerminator = 0, numberOfDataSaved = -1;
    do {
        indexOfTerminator = allDataString.indexOf('\n', indexOfTerminator + 1);
        numberOfDataSaved++;
    } while (indexOfTerminator > 0);
    return numberOfDataSaved;
}


void FileHandler::close() {
    FileHandler::targetFile.close();

}

int FileHandler::available() {
    FileHandler::targetFile.available();

}

void FileHandler::write(String dataToWrite) {
    FileHandler::targetFile.print(dataToWrite);
}
