//
// Created by londe on 23/08/2023.
//

#ifndef ESP32HUB_SDFILE_H
#define ESP32HUB_SDFILE_H

#include "FS.h"
#include "SD.h"
#include "SPI.h"

//TODO: insert #ifdef
#include "FolderEmulator.h"

typedef enum TargetType{
    NormalFile,
    EmulatedFile,
    EmulatedFolder
}TargetType;

typedef struct SDTargetElements{
    void *targetElement;
    TargetType targetType;
}SDTargetElements;

class SDFile {
private:
    struct SDTargetElements targetElements;
public:
    SDFile();

    explicit SDFile(fs::File file);

    explicit operator bool() const;

    virtual String readString();

    int read();

    int available();

    size_t print(const String &s);

    void close();

    SDFile openNextFile();

    const char *path();

};


#endif //ESP32HUB_SDFILE_H
