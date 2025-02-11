package datos;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class Cliente {
    //Creamos los elementos de la interfaz gráfica
    private JFrame frame;
    private JTextArea areaMensajes;
    private JTextField campoTexto;
    private JButton btnEnviar;
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String nombreUsuario;

    public Cliente(Socket socket, String nombreUsuario) {
        try {
            this.socket = socket; //Establecemos conexión con el servidor
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); //Para enviar mensajes al servidor
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream())); //Para recibir mensajes del servidor
            this.nombreUsuario = nombreUsuario;

            //Configuramos la interfaz gráfica
            frame = new JFrame("Chat Cliente");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 500);
            frame.setLayout(new BorderLayout());

            areaMensajes = new JTextArea();
            areaMensajes.setEditable(false);
            frame.add(new JScrollPane(areaMensajes), BorderLayout.CENTER);

            JPanel panelEnvio = new JPanel();
            panelEnvio.setLayout(new BorderLayout());
            campoTexto = new JTextField();
            panelEnvio.add(campoTexto, BorderLayout.CENTER);

            btnEnviar = new JButton("Enviar");
            panelEnvio.add(btnEnviar, BorderLayout.EAST);
            frame.add(panelEnvio, BorderLayout.SOUTH);

            //Configuración del evento del botón Enviar
            btnEnviar.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    enviarMensaje();
                }
            });

            frame.setVisible(true);

            //Enviamos el nombre de usuario al servidor
            bufferedWriter.write(nombreUsuario);
            bufferedWriter.newLine();
            bufferedWriter.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void enviarMensaje() { //Métod0 para enviar mensajes al servidor
        try {
            String mensajeEnviado = campoTexto.getText();
            if(mensajeEnviado.isEmpty()){
                JOptionPane.showMessageDialog(btnEnviar, "Introduce un mensaje antes de enviarlo");
                return; //Si el mensaje está vacío, no se envía
            }
            areaMensajes.append(nombreUsuario + ": " + mensajeEnviado + "\n"); //Mostramos el mensaje en el área de mensajes
            bufferedWriter.write(nombreUsuario + ": " + mensajeEnviado); //Enviamos el mensaje al servidor
            bufferedWriter.newLine();
            bufferedWriter.flush();
            campoTexto.setText(""); //Limpiamos campo de texto
        } catch (IOException e) {
            cerrarCliente(); //Si hay un error, cerramos la conexión
        }
    }

    public void buscarMensajes() { //Métod0 para recibir mensajes del servidor
        new Thread(new Runnable() { //Creamos un hilo para recibir mensajes
            @Override
            public void run() {
                String mensajeRecibido;
                try {
                    while (!socket.isClosed()) {
                        mensajeRecibido = bufferedReader.readLine(); //Leemos el mensaje del servidor
                        if (mensajeRecibido != null) {
                            areaMensajes.append(mensajeRecibido + "\n"); //Mostramos el mensaje en el área de mensajes en caso de que este no sea nulo
                        }
                    }
                } catch (IOException e) {
                    cerrarCliente();
                }
            }
        }).start();
    }

    public void cerrarCliente() { //Métod0 para cerrar la conexión con el servidor
        try {
            if (socket != null) {
                socket.close();
            }
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            System.exit(0);  //Cerramos la aplicación
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
