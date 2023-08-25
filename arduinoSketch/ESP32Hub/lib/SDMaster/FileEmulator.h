//
// Created by londe on 23/08/2023.
//

#ifndef ESP32HUB_FILEEMULATOR_H
#define ESP32HUB_FILEEMULATOR_H
//#include "SDFile.h"

#include <WString.h>

class FileEmulator {
private:
    String data;
    String name;
    String path;
public:
    FileEmulator();

    bool isEmpty();

    String getPath();

protected:
    bool _isEmpty;
};


#endif //ESP32HUB_FILEEMULATOR_H
