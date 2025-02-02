package org.educa.ut3.chat.tcp.servidor.hilos;

import org.educa.ut3.chat.compartidos.entity.UsuarioEntity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Map;

public class Manejador extends Thread {

    private static final String RED = "\u001B[31m";
    private static final String RESET = "\u001B[0m";

    private final Socket cliente;
    private final List<String> mensajes;
    private final Map<UsuarioEntity, Socket> conexiones;

    public Manejador(Socket cliente, List<String> mensajes, Map<UsuarioEntity, Socket> conexiones) {
        this.cliente = cliente;
        this.mensajes = mensajes;
        this.conexiones = conexiones;
    }

    @Override
    public void run() {
        UsuarioEntity usuarioActual = null;
        try (ObjectInputStream ois = new ObjectInputStream(cliente.getInputStream());
             ObjectOutputStream oos = new ObjectOutputStream(cliente.getOutputStream());
             DataInputStream dis = new DataInputStream(cliente.getInputStream());
             DataOutputStream dos = new DataOutputStream(cliente.getOutputStream())
        ) {
            // Bucle para verificar un nickname válido
            boolean salir;
            do {
                String nickname = dis.readUTF();
                synchronized (conexiones) {
                    usuarioActual = new UsuarioEntity(nickname);
                    // Si el nickname no está en uso, lo registramos y salimos del bucle
                    if (!conexiones.containsKey(usuarioActual) && !nickname.isBlank()) {
                        conexiones.put(usuarioActual, cliente);
                        dos.writeUTF("");
                        oos.writeObject(usuarioActual);
                    } else {
                        // Si el nickname está en uso, enviamos un mensaje de error y volvemos a pedir el nickname
                        dos.writeUTF(RED + "[ERROR: Nickname invalido]\n" + RESET);
                    }
                    salir = dis.readBoolean();
                }
            } while (!salir);

            // Si el destinatario no está conectado, guardamos el mensaje en su lista de mensajes
            for (String mensaje : mensajes) {
                dos.writeUTF(mensaje);
            }
            dos.writeUTF("[FIN]");

            // Bucle del chat que intercambia los mensajes
            while (true) {
                String mensaje = dis.readUTF();
                mensajes.add(mensaje);
                if (mensaje.equals("[FIN]")) {
                    break;
                }

                // Si el destinatario está conectado, enviamos el mensaje
                for (Map.Entry<UsuarioEntity, Socket> e : conexiones.entrySet()) {
                    Socket destinatarioSocket = conexiones.get(e.getKey());
                    DataOutputStream ddos = new DataOutputStream(destinatarioSocket.getOutputStream());
                    ddos.writeUTF(mensaje);
                }
            }

            // Cuando acaba la conexion borramos al usuario para que se pueda reutilizar su nombre
            synchronized (conexiones) {
                conexiones.remove(usuarioActual);
            }

            cliente.close();
        } catch (IOException e) {
            System.err.println("[ERROR CON EL CLIENTE " + cliente.getInetAddress().getHostAddress() + ":" + cliente.getPort() + "] -> " + e.getMessage());
            // Nos aseguramos de eliminar al usuario si se produce un error
            synchronized (conexiones) {
                conexiones.remove(usuarioActual);
            }
        }
    }
}