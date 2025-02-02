package org.educa.ut3.chat.udp.cli;

import org.educa.ut3.chat.compartidos.entity.UsuarioEntity;
import org.educa.ut3.chat.udp.cli.hilos.Mensajes;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class MainCliente {
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String PURPLE = "\u001B[35m";
    private static final String RESET = "\u001B[0m";
    private static final int PUERTO = 44444;
    private static final String SERVIDOR = "localhost";

    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in);
             DatagramSocket cliente = new DatagramSocket()
        ) {
            InetAddress ip = InetAddress.getByName(SERVIDOR);
            // Solicitar nickname del cliente
            UsuarioEntity usuario = getValidUser(sc, cliente, ip);

            // Leer mensaje de bienvenida
            System.out.println("-------------------------------------------------");
            System.out.println(GREEN + "[BIENVENIDO " + usuario.getNickname().toUpperCase() + "]" + RESET);
            System.out.println("-------------------------------------------------");

            // Mostrar contenido del usuario en caso de que no estuviese conectado
            String texto;
            do {
                texto = recibirMensaje(cliente);
                if (!texto.equals("[FIN]")) {
                    System.out.println(texto);
                }
            } while (!texto.equals("[FIN]"));

            // Comienza el java.chat
            // Lectura de mensajes
            new Mensajes(cliente, usuario).start();

            // Escritura de mensajes
            while (true) {
                System.out.print("\r");
                System.out.print("\r" + PURPLE + usuario.getNickname() + " ~ " + RESET);
                String mensaje = sc.nextLine();
                // Como en udp no se asegura la llegada de paquetes evitamos los paquetes perdidos que vienen sin mensaje
                if (!mensaje.isEmpty()) {
                    enviarMensaje(cliente, YELLOW + usuario.getNickname() + " ~ " + RESET + mensaje, ip);
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
    private static UsuarioEntity getValidUser(Scanner sc, DatagramSocket cliente, InetAddress ip) throws IOException, ClassNotFoundException {
        UsuarioEntity usuario = null;
        // Enviar nickname al servidor
        System.out.print("NICKNAME: ");
        String nickname = sc.nextLine();
        enviarMensaje(cliente, nickname, ip);

        // Leer la respuesta del servidor
        String respuesta = recibirMensaje(cliente).trim();

        // En caso de que la respuesta sea valida devolver el usuario y enviar señal de que empieza el java.chat
        if (!respuesta.contains("ERROR")) {
            usuario = recibirUsuario(cliente);
        } else {
            System.out.println(RED + respuesta + RESET);
            System.exit(0);
        }
        return usuario;
    }

    // Metodo para enviar mensajes
    private static void enviarMensaje(DatagramSocket cliente, String nickname, InetAddress ip) throws IOException {
        byte[] datos = nickname.getBytes();
        DatagramPacket paquete = new DatagramPacket(datos, datos.length, ip, PUERTO);
        cliente.send(paquete);
    }

    // Metodo para enviar objetos (Usuarios)
    private static void enviarUsuario(DatagramSocket cliente, UsuarioEntity usuario, InetAddress ip) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(usuario);
        objectOutputStream.flush();
        byte[] datos = byteArrayOutputStream.toByteArray();
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