//
// Created by londe on 23/08/2023.
//

#ifndef ESP32HUB_SDMASTER_H
#define ESP32HUB_SDMASTER_H

#define SD_TEST_EMU



#ifdef SD_TEST_EMU
#include "SDEmulator.h"
#else
#include "SDFile.h"
#endif


class SDMaster {
private:
    #ifdef SD_TEST_EMU
        SDEmulator sdEmulator;
    #endif
public:
    bool begin(int pinNumber);
    sdcard_type_t cardType();
    bool mkdir(String &path);
    bool exists(const String& path);
    SDFile open(const String& path, const char* mode, bool create = false);
};


#endif //ESP32HUB_SDMASTER_H
