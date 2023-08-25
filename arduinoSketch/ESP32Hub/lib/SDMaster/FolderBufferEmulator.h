//
// Created by londe on 23/08/2023.
//

#ifndef ESP32HUB_FOLDERBUFFEREMULATOR_H
#define ESP32HUB_FOLDERBUFFEREMULATOR_H

#include <vector>
#include "FileEmulator.h"
//


#define MAX_NUMBER_OF_FILES 5

class FolderBufferEmulator {
private:
    //FileEmulator filesEmulated[MAX_NUMBER_OF_FILES];
    std::vector<FileEmulator*> files;
    String mountPoint;
public:
    FolderBufferEmulator();

    void setRoot(String &root);
    bool addFile(const FileEmulator &sdFileEmulator);
    bool exists(const String &path);
    static String WipePath(const String &path);
};


#endif //ESP32HUB_FOLDERBUFFEREMULATOR_H

