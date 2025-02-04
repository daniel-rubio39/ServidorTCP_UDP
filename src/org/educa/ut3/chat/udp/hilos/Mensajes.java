package org.educa.ut3.chat.udp.hilos;

import org.educa.ut3.chat.compartidos.entity.UsuarioEntity;

import java.io.EOFException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Mensajes extends Thread {

    private static final String PURPLE = "\u001B[35m";
    private static final String RESET = "\u001B[0m";

    private final UsuarioEntity usuarioActual;
    private final DatagramSocket cliente;

    public Mensajes(DatagramSocket cliente, UsuarioEntity usuarioActual) {
        this.cliente = cliente;
        this.usuarioActual = usuarioActual;
    }

    @Override
    public void run() {
        try {
            while (true) {
                try {
                    String mensaje = recibirMensaje(cliente);
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

    // Metodo para recibir mensajes
    private String recibirMensaje(DatagramSocket cliente) throws IOException {
        byte[] datos = new byte[1024];
        DatagramPacket paquete = new DatagramPacket(datos, datos.length);
        cliente.receive(paquete);
        return new String(paquete.getData(), 0, paquete.getLength());
    }
}