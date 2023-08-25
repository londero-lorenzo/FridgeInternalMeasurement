package TCP;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ESP32Server {

    public int port;

    private final String ServerWatchword = "FRIDGE_1";
    public ESP32Server(int port) {
        this.port = port;
    }

    public void start() {

        boolean result = true;
        try {
            //apro una porta TCP
            ServerSocket serverSocket = new ServerSocket(this.port);
            System.out.println("Server socket ready on port: " + this.port);
            //resto in attesa di una connessione


            Socket socket = serverSocket.accept();
            System.out.println("Received client connection");
            //apro gli scream d'input e output per leggere e scrivere nella connessione appena ricevuta Scanner in=

            //InputStream in = socket.getInputStream();

            //leggo e scrivo nella connessione finchè non ricevo <quit>




            socket.getOutputStream().write('1');
            System.out.println("Sending 1...");
            char command = (char) socket.getInputStream().read();
            System.out.println("Receiving: " + command);
            if (command == '1'){
                System.out.println("First command received!");
                System.out.println("Watchword builder initializing...");
                StringBuilder watchword = new StringBuilder();
                socket.getOutputStream().write('w');
                System.out.println("Requesting watchword...");
                do{
                    char watchwordPiece = (char) socket.getInputStream().read();
                    watchword.append(watchwordPiece);
                    System.out.println("Receiving: " + watchwordPiece);
                    if (watchword.length() > ServerWatchword.length()){
                        System.out.println("Invalid watchword.");
                        socket.getOutputStream().write('3');
                        System.out.println("Sending 3...");
                        result = false;
                        break;
                    }
                    if (watchword.toString().equals(this.ServerWatchword)) {
                        System.out.println("Right watchword received!");
                        socket.getOutputStream().write('2');
                        System.out.println("Sending 2...");
                    }
                }while (socket.getInputStream().available() > 0);

                //socket.getOutputStream().write(watchword.toString().getBytes());
            }else
                result = false;



            socket.close();
        } catch (IOException ignored) {
        }
        System.out.println("Closing sockets");
        System.out.println("Connection closed");
        System.out.println("Finished");
        System.out.println("Result: " + result);
    }
}