import datos.Cliente;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class MainCliente {
public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    System.out.println("Ingrese su nombre de usuario: ");
    String nombreUsuario = sc.nextLine();

    Socket socket = null;
    try {
        socket = new Socket("localhost", 1234);
    } catch (IOException e) {
        e.printStackTrace();
    }

    Cliente cliente = new Cliente(socket, nombreUsuario);
    cliente.buscarMensajes();
    }
}