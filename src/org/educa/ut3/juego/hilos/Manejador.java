package org.educa.ut3.juego.hilos;

import org.educa.ut3.juego.util.Sala;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;

public class Manejador extends Thread {
    private final Socket cliente;
    private final Map<String, Sala> salas;
    private Sala sala;
    private String nombre;
    private DataOutputStream dos;

    public Manejador(Socket cliente, Map<String, Sala> salas) {
        this.cliente = cliente;
        this.salas = salas;
    }

    @Override
    public void run() {
        try (DataInputStream dis = new DataInputStream(cliente.getInputStream())) {
            dos = new DataOutputStream(cliente.getOutputStream());

            String codigoSala;
            boolean salir;
            do {
                // Se lee el nombre y código de sala del cliente
                nombre = dis.readUTF();
                codigoSala = dis.readUTF();

                synchronized (salas) {
                    sala = salas.get(codigoSala);

                    // Si la sala no existe, la creamos
                    if (sala == null) {
                        sala = new Sala(codigoSala);
                        salas.put(codigoSala, sala);
                    }

                    // Intentamos agregar al jugador a la sala si no podemos se repite el bucle, si podemos, salimos
                    salir = sala.agregarJugador(this);
                }
            } while (!salir);

            // Esperamos hasta que ambos jugadores estén conectados
            while (sala.getJugadores().size() < 2) {
                dos.writeUTF("ESPERANDO RIVAL...");
                Thread.sleep(100);
            }

            // Notificamos que el rival fue encontrado
            dos.writeUTF("RIVAL ENCONTRADO");
            // Enviamos los nombres de los jugadores
            sala.enviarNombres();

            while (true) {
                // Leemos la jugada del jugador
                String eleccion = dis.readUTF();
                // Registramos la jugada
                if (!eleccion.equalsIgnoreCase(codigoSala)) {
                    sala.registrarJugada(this, eleccion);
                }
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("[ERROR con el Cliente] -> " + e.getMessage());
        } finally {
            try {
                // Si hay una sala, eliminamos al jugador de ella
                if (sala != null) {
                    sala.removerJugador(this);
                    // Si hay otro jugador, le notificamos que el rival se desconectó
                    if (sala.getJugadores().size() == 1) {
                        sala.getJugadores().get(0).enviarMensaje("RIVAL DESCONECTADO");
                    }
                    // Si no hay jugadores en la sala, la eliminamos del mapa
                    if (sala.getJugadores().isEmpty()) {
                        salas.remove(sala.getCodigo());
                    }
                }
                // Cerramos la conexión con el cliente
                cliente.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Envía un mensaje al cliente conectado.
     *
     * @param mensaje El mensaje que se enviará al cliente.
     * @throws IOException Si ocurre un error al enviar el mensaje.
     */
    public void enviarMensaje(String mensaje) throws IOException {
        dos.writeUTF(mensaje);
        dos.flush();
    }

    public String getNombre() {
        return nombre;
    }
}