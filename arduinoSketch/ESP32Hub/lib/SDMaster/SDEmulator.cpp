//
// Created by londe on 23/08/2023.
//


#include "SDEmulator.h"
#include "../utils.h"


bool SDEmulator::begin(int pinNumber) {
    DEBUG_PRINTLN("Initialization sd card emulator...")
    DEBUG_PRINT("Mounting sd at GPIO port number ")
    DEBUG_PRINTLN(pinNumber)
    this->root = "/SD/";
    this->folderEmulator.setRoot(this->root);
    DEBUG_PRINTLN("Initialization completed!")
    return true;
}

sdcard_type_t SDEmulator::cardType() {
    return CARD_EMULATOR;
}

// /SD/folder
bool SDEmulator::mkdir(String &path) {
    FolderEmulator newFolder;
    newFolder.setRoot(path);
    return this->folderEmulator.add(newFolder);
}

bool SDEmulator::exists(const String &path) {
    return this->folderEmulator.exists(path);
}
