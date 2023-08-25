//
// Created by londe on 23/08/2023.
//

#ifndef ESP32HUB_SDEMULATOR_H
#define ESP32HUB_SDEMULATOR_H
#include "SDFile.h"
class SDEmulator {
private:
    String root;
    FolderEmulator folderEmulator;
public:
    bool begin(int pinNumber);
    sdcard_type_t cardType();
    bool mkdir(String &path);
    bool exists(const String& path);
    SDFile open(const String& path, const char* mode, bool create = false);

};


#endif //ESP32HUB_SDEMULATOR_H
