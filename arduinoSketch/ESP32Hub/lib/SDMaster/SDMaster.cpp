//
// Created by londe on 23/08/2023.
//

#include "SDMaster.h"

bool SDMaster::begin(int pinNumber) {
    return
#ifndef SD_TEST_EMU
        SD
#else
            sdEmulator
#endif
                    .begin(pinNumber);
}

sdcard_type_t SDMaster::cardType() {
    return
#ifndef SD_TEST_EMU
        SD
#else
            sdEmulator
#endif
                    .cardType();
}
bool SDMaster::mkdir(String &path) {
    return
#ifndef SD_TEST_EMU
        SD
#else
            sdEmulator
#endif
                    .mkdir(path);
}

bool SDMaster::exists(const String &path) {
    return
#ifndef SD_TEST_EMU
        SD
#else
            sdEmulator
#endif
                    .exists(path);
}

SDFile SDMaster::open(const String &path, const char *mode, bool create) {
#ifndef SD_TEST_EMU
    return SDFile(SD.open(path.c_str(), mode, create));
#endif
    return SDFile;
}
