package org.educa.ut3.chat.tcp.servidor;

import org.educa.ut3.chat.compartidos.entity.UsuarioEntity;
import org.educa.ut3.chat.tcp.servidor.hilos.Manejador;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainServidor {

    private static final String GREEN = "\u001B[32m";
    private static final String RESET = "\u001B[0m";

    private static final int PUERTO = 44444;

    public static void main(String[] args) {

        List<String> mensajes = new ArrayList<>();
        Map<UsuarioEntity, Socket> conexiones = new HashMap<>();

        try (ServerSocket servidor = new ServerSocket(PUERTO)) {
            System.out.println(GREEN + "[ATENDIENDO PETICIONES EN EL PUERTO " + PUERTO + "]" + RESET);
            System.out.println("-------------------------------------------------");

            while (true) {
                Socket cliente = servidor.accept();
                System.out.println(GREEN + "[ATENDIENDO AL CLIENTE " + cliente.getInetAddress().getHostAddress() + ":" + cliente.getPort() + "]" + RESET);
                Manejador manejador = new Manejador(cliente, mensajes, conexiones);
                manejador.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
