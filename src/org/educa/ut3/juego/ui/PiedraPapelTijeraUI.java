package org.educa.ut3.juego.ui;

import javax.swing.*;
import java.awt.*;

public class PiedraPapelTijeraUI {
    private JPanel mainPanel;
    private JLabel statusLabel;
    private JLabel scoreLabel;
    private JLabel playersLabel;
    private JLabel salaLabel;
    private final CircularImageButton[] buttons = new CircularImageButton[3];
    private String playerName;
    private String oponentName;
    private String sala;
    private int contador;

    public void createUIComponents() {
        mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(87, 35, 100));
                g.fillRect(0, 0, getWidth(), getHeight());
                ImageIcon background = new ImageIcon("src/resources/pixar.png");
                Image img = background.getImage();
                g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
            }
        };
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(100, 0, 100, 0));
    }

    public void generateUI() {
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBackground(new Color(87, 35, 100));
        topPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 20, 0);

        playersLabel = new JLabel(playerName + " VS " + oponentName);
        playersLabel.setHorizontalAlignment(SwingConstants.CENTER);
        playersLabel.setForeground(Color.WHITE);
        topPanel.add(playersLabel, gbc);

        scoreLabel = new JLabel("RONDAS GANADAS: " + contador);
        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
        scoreLabel.setForeground(Color.WHITE);
        topPanel.add(scoreLabel, gbc);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        createGameButtons();

        JPanel bottomPanel = new JPanel(new GridBagLayout());
        bottomPanel.setBackground(new Color(87, 35, 100));
        bottomPanel.setOpaque(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 0, 0, 0);

        statusLabel = new JLabel("ESPERANDO A QUE SE CONECTE EL RIVAL...");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setForeground(Color.WHITE);
        bottomPanel.add(statusLabel, gbc);

        salaLabel = new JLabel("SALA - " + sala);
        salaLabel.setHorizontalAlignment(SwingConstants.CENTER);
        salaLabel.setForeground(Color.WHITE);
        bottomPanel.add(salaLabel, gbc);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
    }

    public void askForName() {
        while (this.playerName == null || this.playerName.isBlank()) {
            this.playerName = JOptionPane.showInputDialog("INGRESA TU NOMBRE:");
            if (this.playerName == null) {
                int confirm = JOptionPane.showConfirmDialog(null, "¿SEGURO QUE QUIERES SALIR?", "CONFIRMAR SALIDA", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            } else if (this.playerName.isBlank()) {
                JOptionPane.showMessageDialog(null, "NOMBRE INVALIDO, INTENTELO DE NUEVO", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        this.contador = 0;
        this.oponentName = "***";
        getPlayersLabel().setText(playerName + " VS " + oponentName);
    }

    public void askForCode() {
        while (this.sala == null || this.sala.isBlank()) {
            this.sala = JOptionPane.showInputDialog("INGRESA EL CODIGO DE SALA:");
            if (this.sala == null) {
                int confirm = JOptionPane.showConfirmDialog(null, "¿SEGURO QUE QUIERES SALIR?", "CONFIRMAR SALIDA", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            } else if (this.sala.isBlank()) {
                JOptionPane.showMessageDialog(null, "CODIGO INVALIDO, INTENTELO DE NUEVO", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        salaLabel.setText("SALA - " + sala);
    }

    private void createGameButtons() {
        String[] options = {"Piedra", "Papel", "Tijera"};
        String[] imagePaths = {"src/resources/piedra.png", "src/resources/papel.png", "src/resources/tijeras.png"};

        JPanel buttonContainer = new JPanel();
        buttonContainer.setLayout(new BoxLayout(buttonContainer, BoxLayout.X_AXIS));
        buttonContainer.setBackground(new Color(87, 35, 100));
        buttonContainer.setOpaque(false);

        for (int i = 0; i < options.length; i++) {
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new BorderLayout());
            buttonPanel.setBackground(new Color(87, 35, 100));
            buttonPanel.setOpaque(false);
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 50, 0, 50));

            JLabel label = new JLabel(options[i]);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setForeground(Color.WHITE);
            label.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
            buttonPanel.add(label, BorderLayout.NORTH);

            CircularImageButton button = new CircularImageButton(imagePaths[i]);
            button.setEnabled(false);
            button.setActionCommand(options[i]);
            buttonPanel.add(button, BorderLayout.CENTER);
            buttons[i] = button;

            buttonContainer.add(buttonPanel);
        }

        mainPanel.add(buttonContainer, BorderLayout.CENTER);
    }

    public void reasignarAcciones() {
        int i = 0;
        String[] options = {"Piedra", "Papel", "Tijera"};
        for (CircularImageButton button : buttons) {
            button.setActionCommand(options[i++]);
        }
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public void setOponentName(String oponentName) {
        this.oponentName = oponentName;
    }

    public String getPlayerName() {
        return this.playerName;
    }

    public String getSala() {
        return sala;
    }

    public JLabel getStatusLabel() {
        return statusLabel;
    }

    public JLabel getPlayersLabel() {
        return playersLabel;
    }

    public int getContador() {
        return contador;
    }

    public JLabel getScoreLabel() {
        return scoreLabel;
    }

    public String getOponentName() {
        return oponentName;
    }

    public void setContador(int contador) {
        this.contador = contador;
    }

    public CircularImageButton[] getButtons() {
        return buttons;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void setSala(String sala) {
        this.sala = sala;
    }
}