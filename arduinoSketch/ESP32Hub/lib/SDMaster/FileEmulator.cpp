//
// Created by londe on 23/08/2023.
//

#include "FileEmulator.h"

FileEmulator::FileEmulator() {
    this->_isEmpty = true;
}

bool FileEmulator::isEmpty() {
    return this->_isEmpty;
}

String FileEmulator::getPath() {
    return this->path;
}
