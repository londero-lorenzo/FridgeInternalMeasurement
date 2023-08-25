//
// Created by londe on 23/08/2023.
//

#include "FolderBufferEmulator.h"

FolderBufferEmulator::FolderBufferEmulator() {

}


void FolderBufferEmulator::setRoot(String &root) {
this->mountPoint = root;
}


bool FolderBufferEmulator::addFile(const FileEmulator &sdFileEmulator) {
    for (FileEmulator *file: this->files) {
        if(file->isEmpty())
            *file = sdFileEmulator;
    }
    return false;
}

bool FolderBufferEmulator::exists(const String &path) {
    String WipedPath = FolderBufferEmulator::WipePath(path);
    for (FileEmulator *file: this->files) {
        if(!file->isEmpty())
            if (file->getPath() == path)
                return true;
    }
    return false;
}



String FolderBufferEmulator::WipePath(const String &path){
    String newPath;
    if (path.indexOf('\\') != -1){
        std::replace_copy(path.begin(), path.end(), &newPath,'\\', '/');
    }
    if (path.endsWith("/"))
        newPath = path.substring(0, path.length() - 1);
    return newPath;
}
