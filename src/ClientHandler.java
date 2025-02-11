import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    public static ArrayList<String> historialMensajes = new ArrayList<>();
    public static Set<String> nombresUsuarios = new HashSet<>();
    private ServerSocket serverSocket;
    private Socket socket;
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
            this.nombreCliente = "";
            String comprobarNombre = bufferedReader.readLine();
            boolean nombreValido = false;
            while(!nombreValido){
                if (nombresUsuarios.contains(comprobarNombre)) {
                    bufferedWriter.write("SERVIDOR: El nombre de usuario ya está en uso");
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                    expulsarCliente(socket,bufferedReader,bufferedWriter);
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

    public void enviarMensaje(String mensaje) {
        if (!mensaje.contains("SERVIDOR:")) {
            historialMensajes.add(mensaje);
        }
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (!clientHandler.nombreCliente.equals(this.nombreCliente)) {

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
    public void quitarCliente(){
        clientHandlers.remove(this);
        nombresUsuarios.remove(this.nombreCliente);
        if (!nombreCliente.equals("")) {
            enviarMensaje("SERVIDOR: " + nombreCliente + " se ha desconectado");
        }
    }
    public void cerrarCliente(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        quitarCliente();
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

    public void expulsarCliente(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
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

    public void cerrarClienteSinBorrarNombre(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        clientHandlers.remove(this);
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
