package org.educa.ut3.chat.compartidos.ui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class ChatUI {
    private JPanel mainPanel;
    private JLabel recipientLabel;
    private BackgroundTextArea messageArea;
    private JTextField inputField;
    private JButton sendButton;

    public void createUIComponents() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel topBar = new JPanel();
        topBar.setBackground(new Color(24, 24, 42));
        topBar.setLayout(new BorderLayout());

        topBar.setPreferredSize(new Dimension(0, 80));
        topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.WHITE));

        recipientLabel = new JLabel("DESTINATARIO", SwingConstants.LEFT);
        recipientLabel.setForeground(Color.WHITE);
        recipientLabel.setBorder(BorderFactory.createEmptyBorder(0, 50, 0, 0));

        recipientLabel.setFont(new Font("Arial", Font.BOLD, 24));

        topBar.add(recipientLabel, BorderLayout.CENTER);

        messageArea = new BackgroundTextArea();
        messageArea.setEditable(false);
        messageArea.setBackground(new Color(37, 40, 80));
        messageArea.setForeground(Color.WHITE);
        messageArea.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        messageArea.setFont(new Font("Arial", Font.PLAIN, 20));
        messageArea.setOpaque(true);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);

        messageArea.setBackgroundImage("src/resources/fondo.png");

        JScrollPane scrollPane = new JScrollPane(messageArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());

        inputField = new JTextField();
        inputField.setPreferredSize(new Dimension(400, 80));
        inputField.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 20));
        Border topBorder = BorderFactory.createMatteBorder(5, 0, 0, 0, Color.BLACK);
        Border leftBorder = BorderFactory.createMatteBorder(0, 50, 0, 0, Color.WHITE);
        Border compoundBorder = BorderFactory.createCompoundBorder(topBorder, leftBorder);
        inputField.setBorder(compoundBorder);

        sendButton = new JButton("ENVIAR");
        sendButton.setPreferredSize(new Dimension(200, 80));
        sendButton.setBorder(BorderFactory.createMatteBorder(5, 5, 0, 0, Color.BLACK));
        sendButton.setBackground(new Color(24, 24, 42));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFont(new Font("Arial", Font.BOLD, 20));

        bottomPanel.add(inputField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);

        mainPanel.add(topBar, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public JLabel getRecipientLabel() {
        return recipientLabel;
    }

    public JTextField getInputField() {
        return inputField;
    }

    public JTextArea getMessageArea() {
        return messageArea;
    }

    public JButton getSendButton() {
        return sendButton;
    }
}