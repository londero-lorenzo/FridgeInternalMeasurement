
#ifndef PROBES_TIMER_H

#define PROBES_TIMER_H

#define MAX_IDENTIFIER_LENGTH 5

class ProbesTimer {
private:
    int timeDelay{};
    char identifier[MAX_IDENTIFIER_LENGTH]{};
    bool _isEmpty;
public:
    ProbesTimer();

    explicit ProbesTimer(int timeDelay);

    char *getIdentifier();

    int getTimeDelay() const;

    bool isEmpty() const;
};

#endif