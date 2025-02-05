import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class ClienteForm {
    private JFrame frame;
    private JTextArea areaMensajes;
    private JTextField campoTexto;
    private JButton btnEnviar;
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String nombreUsuario;

    public ClienteForm(Socket socket, String nombreUsuario) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.nombreUsuario = nombreUsuario;

            // Configurar la interfaz gráfica
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

            // Configuración del evento de botón "Enviar"
            btnEnviar.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    enviarMensaje();
                }
            });

            frame.setVisible(true);

            // Enviar el nombre de usuario al servidor
            bufferedWriter.write(nombreUsuario);
            bufferedWriter.newLine();
            bufferedWriter.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void enviarMensaje() {
        try {
            String mensajeEnviado = campoTexto.getText();
            areaMensajes.append(nombreUsuario+": "+mensajeEnviado+"\n");
            bufferedWriter.write(nombreUsuario + ": " + mensajeEnviado);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            campoTexto.setText("");  // Limpiar campo de texto
        } catch (IOException e) {
            cerrarCliente();
        }
    }

    public void buscarMensajes() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String mensajeRecibido;
                try {
                    while (!socket.isClosed()) {
                        mensajeRecibido = bufferedReader.readLine();
                        if (mensajeRecibido != null) {
                            areaMensajes.append(mensajeRecibido + "\n");
                        }
                    }
                } catch (IOException e) {
                    cerrarCliente();
                }
            }
        }).start();
    }

    public void cerrarCliente() {
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
            System.exit(0);  // Terminar el programa
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
