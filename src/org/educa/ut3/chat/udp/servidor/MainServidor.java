package org.educa.ut3.chat.udp.servidor;

import org.educa.ut3.chat.compartidos.entity.UsuarioEntity;
import org.educa.ut3.chat.udp.hilos.Manejador;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainServidor {

    private static final String GREEN = "\u001B[32m";
    private static final String RESET = "\u001B[0m";

    private static final int PUERTO = 44444;
    private static final Set<InetSocketAddress> clientesAtendidos = new HashSet<>();

    public static void main(String[] args) {

        List<String> mensajes = new ArrayList<>();
        Map<UsuarioEntity, InetSocketAddress> conexiones = new HashMap<>();

        try (DatagramSocket servidor = new DatagramSocket(PUERTO)) {
            System.out.println(GREEN + "[ATENDIENDO PETICIONES EN EL PUERTO " + PUERTO + "]" + RESET);
            System.out.println("-------------------------------------------------");

            while (true) {
                byte[] datos = new byte[1024];
                DatagramPacket paquete = new DatagramPacket(datos, datos.length);
                servidor.receive(paquete);

                InetSocketAddress clienteAddress = new InetSocketAddress(paquete.getAddress(), paquete.getPort());

                // Verificar si el cliente ya ha sido atendido
                // Sin esta condicion el servidor no funciona y se reinicia la conexion constantemente
                if (!clientesAtendidos.contains(clienteAddress)) {
                    System.out.println(GREEN + "[ATENDIENDO AL CLIENTE " + paquete.getAddress() + ":" + paquete.getPort() + "]" + RESET);
                    clientesAtendidos.add(clienteAddress);

                    Manejador manejador = new Manejador(paquete, servidor, mensajes, conexiones);
                    manejador.start();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}