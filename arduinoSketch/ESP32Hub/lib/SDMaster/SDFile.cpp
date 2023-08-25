//
// Created by londe on 23/08/2023.
//

#include "SDFile.h"

SDFile::SDFile() {

}

SDFile::SDFile(fs::File file) {
    this->targetElements.targetElement = &file;
    this->targetElements.targetType = NormalFile;
}

SDFile::operator bool() const {
    if (this->targetElements.targetType == NormalFile)
        return *((fs::File*)this->targetElements.targetElement);
    else if(this->targetElements.folderEmulator != nullptr)
        return *()
}

String SDFile::readString() {
    return this->file.readString();
}

int SDFile::read() {
    return this->file.read();
}

int SDFile::available() {
    file.close();
    return this->file.available();
}


size_t SDFile::print(const String &s) {
    return this->file.print(s);
}

void SDFile::close() {
    this->file.close();
}

SDFile SDFile::openNextFile() {
    return SDFile(this->file.openNextFile());
}

const char *SDFile::path() {
    return this->file.path();
}
