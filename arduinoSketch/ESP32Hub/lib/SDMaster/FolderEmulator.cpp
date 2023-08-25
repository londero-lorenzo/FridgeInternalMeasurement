//
// Created by londe on 23/08/2023.
//

#include "FolderEmulator.h"


FolderEmulator::FolderEmulator() {
    this->_isEmpty = false;
    this->root = "";
}

void FolderEmulator::setRoot(String &path) {
    this->root = path;
}

bool FolderEmulator::add(const FolderEmulator& folderEmulator) {
    for (FolderEmulator *subFolder: this->subFolders) {
        if (this->exists(folderEmulator.getPath(), Folder)){

        }
        if(subFolder->isEmpty()) {
            if (!this->exists(folderEmulator.getPath())) {
                *subFolder = folderEmulator;
                return true;
            }
        }
    }
    return false;
}

bool FolderEmulator::isEmpty() {
    return this->_isEmpty;
}

bool FolderEmulator::exists(const String &path, FileType type) {
    String WipedPath = FolderBufferEmulator::WipePath(path);
    if (WipedPath == this->root)
        return true;
    if (type == File || type == Both) {
        if (WipedPath.startsWith(this->root) && (std::count(WipedPath.begin(), WipedPath.end(), '/') ==
                                                 std::count(this->root.begin(), this->root.end(), '/') + 1))
            return this->folderBufferEmulator.exists(WipedPath);
    }
    bool found = false;
    if (type == Folder || type == Both) {
        for (FolderEmulator *subFolder: this->subFolders) {
            if (!subFolder->isEmpty()) {
                found |= subFolder->exists(WipedPath, type);
            }
        }
    }
    return found;
}

String FolderEmulator::getPath() const {
    return this->root;
}
