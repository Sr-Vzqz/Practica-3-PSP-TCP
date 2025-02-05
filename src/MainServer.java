import java.io.IOException;
import java.net.ServerSocket;

public class MainServer {
    public static void main(String[] args) {
        try{
            ServerSocket serverSocket = new ServerSocket(12345);
            Servidor servidor = new Servidor(serverSocket);
            servidor.iniciarServidor();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
