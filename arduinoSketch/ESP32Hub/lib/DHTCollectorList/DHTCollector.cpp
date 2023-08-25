#include "DHTCollector.h"

#include <utility>
//#include "../DHT/DHT.h"
//#include <cstring>


DHTCollector::DHTCollector() {
    DHTCollector::_isEmpty = true;
    DHTCollector::_isOnline = false;
    DHT();
}

DHTCollector::DHTCollector(int GPIOPort, const char *description) {
    DHTCollector::GPIOPort = GPIOPort;
    DHTCollector::_isEmpty = false;
    DHTCollector::_isOnline = false;
    DHTCollector::description = description;
    DHTCollector::dht = DHT(DHTCollector::GPIOPort, DHT11);
    DHTCollector::dht.begin();
    DHTCollector::checkForDHTStatus();
}

DHTCollector::DHTCollector(int GPIOPort, const char *description, uint8_t DHTTYPE) {
    DHTCollector::GPIOPort = GPIOPort;
    DHTCollector::_isEmpty = false;
    DHTCollector::_isOnline = false;
    DHTCollector::description = description;
    DHTCollector::dht = DHT(DHTCollector::GPIOPort, DHTTYPE);
    DHTCollector::dht.begin();
    DHTCollector::checkForDHTStatus();
}

void DHTCollector::checkForDHTStatus() {
    DHTCollector::_isOnline = !(isnan(DHTCollector::dht.readTemperature()) || isnan(DHTCollector::dht.readHumidity()));
}

struct DHTData DHTCollector::collect(String currentTime) {
    float temperature = DHTCollector::dht.readTemperature();
    float humidity = DHTCollector::dht.readHumidity();
    vTaskDelay(3000);
    struct DHTData currentDHTData = {};
    currentDHTData.temperature = (isnan(temperature)) ? DHTCollector::dht.readTemperature() : temperature ;
    currentDHTData.humidity = (isnan(humidity)) ? DHTCollector::dht.readHumidity() : humidity;
    currentDHTData.currentTime = currentTime;
    //currentDHTData.timeIndex = timeIndex;
    //currentDHTData.GPIOPort = DHTCollector::GPIOPort;
    DEBUG_PRINTLN("***************************************")
    DEBUG_PRINT("> Collecting data from DHT allocated at GPIO port ") DEBUG_PRINT(DHTCollector::getGPIOPort()) DEBUG_PRINTLN("... <")
    DEBUG_PRINT("Description: ") DEBUG_PRINTLN(DHTCollector::getDescription())
    DEBUG_PRINT("Temperature: ") DEBUG_PRINTLN(currentDHTData.temperature)
    DEBUG_PRINT("Humidity: ") DEBUG_PRINTLN(currentDHTData.humidity)
    DEBUG_PRINT("Time: ") DEBUG_PRINTLN(currentDHTData.currentTime)
    DEBUG_PRINTLN("***************************************")
    return currentDHTData;
}

bool DHTCollector::isEmpty() const {
    return DHTCollector::_isEmpty;
}

bool DHTCollector::isOnline() const {
    return DHTCollector::_isOnline;
}

uint8_t DHTCollector::getGPIOPort() const {
    return DHTCollector::GPIOPort;
}

const char *DHTCollector::getDescription() const {
    return DHTCollector::description;
}


bool DHTCollector::operator==(const DHTCollector &dhtCollector) const {
    return DHTCollector::GPIOPort == dhtCollector.getGPIOPort();
}
