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
                Socket socket = serverSocket.accept();
                System.out.println("Nuevo cliente conectado");
                ClientHandler clientHandler = new ClientHandler(socket);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void cerrarServidor() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
