


#include "ProbesTimer.h"

ProbesTimer::ProbesTimer(){
    ProbesTimer::_isEmpty = true;
}

ProbesTimer::ProbesTimer(int timeDelay) {
    ProbesTimer::timeDelay = timeDelay;
    ProbesTimer::_isEmpty = false;
}

char *ProbesTimer::getIdentifier() {
    return &ProbesTimer::identifier[0];
}

int ProbesTimer::getTimeDelay() const{
    return  ProbesTimer::timeDelay;
}

bool ProbesTimer::isEmpty() const{
    return ProbesTimer::_isEmpty;
}
