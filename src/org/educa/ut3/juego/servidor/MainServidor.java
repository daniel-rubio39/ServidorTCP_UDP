package org.educa.ut3.juego.servidor;

import org.educa.ut3.juego.hilos.Manejador;
import org.educa.ut3.juego.util.Sala;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class MainServidor {
    private static final String GREEN = "\u001B[32m";
    private static final String RESET = "\u001B[0m";
    private static final int PUERTO = 44444;
    private static final Map<String, Sala> salas = new HashMap<>();

    public static void main(String[] args) {
        try (ServerSocket servidor = new ServerSocket(PUERTO)) {
            System.out.println(GREEN + "[ATENDIENDO PETICIONES EN EL PUERTO " + PUERTO + "]" + RESET);
            System.out.println("-----------------------------------------------------");
            while (true) {
                Socket cliente = servidor.accept();
                System.out.println(GREEN + "[ATENDIENDO AL CLIENTE " + cliente.getInetAddress().getHostAddress() + ":" + cliente.getPort() + "]" + RESET);
                new Manejador(cliente, salas).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}