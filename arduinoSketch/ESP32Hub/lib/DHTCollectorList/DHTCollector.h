
#include "../DHT/DHT.h"

#ifndef DHT_PROPE_H

#define DHT_PROPE_H

//#pragma pack(1) //used to avoid the wastage of memory in struct and more.

struct DHTData {
    float temperature;

    float humidity;

    //bool doorIsOpen;

    uint32_t timeIndex;

    String currentTime;

    bool empty;

    //uint8_t GPIOPort;

    // TODO: ADD TIME RATIO (current time of data collect / time when the task starts)
};

static const struct DHTData defaultDHTData = {.temperature = 0, .humidity = 0, /*.doorIsOpen = false,*/ .timeIndex= 0 /*,.GPIOPort = UINT8_MAX*/, .empty = true};

// size: 4byte * 3 = 12byte

class DHTCollector {

private:
    uint8_t GPIOPort{};
    const char *description{};
    DHT dht;
    bool _isEmpty;
    bool _isOnline;


public:
    DHTCollector();

    DHTCollector(int GPIOPort, const char *description);

    DHTCollector(int GPIOPort, const char *description, uint8_t DHTTPYE);

    void checkForDHTStatus();

    struct DHTData collect(String currentTime);

    bool isEmpty() const;

    bool isOnline() const;

    uint8_t getGPIOPort() const;

    const char *getDescription() const;

    bool operator==(DHTCollector const &dhtCollector) const;

};

#endif