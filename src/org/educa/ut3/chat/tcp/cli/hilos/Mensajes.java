package org.educa.ut3.chat.tcp.cli.hilos;

import org.educa.ut3.chat.compartidos.entity.UsuarioEntity;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

public class Mensajes extends Thread {

    private static final String PURPLE = "\u001B[35m";
    private static final String RESET = "\u001B[0m";

    private final UsuarioEntity usuarioActual;
    private final Socket cliente;
    private final DataInputStream dis;

    public Mensajes(UsuarioEntity usuarioActual, Socket cliente, DataInputStream dis) {
        this.usuarioActual = usuarioActual;
        this.cliente = cliente;
        this.dis = dis;
    }

    @Override
    public void run() {
        try {
            while (!cliente.isClosed()) {
                try {
                    String mensaje = dis.readUTF();
                    if (!mensaje.contains(usuarioActual.getNickname())) {
                        // Se posiciona al principio de la línea y imprime el mensaje, de esta manera se borra la linea del usuario en la que escribe
                        System.out.println("\r" + mensaje);
                        // Vuelve a mostrar el prompt del usuario
                        System.out.print(PURPLE + usuarioActual.getNickname() + " ~ " + RESET);
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