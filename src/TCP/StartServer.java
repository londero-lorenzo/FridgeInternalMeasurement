package TCP;

public class StartServer {
    public static void main(String[] args){
        ESP32Server server = new ESP32Server(11000);
        server.start();
    }
}
