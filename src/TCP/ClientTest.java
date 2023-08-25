package TCP;

class ClientTest {

    public static void main(String[] args){
        Client client = new Client();
        String ip = "192.168.178.136";
        int port = 11000;
        client.startClient(ip, port);
    }
}