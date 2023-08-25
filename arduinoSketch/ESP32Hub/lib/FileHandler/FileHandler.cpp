//
// Created by londe on 04/08/2023.
//

#include "FileHandler.h"


FileHandler::FileHandler() {
    FileHandler::targetFile = SDFile();
    FileHandler::root = "";
    FileHandler::mode = "\0";
    FileHandler::lineCursor = 0;
    FileHandler::changedMode = false;
}


FileHandler::FileHandler(SDMaster &sdMaster, const String &root, const String &fileName,
                         const char *mode) {
    FileHandler::root = root;
    FileHandler::mode = mode;
    FileHandler::lineCursor = 0;
    FileHandler::filePath = root + (root.endsWith("/") ? fileName : ('/' + fileName));
    bool alreadyExists = FileHandler::sdMaster.exists(FileHandler::filePath);
    FileHandler::targetFile = FileHandler::sdMaster.open(FileHandler::filePath, FileHandler::mode);
    if (FileHandler::targetFile) {
        if (!alreadyExists) {
            DEBUG_PRINT("File created at: ")
            DEBUG_PRINTLN(FileHandler::filePath)
        }
        DEBUG_PRINT("File opened at: ")
        DEBUG_PRINTLN(FileHandler::filePath)
    } else {
        if (alreadyExists) {
            DEBUG_PRINT("Failed to open file at: ")
            DEBUG_PRINTLN(FileHandler::filePath)
        } else {
            DEBUG_PRINT("Failed to create file at: ")
            DEBUG_PRINTLN(FileHandler::filePath)
        }
    }
    FileHandler::changedMode = false;
}


uint64_t FileHandler::getNumberOfLines() {
    FileHandler::switchModeTo(FILE_READ);
    String allDataString = FileHandler::targetFile.readString();
    int indexOfTerminator = 0, numberOfDataSaved = -1;
    do {
        indexOfTerminator = allDataString.indexOf(LINE_SEPARATOR, indexOfTerminator + 1);
        numberOfDataSaved++;
    } while (indexOfTerminator > 0);
    FileHandler::switchModeTo(FileHandler::mode);
    return numberOfDataSaved;
}

String FileHandler::getNextLine() {
    FileHandler::switchModeTo(FILE_READ);
    int cursor = 0, c = 0;
    String line;
    while (FileHandler::targetFile.available()){
        c = FileHandler::targetFile.read();

        cursor++;
        if ((c == LINE_SEPARATOR) && !FileHandler::changedMode ||
            ((c == LINE_SEPARATOR) && cursor > FileHandler::lineCursor)) {
            FileHandler::lineCursor = cursor;
            return line;
        }
        if (cursor > FileHandler::lineCursor)
            line += c;
    }
    FileHandler::switchModeTo(FileHandler::mode);
    FileHandler::changedMode = false;
    return line;
}


int FileHandler::available() {
    return FileHandler::targetFile.available();
}

void FileHandler::write(const String &dataToWrite) {
    FileHandler::switchModeTo(FILE_APPEND);
    FileHandler::targetFile.print(dataToWrite);
    FileHandler::switchModeTo(FileHandler::mode);
    FileHandler::changedMode = false;
}

void FileHandler::write(const String &dataToWrite, bool forceWrite) {
    FileHandler::switchModeTo((forceWrite) ? FILE_WRITE : FILE_APPEND);
    FileHandler::targetFile.print(dataToWrite);
    FileHandler::switchModeTo(FileHandler::mode);
    FileHandler::changedMode = false;
}

void FileHandler::switchModeTo(const char *newMode) {
    if (strcmp(FileHandler::mode, newMode) == 0 || strcmp(newMode, FILE_WRITE) == 0) return;
    String path = FileHandler::filePath;
    FileHandler::targetFile.close();
    vTaskDelay(100);
    FileHandler::targetFile = FileHandler::sdMaster.open(path, newMode);
    FileHandler::changedMode = true;
}

String FileHandler::getPath() {
    return FileHandler::filePath;
}

void FileHandler::close() {
    FileHandler::targetFile.close();
}

