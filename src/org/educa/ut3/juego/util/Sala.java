package org.educa.ut3.juego.util;

import org.educa.ut3.juego.hilos.Manejador;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sala {
    private final List<Manejador> jugadores;
    private final Map<Manejador, String> elecciones;
    private final String codigo;

    public Sala(String codigo) {
        this.jugadores = new ArrayList<>();
        this.elecciones = new HashMap<>();
        this.codigo = codigo;
    }

    /**
     * Agrega un jugador a la sala. Si la sala está llena o el nombre ya está escogido, se rechaza el jugador.
     *
     * @param jugador El jugador que intenta unirse a la sala.
     * @return true si el jugador fue agregado exitosamente, false si no se pudo agregar.
     * @throws IOException Si ocurre un error al enviar un mensaje al jugador.
     */
    public synchronized boolean agregarJugador(Manejador jugador) throws IOException {
        // Verificamos si la sala está llena
        if (jugadores.size() >= 2) {
            jugador.enviarMensaje("CODIGO DE SALA LLENO");
            return false;
        }
        // Verificamos si el nombre ya está escogido
        if (jugadores.size() == 1) {
            if (jugadores.get(0).getNombre().equalsIgnoreCase(jugador.getNombre())) {
                jugador.enviarMensaje("NOMBRE YA ESCOGIDO");
                return false;
            }
        }
        jugadores.add(jugador);
        return true;
    }

    /**
     * Envía los nombres de los oponentes a cada jugador
     *
     * @throws IOException Si ocurre un error al enviar el mensaje a los jugadores.
     */
    public synchronized void enviarNombres() throws IOException {
        if (jugadores.size() == 2) {
            jugadores.get(0).enviarMensaje(jugadores.get(1).getNombre());
            jugadores.get(1).enviarMensaje(jugadores.get(0).getNombre());
        }
    }

    /**
     * Registra la jugada de un jugador. Si ambos jugadores han realizado su jugada, se determina el ganador.
     *
     * @param jugador  El jugador que realiza la jugada.
     * @param eleccion La jugada que el jugador ha elegido ("piedra", "papel" o "tijera").
     * @throws IOException          Si ocurre un error al enviar el mensaje al jugador.
     * @throws InterruptedException Si el hilo se ve interrumpido durante la espera.
     */
    public synchronized void registrarJugada(Manejador jugador, String eleccion) throws IOException, InterruptedException {
        if (!elecciones.containsKey(jugador)) {
            // Validamos que la elección sea una jugada válida
            if (eleccion.matches("(?i)^(piedra|papel|tijera)$")) {
                elecciones.put(jugador, eleccion);
                jugador.enviarMensaje("ESPERANDO RIVAL");
            }
        }
        // Si ambos jugadores han hecho su jugada, determinamos el ganador
        if (elecciones.size() == 2) {
            determinarGanador();
        }
    }

    /**
     * Determina el ganador de la partida basado en las elecciones de los jugadores.
     * Envía el resultado a ambos jugadores.
     *
     * @throws IOException Si ocurre un error al enviar el resultado a los jugadores.
     */
    private void determinarGanador() throws IOException {
        Manejador jugador1 = jugadores.get(0);
        Manejador jugador2 = jugadores.get(1);
        String eleccion1 = elecciones.get(jugador1);
        String eleccion2 = elecciones.get(jugador2);

        String resultado;
        if (eleccion1.equals(eleccion2)) {
            resultado = "EMPATE";
        } else if (eleccion1.equalsIgnoreCase("PIEDRA") && eleccion2.equalsIgnoreCase("TIJERA") ||
                eleccion1.equalsIgnoreCase("TIJERA") && eleccion2.equalsIgnoreCase("PAPEL") ||
                eleccion1.equalsIgnoreCase("PAPEL") && eleccion2.equalsIgnoreCase("PIEDRA")
        ) {
            resultado = "GANO " + jugador1.getNombre();
        } else {
            resultado = "GANO " + jugador2.getNombre();
        }

        jugador1.enviarMensaje(resultado);
        jugador2.enviarMensaje(resultado);
        // Limpiamos las elecciones para la siguiente ronda
        elecciones.clear();
    }

    /**
     * Remueve un jugador de la sala.
     *
     * @param jugador El jugador que debe ser removido.
     */
    public synchronized void removerJugador(Manejador jugador) {
        jugadores.remove(jugador);
    }

    public List<Manejador> getJugadores() {
        return jugadores;
    }

    public String getCodigo() {
        return codigo;
    }
}