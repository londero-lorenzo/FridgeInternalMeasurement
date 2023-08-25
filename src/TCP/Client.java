package TCP;

import java.io.IOException;
import java.net.Socket;


public class Client {
    Socket socket;
    public void startClient(String ip, int port) {
        try {
            this.socket= new Socket(ip, port);
            System.out.println("Connection established");
            this.waitFor('1');
            socket.getOutputStream().write("FRIDGE_1".getBytes());
            this.waitFor('2');
            socket.getOutputStream().write("4".getBytes());

            this.waitFor((char)126);
            socket.getOutputStream().write("30000".getBytes());
            int incomingByte = 0;
            try {
                while (incomingByte != -1) {
                    incomingByte = socket.getInputStream().read();
                    System.out.println(incomingByte);
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }


        } catch (IOException o) {
            throw new RuntimeException(o);
        }
        System.out.println("Connection closed");
    }

    public void waitFor(char id){
        int incomingByte = 0;
        try {
            do{
                incomingByte = socket.getInputStream().read();}
            while (incomingByte != id);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
