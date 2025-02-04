package org.educa.ut3.chat.udp.gui;

import org.educa.ut3.chat.compartidos.entity.UsuarioEntity;
import org.educa.ut3.chat.compartidos.ui.ChatUI;

import javax.swing.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MainCliente {
    private static final int PUERTO = 44444;
    private static final String SERVIDOR = "localhost";

    public static void main(String[] args) {
        try (DatagramSocket cliente = new DatagramSocket()) {
            InetAddress ip = InetAddress.getByName(SERVIDOR);
            // Solicitar nickname del cliente
            UsuarioEntity usuario = getValidUser(cliente, ip);

            // Iniciar la Interfaz Grafica
            ChatUI chatUI = createGUI(cliente, ip, usuario);

            // Mostrar contenido del usuario en caso de que no estuviese conectado
            String texto;
            do {
                texto = recibirMensaje(cliente);
                if (!texto.equals("[FIN]")) {
                    chatUI.getMessageArea().append(texto + "\n\n");
                }
            } while (!texto.equals("[FIN]"));

            // Comienza el java.chat
            // Escritura y lectura de mensajes
            while (true) {
                String mensaje = recibirMensaje(cliente);
                if (!mensaje.contains(usuario.getNickname())) {
                    chatUI.getMessageArea().append(mensaje + "\n\n");
                }
            }
        } catch (IOException e) {
            System.err.println("[ERROR CON EL SERVIDOR] -> " + e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    // Solicita un nickname válido al usuario y lo registra en el servidor.
    // Lo encierra en un bucle hasta que el nickname sea válido.
    private static UsuarioEntity getValidUser(DatagramSocket cliente, InetAddress ip) throws IOException, ClassNotFoundException {
        UsuarioEntity usuario = null;
        // Enviar nickname al servidor
        String nickname = JOptionPane.showInputDialog("INTRODUCE EL NOMBRE DE USUARIO:");
        enviarMensaje(cliente, nickname, ip);

        // Leer la respuesta del servidor
        String respuesta = recibirMensaje(cliente).trim();

        // En caso de que la respuesta sea valida devolver el usuario y enviar señal de que empieza el java.chat
        if (!respuesta.contains("ERROR")) {
            usuario = recibirUsuario(cliente);
        } else {
            JOptionPane.showMessageDialog(null, "NOMBRE INVALIDO | SALIENDO...", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
        return usuario;
    }

    private static ChatUI createGUI(DatagramSocket cliente, InetAddress ip, UsuarioEntity usuario) {
        JFrame frame = new JFrame("Chat");

        ImageIcon icon = new ImageIcon("src/resources/java.chat.png");
        frame.setIconImage(icon.getImage());

        ChatUI chatUI = new ChatUI();
        chatUI.createUIComponents();

        chatUI.getRecipientLabel().setText(usuario.getNickname());
        chatUI.getInputField().addActionListener(e -> sendMessage(chatUI, cliente, ip, usuario));
        chatUI.getSendButton().addActionListener(e -> sendMessage(chatUI, cliente, ip, usuario));

        frame.setContentPane(chatUI.getMainPanel());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
        return chatUI;
    }

    // Metodo para enviar un mensaje y mostrarlo en nuestra area de mensajes
    private static void sendMessage(ChatUI chatUI, DatagramSocket cliente, InetAddress ip, UsuarioEntity usuario) {
        try {
            // Enviamos el paquete al servidor
            String message = usuario.getNickname() + " ~ " + chatUI.getInputField().getText().trim();
            enviarMensaje(cliente, message, ip);
            // Limpiamos nuestro textbox
            chatUI.getInputField().setText("");
            // Mostramos el mensaje en nuestro chat
            chatUI.getMessageArea().append(message + "\n\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Metodo para enviar mensajes
    private static void enviarMensaje(DatagramSocket cliente, String nickname, InetAddress ip) throws IOException {
        byte[] datos = nickname.getBytes();
        DatagramPacket paquete = new DatagramPacket(datos, datos.length, ip, PUERTO);
        cliente.send(paquete);
    }

    // Metodo para recibir mensajes
    private static String recibirMensaje(DatagramSocket cliente) throws IOException {
        byte[] datos = new byte[1024];
        DatagramPacket paquete = new DatagramPacket(datos, datos.length);
        cliente.receive(paquete);
        return new String(paquete.getData(), 0, paquete.getLength());
    }

    // Metodo para recibir objetos (Usuarios)
    private static UsuarioEntity recibirUsuario(DatagramSocket cliente) throws IOException, ClassNotFoundException {
        byte[] datos = new byte[1024];
        DatagramPacket paquete = new DatagramPacket(datos, datos.length);
        cliente.receive(paquete);

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(paquete.getData());
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        return (UsuarioEntity) objectInputStream.readObject();
    }
}