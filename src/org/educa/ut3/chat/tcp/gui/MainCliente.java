package org.educa.ut3.chat.tcp.gui;

import org.educa.ut3.chat.compartidos.entity.UsuarioEntity;
import org.educa.ut3.chat.compartidos.ui.ChatUI;
import org.educa.ut3.chat.tcp.gui.hilos.Mensajes;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class MainCliente {
    private static final int PORT = 44444;
    private static final String SERVER = "localhost";

    public static void main(String[] args) {
        try (Socket cliente = new Socket(SERVER, PORT);
             ObjectOutputStream oos = new ObjectOutputStream(cliente.getOutputStream());
             ObjectInputStream ois = new ObjectInputStream(cliente.getInputStream());
             DataOutputStream dos = new DataOutputStream(cliente.getOutputStream());
             DataInputStream dis = new DataInputStream(cliente.getInputStream())
        ) {
            // Solicitar nickname del cliente
            UsuarioEntity usuario = getValidUser(ois, dis, dos);

            // Iniciar la Interfaz Grafica
            ChatUI chatUI = createGUI(dos, usuario);

            // Mostrar contenido del usuario en caso de que no estuviese conectado
            String texto;
            do {
                texto = dis.readUTF();
                if (!texto.equals("[FIN]")) {
                    chatUI.getMessageArea().append(texto + "\n");
                }
            } while (!texto.equals("[FIN]"));

            // Comienza el chat
            // Lectura de mensajes
            new Mensajes(cliente, dis, chatUI, usuario).start();

            // Escritura de mensajes
            while (true) {
                // Bucle para enviar mensajes
                // Simplemente debe mantenerse abierto para poder mantener la conexion
            }
        } catch (IOException e) {
            System.err.println("[ERROR CON EL SERVIDOR] -> " + e.getMessage());
        }
    }

    // Solicita un nickname válido al usuario y lo registra en el servidor.
    // Lo encierra en un bucle hasta que el nickname sea válido.
    private static UsuarioEntity getValidUser(ObjectInputStream ois, DataInputStream dis, DataOutputStream dos) throws IOException {
        try {
            while (true) {
                // Enviar nickname al servidor
                String nickname = JOptionPane.showInputDialog("INTRODUCE EL NOMBRE DE USUARIO:");
                dos.writeUTF(nickname);

                // Leer la respuesta del servidor
                String respuesta = dis.readUTF();

                // En caso de que la respuesta sea valida devolver el usuario y enviar señal de que empieza el java.chat
                if (!respuesta.contains("ERROR")) {
                    dos.writeBoolean(true);
                    return (UsuarioEntity) ois.readObject();
                } else {
                    JOptionPane.showMessageDialog(null, "NOMBRE INVALIDO | INTENTELO DE NUEVO", "Error", JOptionPane.ERROR_MESSAGE);
                }

                dos.writeBoolean(false);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static ChatUI createGUI(DataOutputStream dos, UsuarioEntity usuario) {
        JFrame frame = new JFrame("Chat");

        ImageIcon icon = new ImageIcon("src/resources/java.chat.png");
        frame.setIconImage(icon.getImage());

        ChatUI chatUI = new ChatUI();
        chatUI.createUIComponents();

        chatUI.getRecipientLabel().setText(usuario.getNickname());
        chatUI.getInputField().addActionListener(e -> sendMessage(chatUI, dos, usuario));
        chatUI.getSendButton().addActionListener(e -> sendMessage(chatUI, dos, usuario));

        frame.setContentPane(chatUI.getMainPanel());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
        return chatUI;
    }

    // Metodo que envia el mensaje al servidor y lo muestra en nuestra area de mensajes
    private static void sendMessage(ChatUI chatUI, DataOutputStream dos, UsuarioEntity usuario) {
        try {
            // Enviamos el mensaje
            String message = usuario.getNickname() + " ~ " + chatUI.getInputField().getText().trim();
            dos.writeUTF(message + "\n");
            // Limpiamos nuestro textbox
            chatUI.getInputField().setText("");
            // Añadimos el mensaje a nuestro chat
            chatUI.getMessageArea().append(message + "\n\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}