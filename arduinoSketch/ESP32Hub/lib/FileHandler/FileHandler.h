//
// Created by londe on 04/08/2023.
//

#ifndef ESP32HUB_FILEHANDLER_H
#define ESP32HUB_FILEHANDLER_H

#include "../utils.h"
#include "../SDMaster/SDMaster.h"

#define LINE_SEPARATOR '\n'

class FileHandler {
private:
    SDMaster sdMaster;
    SDFile targetFile;
    String root;
    String filePath;
    const char * mode;
    uint32_t lineCursor;
    bool changedMode;
public:
    FileHandler();

    explicit FileHandler(SDMaster &sdMaster, const String &root, const String &fileName,
                         const char *mode);

    uint64_t getNumberOfLines();

    String getNextLine();

    int available();

    void write(const String &dataToWrite);

    void write(const String &dataToWrite, bool forceWrite);

    void switchModeTo(const char *newMode);

    String getPath();

    void close();
};


#endif //ESP32HUB_FILEHANDLER_H
