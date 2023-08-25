#include <Arduino.h>
#include <WiFi.h>
#include <ctime>
#include "../lib/SDMaster/SDMaster.h"
#include "../lib/ClientList/ClientList.h"
#include "../lib/DHTCollectorList/DHTCollectorList.h"


const char* ssid     = "EOLO - FRITZ!Box 7530 KO";
const char* password = "71394093540551127339";

//const char* ssid     = "Wifi RX";
//const char* password = "12345678";


#define serverPort 11000
#define PROJECT_NAME "Fridge32"


SDMaster sdMaster;


WiFiServer server(serverPort);
Socket::ClientList clientList;
DHTCollectorList probeList;
TaskList taskList(&probeList);

//TaskList taskList(&probeList);


//const int DHT_GPIO[]{13, 12, 27, 2, 4, 17};

void printLocalTime(){
    struct tm timeinfo{};
    if(!getLocalTime(&timeinfo)){
        Serial.println("Failed to obtain time");
        return;
    }
    Serial.println(&timeinfo, "%A, %B %d %Y %H:%M");
}

void setup()
{

    Serial.begin(115200);
    while(!Serial){delay(100);}


    //TODO: remove commands in order to use SD

    if(!(sdMaster.begin(CS_PIN))){
        Serial.println("Card Mount Failed");
        return;
    }
    uint8_t cardType = sdMaster.cardType();
    if(cardType == CARD_NONE){
        Serial.println("No SD card attached");
        return;
    }
    //static SDFileSystemClass s = SD;
    DEBUG_PRINT("Attempt to create project folder at: ")
    DEBUG_PRINTLN(PROJECT_NAME)



    //TODO: remove commands in order to use SD
    sdMaster.mkdir(String('/') + PROJECT_NAME);
    sdMaster.mkdir(String('/') + PROJECT_NAME  + "/tasks");
    FileHandler testFile(sdMaster, String(String('/') + PROJECT_NAME), "simple1.txt", FILE_WRITE);
    testFile.write("This is a simple message!\n");
    DEBUG_PRINT("Lines in ")
    DEBUG_PRINT(testFile.getPath())
    DEBUG_PRINT(": ")
    DEBUG_PRINTLN(testFile.getNumberOfLines())
    testFile.close();
    FileHandler testFile2(sdMaster, String(String('/') + PROJECT_NAME), "simple1.txt", FILE_WRITE);
    testFile2.write("This is a simple message!\nShould work!\n");
    DEBUG_PRINT("Lines in ")
    DEBUG_PRINT(testFile2.getPath())
    DEBUG_PRINT(": ")
    DEBUG_PRINTLN(testFile2.getNumberOfLines())
    testFile2.close();
    /*
    // TODO: ONLY THE FIRST ONE IS WRITTEN IN THE RIGHT WAY!!
    //SD.mkdir("/ESP32"); SD.mkdir("ESP32A/"); SD.mkdir("/ESP32B/");

    if (SD.mkdir((String('/') + PROJECT_NAME  + '\0').c_str()))
        DEBUG_PRINTLN("Project folder created!")
    else
        if (SD.exists((String('/') + PROJECT_NAME + '\0').c_str()))
            DEBUG_PRINTLN("Project folder already created!")
        else
            DEBUG_PRINTLN("Failed to create project folder!")

    //SD.mkdir(String(PROJECT_NAME) + "/tasks");
    */

    taskList.setSDFileSystem(sdMaster,(String('/') + PROJECT_NAME + "/tasks/").c_str());

    DHTCollector probe1(13, "Primo scaffale");
    probeList.add(&probe1);

    DHTCollector probe2(12, "Secondo scaffale");
    probeList.add(&probe2);

    DHTCollector probe3(27, "Terzo scaffale");
    probeList.add(&probe3);

    DHTCollector probe4(2, "Scompartimento sottostante");
    probeList.add(&probe4);

    DHTCollector probe5(4, "Portiera alto");
    probeList.add(&probe5);

    DHTCollector probe6(17, "Portiera basso");
    probeList.add(&probe6);

    Serial.println();
    Serial.println("******************************************************");
    Serial.print("Connecting to ");
    Serial.println(ssid);

    WiFi.begin(ssid, password);

    while (WiFiClass::status() != WL_CONNECTED) {
        delay(500);
        Serial.print(".");
    }

    Serial.println("");
    Serial.println("WiFi connected");
    Serial.println("IP address: ");
    Serial.println(WiFi.localIP());
    configTime(3600 * 2, 0, "pool.ntp.org");
    printLocalTime();
    server.begin();
    taskList.run();
}




void loop() {
    if (server.hasClient())
    {
        Socket::ClientSlot client(&server, &taskList);
        //Socket::Client client
        DEBUG_PRINT("[1] Memory address of client ")
        DEBUG_PRINT(client.getIp())
        DEBUG_PRINT(": ")
        DEBUG_PRINTLN((unsigned int)&client, HEX)
        clientList.add(&client);
    }
    // put your main code here, to run repeatedly:

}
