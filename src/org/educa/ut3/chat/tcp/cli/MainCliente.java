package org.educa.ut3.chat.tcp.cli;

import org.educa.ut3.chat.compartidos.entity.UsuarioEntity;
import org.educa.ut3.chat.tcp.cli.hilos.Mensajes;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class MainCliente {
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String PURPLE = "\u001B[35m";
    private static final String RESET = "\u001B[0m";
    private static final int PUERTO = 44444;
    private static final String SERVIDOR = "localhost";

    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in);
             Socket cliente = new Socket(SERVIDOR, PUERTO);
             ObjectOutputStream oos = new ObjectOutputStream(cliente.getOutputStream());
             ObjectInputStream ois = new ObjectInputStream(cliente.getInputStream());
             DataOutputStream dos = new DataOutputStream(cliente.getOutputStream());
             DataInputStream dis = new DataInputStream(cliente.getInputStream())
        ) {
            // Solicitar nickname del cliente
            UsuarioEntity usuario = getValidUser(sc, ois, dis, dos);

            // Leer mensaje de bienvenida
            System.out.println("-------------------------------------------------");
            System.out.println(GREEN + "[BIENVENIDO " + usuario.getNickname().toUpperCase() + "]" + RESET);
            System.out.println("-------------------------------------------------");

            // Mostrar contenido del usuario en caso de que no estuviese conectado
            String texto;
            do {
                texto = dis.readUTF();
                if (!texto.equals("[FIN]")) {
                    System.out.println(texto);
                }
            } while (!texto.equals("[FIN]"));

            // Comienza el java.chat
            // Lectura de mensajes
            new Mensajes(usuario, cliente, dis).start();

            // Escritura de mensajes
            while (true) {
                System.out.print(PURPLE + usuario.getNickname() + " ~ " + RESET);
                dos.writeUTF(YELLOW + usuario.getNickname() + " ~ " + RESET + sc.nextLine());
            }
        } catch (IOException e) {
            System.err.println("[ERROR CON EL SERVIDOR] -> " + e.getMessage());
        }
    }

    // Solicita un nickname válido al usuario y lo registra en el servidor.
    // Lo encierra en un bucle hasta que el nickname sea válido.
    private static UsuarioEntity getValidUser(Scanner sc, ObjectInputStream ois, DataInputStream dis, DataOutputStream dos) throws IOException {
        try {
            while (true) {
                // Enviar nickname al servidor
                System.out.print("NICKNAME: ");
                dos.writeUTF(sc.nextLine());

                // Leer la respuesta del servidor
                String respuesta = dis.readUTF();
                System.out.print(respuesta);

                // En caso de que la respuesta sea valida devolver el usuario y enviar señal de que empieza el java.chat
                if (!respuesta.contains("ERROR")) {
                    dos.writeBoolean(true);
                    return (UsuarioEntity) ois.readObject();
                }

                dos.writeBoolean(false);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}