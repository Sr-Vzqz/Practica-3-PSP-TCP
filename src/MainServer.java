import datos.Servidor;

import java.io.IOException;
import java.net.ServerSocket;

public class MainServer {
    public static void main(String[] args) {
        try{
            ServerSocket serverSocket = new ServerSocket(1234);
            Servidor servidor = new Servidor(serverSocket);
            servidor.iniciarServidor();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
