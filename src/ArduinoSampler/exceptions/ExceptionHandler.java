package ArduinoSampler.exceptions;

import ArduinoSampler.interfaccia.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionHandler {
    private SocketException lastException;

    public void printError(Logger logger) {
        StringWriter errors = new StringWriter();
        lastException.printStackTrace(new PrintWriter(errors));
        logger.write(errors.toString());
    }

    public void setException(SocketException socketException) {
        this.lastException = socketException;
    }

    public void setExceptionFromAnotherExceptionHandler(ExceptionHandler handler) {
        this.setException(handler.getLastException());
    }

    public SocketException getLastException() {
        return lastException;
    }
}
