package org.educa.ut3.chat.udp.gui.hilos;

import org.educa.ut3.chat.compartidos.entity.UsuarioEntity;
import org.educa.ut3.chat.compartidos.ui.ChatUI;

import java.io.EOFException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Mensajes extends Thread {

    private final UsuarioEntity usuarioActual;
    private final DatagramSocket cliente;
    private final ChatUI chatUI;

    public Mensajes(DatagramSocket cliente, UsuarioEntity usuarioActual, ChatUI chatUI) {
        this.cliente = cliente;
        this.usuarioActual = usuarioActual;
        this.chatUI = chatUI;
    }

    @Override
    public void run() {
        try {
            while (true) {
                try {
                    String mensaje = recibirMensaje(cliente);
                    if (!mensaje.contains(usuarioActual.getNickname())) {
                        chatUI.getMessageArea().append(mensaje + "\n\n");
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

    private String recibirMensaje(DatagramSocket cliente) throws IOException {
        byte[] datos = new byte[1024];
        DatagramPacket paquete = new DatagramPacket(datos, datos.length);
        cliente.receive(paquete);
        return new String(paquete.getData(), 0, paquete.getLength());
    }
}