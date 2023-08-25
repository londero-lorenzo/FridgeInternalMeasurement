package ArduinoSampler.arduino;

import ArduinoSampler.exceptions.*;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

class Command<Type>{

    private Type command;

    Command(Type command){

    }


    public Class<?> getType(){
        return this.command.getClass();
    }
}

class CommandBuffer {
    private final ArrayList<Byte> sentCommands = new ArrayList<>();

    public void addCommand(byte c){
        this.sentCommands.add(c);
    }

    public Byte getLastCommand(){
        return this.sentCommands.get(this.sentCommands.size() - 1);
    }

    public ArrayList<Byte> getSentCommands(){
        return this.sentCommands;
    }
}


public class ServerSocket {

    private boolean isConnected;
    private final Address address;

    private Socket socket;

    private String watchword;

    private final CommandBuffer sentCommands;


    public ServerSocket(Address address) {
        this.address = address;
        this.sentCommands = new CommandBuffer();
    }

    public boolean checkForConnection() throws SocketException {
        try {
            socket = new Socket(this.address.getIpv4(), this.address.getPort());
        } catch (IOException e) {
            throw new UnableToWriteOnSocketException();
        } catch (IllegalArgumentException e) {
            throw new IllegalPortArgumentProvided();
        }
        if (ServerCommands.fromCommandByte(this.read()) == ServerCommands.INITIAL_IDENTIFIER) {
            if (this.writeCommand(ServerCommands.INITIAL_IDENTIFIER.getByte()))
                return true;
            else
                throw new UnableToWriteOnSocketException();
        } else
            throw new UnableToAuthenticateTheServerException();
    }


    public void waitFor(char id) {
        int incomingByte;
        try {
            do {
                incomingByte = socket.getInputStream().read();
            }
            while (incomingByte != id);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int read() throws UnableToReadFromSocketException {
        try {
            return socket.getInputStream().read();
        } catch (IOException e) {
            throw new UnableToReadFromSocketException();
        }
    }

    private boolean write(byte b) {
        try {
            this.socket.getOutputStream().write(b);
            this.socket.getOutputStream().flush();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean writeCommand(byte command){
        if (this.write(command)){
            this.sentCommands.addCommand(command);
            return true;
        }
        return false;
    }

    public boolean isAvailable() {
        try {
            return (this.socket.getInputStream().available()) > 0;
        } catch (IOException e) {
            return false;
        }
    }

    public String readUntilEnd() throws UnableToReadFromSocketException {
        StringBuilder s = new StringBuilder();
        do {
            s.append((char) this.read());
        } while (this.isAvailable());
        return s.toString();
    }

    public boolean write(String s) {
        try {
            this.socket.getOutputStream().write(s.getBytes());
            this.socket.getOutputStream().flush();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public void setWatchword(String watchword) {
        this.watchword = watchword;
    }

    public boolean requiresWatchword() throws UnableToReadFromSocketException {
        byte watchwordIdentifierCharRequirement = (byte) this.read();
        return ServerCommands.WATCHWORD_REQUIRED__IDENTIFIER_CHAR.matchTo(watchwordIdentifierCharRequirement);
    }

    public void verifyWatchword(String watchword) throws UnableToWriteOnSocketException, UnableToReadFromSocketException, UserNotAuthorizedException, NotRecognizedCommandException, SyncErrorException {
        if (this.write(watchword)) {
            switch (ServerCommands.fromCommandByte(this.read())) {
                case COMMANDS_AUTHORIZED -> {}

                case COMMANDS_NOT_AUTHORIZED ->
                        throw new UserNotAuthorizedException();

                case NOT_RECOGNIZED_COMMAND -> {
                    this.resetConnection();
                    throw new NotRecognizedCommandException();
                }

                default ->
                        throw new SyncErrorException();
            }
        }else
            throw new UnableToWriteOnSocketException();
    }
}
