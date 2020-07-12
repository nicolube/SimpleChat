package de.nicolube.simplechat.client;

import com.formdev.flatlaf.FlatDarculaLaf;
import lombok.Getter;
import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    @Getter
    private final MainPanel mainPanel;
    @Getter
    private final LoginPanel loginPanel;
    private final Client client;

    @SneakyThrows
    public MainFrame(Client client) throws HeadlessException {
        super("SimpleChat");
        this.client = client;
        UIManager.setLookAndFeel(new FlatDarculaLaf());
        this.setIconImage(ImageIO.read(getClass().getResource("/images/icon.png")));
        this.setMinimumSize(new Dimension(300, 200));
        this.mainPanel = new MainPanel(client);
        this.loginPanel = new LoginPanel(client);
        this.setVisible(true);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
    }

    public void loginPanel() {
        this.client.initChannel();
        this.getContentPane().removeAll();
        this.getContentPane().add(this.loginPanel.getMainPanel());
        this.loginPanel.getNameField().requestFocus();
        this.getRootPane().setDefaultButton(this.loginPanel.getLoginButton());
        pack();
    }
    public void mainPanel() {
        this.getContentPane().removeAll();
        this.mainPanel.getChatListPanel().removeAll();
        this.getContentPane().add(this.mainPanel.getMainPanel());
        this.mainPanel.getChatField().requestFocus();
        this.getRootPane().setDefaultButton(this.mainPanel.getSendButton());
        pack();
    }
}
