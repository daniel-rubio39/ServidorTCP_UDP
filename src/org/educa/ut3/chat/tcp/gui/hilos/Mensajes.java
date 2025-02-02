package org.educa.ut3.chat.tcp.gui.hilos;

import org.educa.ut3.chat.compartidos.entity.UsuarioEntity;
import org.educa.ut3.chat.compartidos.ui.ChatUI;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

public class Mensajes extends Thread {

    private final Socket cliente;
    private final DataInputStream dis;
    private final ChatUI chatUI;
    private final UsuarioEntity usuario;

    public Mensajes(Socket cliente, DataInputStream dis, ChatUI chatUI, UsuarioEntity usuario) {
        this.cliente = cliente;
        this.dis = dis;
        this.chatUI = chatUI;
        this.usuario = usuario;
    }

    @Override
    public void run() {
        try {
            // Mientras que el cliente este conectado el hilo estara constantemente leyendo mensajes
            while (!cliente.isClosed()) {
                try {
                    // Leemos el mensaje
                    String mensaje = dis.readUTF();
                    // Mostramos el mensaje en nuestro chat
                    if (!mensaje.contains(usuario.getNickname()) && mensaje.contains("~")) {
                        chatUI.getMessageArea().append(mensaje + "\n");
                    }
                } catch (EOFException e) {
                    System.err.println("[ERROR] -> " + e.getMessage());
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("[ERROR CON EL SERVIDOR] -> " + e.getMessage());
        }
    }
}