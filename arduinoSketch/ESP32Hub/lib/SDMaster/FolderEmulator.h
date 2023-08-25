//
// Created by londe on 23/08/2023.
//

#ifndef ESP32HUB_FOLDEREMULATOR_H
#define ESP32HUB_FOLDEREMULATOR_H

#include "FolderBufferEmulator.h"
#include <vector>

typedef enum FileType{
    File,
    Folder,
    Both
}FileType;


class FolderEmulator {
private:
    bool _isEmpty;
    String root;
    FolderBufferEmulator folderBufferEmulator;
    std::vector<FolderEmulator*> subFolders;

    //FolderEmulator *subFolders;

public:
    FolderEmulator();
    void setRoot(String &path);
    bool add(const FolderEmulator& folderEmulator);
    bool exists(const String &path, FileType type = Both);
    String getPath() const;
    bool isEmpty();
};


#endif //ESP32HUB_FOLDEREMULATOR_H
