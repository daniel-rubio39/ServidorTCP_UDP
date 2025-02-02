package org.educa.ut3.juego.cliente;

import org.educa.ut3.juego.ui.CircularImageButton;
import org.educa.ut3.juego.ui.PiedraPapelTijeraUI;

import javax.swing.*;
import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;

public class MainCliente {
    private static final String SERVIDOR = "localhost";
    private static final int PUERTO = 44444;

    public static void main(String[] args) {
        try (Socket cliente = new Socket(SERVIDOR, PUERTO);
             DataOutputStream dos = new DataOutputStream(cliente.getOutputStream());
             DataInputStream dis = new DataInputStream(cliente.getInputStream())
        ) {
            PiedraPapelTijeraUI ui = generateUI();

            // Solicitamos el nombre del jugador y el codigo de sala
            ui.askForName();
            ui.askForCode();

            boolean salir = false;
            while (!salir) {
                // Enviamos el nombre y el código de la sala al servidor
                dos.writeUTF(ui.getPlayerName());
                dos.writeUTF(ui.getSala());
                dos.flush();

                // Leemos la respuesta del servidor
                String respuesta = dis.readUTF();
                ui.getStatusLabel().setText(respuesta);

                switch (respuesta) {
                    // El jugador está listo para jugar
                    case "RIVAL ENCONTRADO" -> salir = true;
                    // Si la sala está llena, muestra un mensaje de error y pide un nuevo código
                    case "CODIGO DE SALA LLENO" -> {
                        JOptionPane.showMessageDialog(null, "LA SALA ESTA LLENA, INTRODUCE UN NUEVO CODIGO", "Error", JOptionPane.ERROR_MESSAGE);
                        ui.setSala("");
                        ui.askForCode();
                    }
                    // Si el nombre ya está en uso, muestra un mensaje de error y pide un nuevo nombre
                    case "NOMBRE YA ESCOGIDO" -> {
                        JOptionPane.showMessageDialog(null, "EL NOMBRE YA ESTA ESCOGIDO, INTRODUCE UN NUEVO NOMBRE", "Error", JOptionPane.ERROR_MESSAGE);
                        ui.setPlayerName("");
                        ui.askForName();
                    }
                }
            }

            // Una vez que se encuentra un rival, muestra el nombre del rival en la interfaz
            String rival = dis.readUTF();
            ui.setOponentName(rival);
            ui.getPlayersLabel().setText(ui.getPlayersLabel().getText().replace("***", rival));

            while (true) {
                // Esperamos la elección del jugador
                ui.getStatusLabel().setText("ELIGE TU RESPUESTA");
                enableGameButtons(dos, ui);

                String estado;
                boolean roundOver = false;
                // Bucle que continúa hasta que termine la ronda
                do {
                    // Leemos el estado del juego
                    estado = dis.readUTF();
                    // Mostramos el estado del juego
                    ui.getStatusLabel().setText(estado);

                    // Si el rival se desconecta se cierra la app
                    if (estado.contains("DESCONECTADO")) {
                        JOptionPane.showMessageDialog(null, "RIVAL DESCONECTADO | SALIENDO...", "Error", JOptionPane.ERROR_MESSAGE);
                        System.exit(0);
                    }

                    // Si el estado contiene el nombre del jugador, incrementa el contador, porque ha ganado
                    if (estado.contains(ui.getPlayerName())) {
                        ui.setContador(ui.getContador() + 1);
                        String text = ui.getScoreLabel().getText();
                        String updatedText = text.substring(0, text.length() - 1) + ui.getContador();
                        ui.getScoreLabel().setText(updatedText);
                    }
                    // Si el estado contiene el nombre del rival, nuestro nombre o es empate, termina la ronda
                    if (estado.contains(ui.getOponentName()) || estado.contains(ui.getPlayerName()) || estado.equals("EMPATE")) {
                        roundOver = true;
                    }
                } while (!roundOver);

                // Deshabilita los botones después de que termine la ronda
                disabledGameButtons(ui);
                // Pausa antes de continuar con la siguiente ronda
                Thread.sleep(3000);
            }
        } catch (IOException e) {
            System.err.println("[ERROR con el Servidor] -> " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error de conexión con el servidor", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    // Habilita los botones del juego y asigna las acciones correspondientes
    private static void enableGameButtons(DataOutputStream dos, PiedraPapelTijeraUI ui) {
        for (CircularImageButton button : ui.getButtons()) {
            button.setEnabled(true);
            ui.reasignarAcciones();
            button.addActionListener(e -> {
                try {
                    dos.writeUTF(button.getActionCommand());
                    button.setActionCommand("");
                    disabledGameButtons(ui);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
        }
    }

    // Deshabilita todos los botones del juego para evitar más interacciones
    private static void disabledGameButtons(PiedraPapelTijeraUI ui) {
        for (Component component : ui.getMainPanel().getComponents()) {
            if (component instanceof CircularImageButton button) {
                button.setEnabled(false);
            }
        }
    }

    private static PiedraPapelTijeraUI generateUI() {
        JFrame frame = new JFrame("PIEDRA PAPEL TIJERA ONLINE");

        try {
            Font customFont = Font.createFont(Font.TRUETYPE_FONT, new File("src/resources/Pixar.ttf")).deriveFont(30f);
            applyCustomFont(customFont);
        } catch (FontFormatException | IOException e) {
            throw new RuntimeException(e);
        }

        ImageIcon icon = new ImageIcon("src/resources/icono.png");
        frame.setIconImage(icon.getImage());

        PiedraPapelTijeraUI ui = new PiedraPapelTijeraUI();
        ui.createUIComponents();
        ui.generateUI();

        frame.setContentPane(ui.getMainPanel());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);

        return ui;
    }

    private static void applyCustomFont(Font font) {
        UIManager.put("Label.font", font);
        UIManager.put("Button.font", font);
        UIManager.put("TextField.font", font);
        UIManager.put("OptionPane.font", font);
        UIManager.put("Panel.font", font);
    }
}