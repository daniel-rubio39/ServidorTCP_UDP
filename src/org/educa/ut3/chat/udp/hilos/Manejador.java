package org.educa.ut3.chat.udp.hilos;

import org.educa.ut3.chat.compartidos.entity.UsuarioEntity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

public class Manejador extends Thread {

    private final DatagramSocket servidor;
    private final DatagramPacket paquete;
    private final List<String> mensajes;
    private final Map<UsuarioEntity, InetSocketAddress> conexiones;
    private UsuarioEntity usuarioActual;

    public Manejador(DatagramPacket paquete, DatagramSocket servidor, List<String> mensajes, Map<UsuarioEntity, InetSocketAddress> conexiones) {
        this.paquete = paquete;
        this.servidor = servidor;
        this.mensajes = mensajes;
        this.conexiones = conexiones;
        this.usuarioActual = null;
    }

    @Override
    public void run() {
        try {
            // Verificar si el usuario ya esta registrado
            usuarioActual = new UsuarioEntity(new String(paquete.getData(), 0, paquete.getLength()));
            // Verificar un nickname válido
            synchronized (conexiones) {
                // Si el nickname no está en uso, lo registramos y salimos del bucle
                if (!conexiones.containsKey(usuarioActual) && !usuarioActual.getNickname().isBlank()) {
                    conexiones.put(usuarioActual, new InetSocketAddress(paquete.getAddress(), paquete.getPort()));
                    enviarMensaje("", paquete.getAddress(), paquete.getPort());
                    enviarUsuario(usuarioActual, paquete.getAddress(), paquete.getPort());
                } else {
                    // Si el nickname está en uso, enviamos un mensaje de error y seguimos pidiendo el nickname
                    enviarMensaje("[ERROR] -> Nickname invalido", paquete.getAddress(), paquete.getPort());
                }
            }

            // Si el destinatario no está conectado, guardamos el mensaje en su lista de mensajes
            for (String mensaje : mensajes) {
                enviarMensaje(mensaje, paquete.getAddress(), paquete.getPort());
            }
            enviarMensaje("[FIN]", paquete.getAddress(), paquete.getPort());

            // Bucle del java.chat que intercambia los mensajes
            while (true) {
                String mensaje = recibirMensaje();
                if (mensaje.equals("[FIN]")) {
                    break;
                } else if (mensaje.contains("~")) {
                    mensajes.add(mensaje);
                    // Si el destinatario está conectado, enviamos el mensaje
                    for (Map.Entry<UsuarioEntity, InetSocketAddress> e : conexiones.entrySet()) {
                        InetSocketAddress destinatarioSocket = conexiones.get(e.getKey());
                        enviarMensaje(mensaje, destinatarioSocket.getAddress(), destinatarioSocket.getPort());
                    }
                }
            }

            // Cuando acaba la conexion borramos al usuario para que se pueda reutilizar su nombre
            synchronized (conexiones) {
                conexiones.remove(usuarioActual);
            }

        } catch (IOException e) {
            System.err.println("[ERROR CON EL CLIENTE " + paquete.getAddress().getHostAddress() + ":" + paquete.getPort() + "] -> " + e.getMessage());
            // Nos aseguramos de eliminar al usuario si se produce un error
            synchronized (conexiones) {
                conexiones.remove(usuarioActual);
            }
        }
    }

    // Metodo para enviar mensajes
    private void enviarMensaje(String mensaje, InetAddress ip, int puerto) throws IOException {
        byte[] datos = mensaje.getBytes();
        DatagramPacket paquete = new DatagramPacket(datos, datos.length, ip, puerto);
        servidor.send(paquete);
    }

    // Metodo para recibir mensajes
    private String recibirMensaje() throws IOException {
        byte[] datos = new byte[1024];
        DatagramPacket paquete = new DatagramPacket(datos, datos.length);
        servidor.receive(paquete);
        return new String(paquete.getData(), 0, paquete.getLength());
    }

    // Metodo para enviar objetos (Usuarios)
    private void enviarUsuario(UsuarioEntity usuario, InetAddress ip, int puerto) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(usuario);
        objectOutputStream.flush();
        byte[] datos = byteArrayOutputStream.toByteArray();
        DatagramPacket paquete = new DatagramPacket(datos, datos.length, ip, puerto);
        servidor.send(paquete);
    }

}