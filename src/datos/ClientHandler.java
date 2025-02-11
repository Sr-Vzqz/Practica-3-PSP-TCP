package datos;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>(); //Lista de clientes
    public static ArrayList<String> historialMensajes = new ArrayList<>(); //Historial de mensajes a mostrar a los clientes que se conecten
    public static Set<String> nombresUsuarios = new HashSet<>(); //Lista de nombres de usuarios
    private ServerSocket serverSocket; //Socket del servidor
    private Socket socket; //Socket del cliente
    //BufferedReader y BufferedWriter para recoger y enviar mensajes al servidor
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String nombreCliente;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            //Inicializamos los BufferedReader y BufferedWriter con los streams de entrada y salida del socket
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.nombreCliente = ""; //Inicializamos el nombre del cliente a vacío
            String comprobarNombre = bufferedReader.readLine();
            boolean nombreValido = false; //Variable para comprobar si el nombre de usuario es válido
            while(!nombreValido){
                if (nombresUsuarios.contains(comprobarNombre)) {
                    bufferedWriter.write("SERVIDOR: El nombre de usuario ya está en uso"); //Enviamos un mensaje al cliente si el nombre de usuario ya está en uso
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                    expulsarCliente(socket,bufferedReader,bufferedWriter); //Expulsamos al cliente y salimos del bucle
                    return;
                } else {
                    //Añadimos el nombre de usuario a la lista de nombres de usuarios
                    nombreCliente = comprobarNombre;
                    nombresUsuarios.add(nombreCliente);
                    nombreValido = true;
                    bufferedWriter.write("SERVIDOR: Conexión correcta");
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                }
            }
            //Añadimos el cliente a la lista de clientes
            clientHandlers.add(this);
            enviarMensaje("SERVIDOR: " + nombreCliente + " se ha conectado");
            //Enviamos el historial de mensajes al cliente
            for (String mensaje : historialMensajes){
                bufferedWriter.write(mensaje);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }


        } catch (IOException e) {
            cerrarCliente(socket, bufferedReader, bufferedWriter);
        }

    }

    public void enviarMensaje(String mensaje) { //Métod0 para enviar mensajes a los clientes
        if (!mensaje.contains("SERVIDOR:")) { //Si el mensaje no es del servidor, lo añadimos al historial, ya que no queremos mostrar los mensajes del servidor en el chat
            historialMensajes.add(mensaje);
        }
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (!clientHandler.nombreCliente.equals(this.nombreCliente)) { //Si el cliente no es el que envía el mensaje, se lo enviamos
                    clientHandler.bufferedWriter.write(mensaje);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException e) {
                cerrarCliente(socket, bufferedReader, bufferedWriter);
                throw new RuntimeException(e);
            }
        }
    }
    public void quitarCliente(){ //Métod0 para quitar un cliente de la lista de clientes
        clientHandlers.remove(this);
        nombresUsuarios.remove(this.nombreCliente);
        if (!nombreCliente.equals("")) {
            enviarMensaje("SERVIDOR: " + nombreCliente + " se ha desconectado");
        }
    }
    public void cerrarCliente(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){ //Métod0 para cerrar la conexión con el cliente
        quitarCliente(); //Quitamos al cliente de la lista de clientes
        try{ //Cerramos el socket y los streams de entrada y salida
            if (socket != null && !socket.isClosed()){
                socket.close();
            }
            if (bufferedReader != null){
                bufferedReader.close();
            }
            if (bufferedWriter != null){
                bufferedWriter.close();
            }
        } catch (IOException e) {
            quitarCliente();
        }
    }

    public void expulsarCliente(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){ //Métod0 para expulsar a un cliente, mismo métod0 que el de cerrarCliente pero sin borrar el nombre de usuario
        clientHandlers.remove(this);
        if (!nombreCliente.equals("")) {
            enviarMensaje("SERVIDOR: " + nombreCliente + " se ha desconectado");
        }
        try{
            if (socket != null && !socket.isClosed()){
                socket.close();
            }
            if (bufferedReader != null){
                bufferedReader.close();
            }
            if (bufferedWriter != null){
                bufferedWriter.close();
            }
        } catch (IOException e) {
            quitarCliente();
        }
    }


    @Override
    public void run() {
        String mensajeRecibido;
        while (!socket.isClosed()) {
            try {
                mensajeRecibido = bufferedReader.readLine();
                if (mensajeRecibido == null){
                    break;
                }
                enviarMensaje(mensajeRecibido);
            } catch (IOException e) {
                cerrarCliente(socket, bufferedReader, bufferedWriter);
            }
        }
        cerrarCliente(socket, bufferedReader, bufferedWriter);
    }
}
