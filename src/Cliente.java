import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Cliente{
public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    System.out.println("Ingrese su nombre de usuario: ");
    String nombreUsuario = sc.nextLine();

    Socket socket = null;
    try {
        socket = new Socket("localhost", 12345);
    } catch (IOException e) {
        e.printStackTrace();
    }

    ClienteForm cliente = new ClienteForm(socket, nombreUsuario);
    cliente.buscarMensajes();
    }
}