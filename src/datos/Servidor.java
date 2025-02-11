package datos;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    private ServerSocket serverSocket;

    public Servidor(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void iniciarServidor() {
        System.out.println("Servidor iniciado");
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept(); //Esperamos a que un cliente se conecte
                System.out.println("Nuevo cliente conectado");
                ClientHandler clientHandler = new ClientHandler(socket); //Creamos un clientHandler
                Thread thread = new Thread(clientHandler); //Creamos un hilo para el clientHandler y lo iniciamos
                thread.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
