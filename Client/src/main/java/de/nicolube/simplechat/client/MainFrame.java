package de.nicolube.simplechat.client;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    @Getter
    private final MainPanel mainPanel;
    private final LoginPanel loginPanel;

    public MainFrame(Client client) throws HeadlessException {
        this.setMinimumSize(new Dimension(300, 200));
        this.mainPanel = new MainPanel(client);
        this.loginPanel = new LoginPanel(client);
        this.getContentPane().add(this.loginPanel.getMainPanel());
        this.getRootPane().setDefaultButton(this.loginPanel.getLoginButton());
        this.setVisible(true);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public void main() {
        this.getContentPane().removeAll();
        this.getContentPane().add(this.mainPanel.getMainPanel());
        this.getRootPane().setDefaultButton(this.mainPanel.getSendButton());
        this.getRootPane().updateUI();
    }
}
