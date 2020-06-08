package ServerPackage;

import java.awt.BorderLayout;
import java.awt.event.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.JFrame;

public class Server extends JFrame {
    private JTextField userText;
    private JTextArea chatWindow;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private ServerSocket server;
    private Socket connection;

    public Server() {
        super("Server Computer");
        userText = new JTextField();
        chatWindow = new JTextArea();
        userText.setEditable(false);
        userText.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage(e.getActionCommand());
                userText.setText("");
            }
        });
        add(userText, BorderLayout.NORTH);
        add(new JScrollPane(chatWindow));
        setSize(300, 300);
        setVisible(true);
    }

    public void startRunning() {
        try {
            server = new ServerSocket(6789,100);
            while (true) {
                try {
                    waitForConnection();
                    setUpStream();
                    WhileChatting();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    closeObject();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void closeObject() {
        sendMessage("Close Connection\n");
        ableToType(false);
        try {
            output.close();
            input.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void WhileChatting() {
        String message = "Now Connected";
        ableToType(true);
        do {
            try {
                message = (String)input.readObject();
                showMessage("\n" + message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } while (!message.equals("CLIENT-END"));
    }

    private void ableToType(boolean b) {
        SwingUtilities.invokeLater(new Runnable(){
        
            @Override
            public void run() {
                userText.setEditable(b);
            }
        });
    }

    public void sendMessage(String str) {
        try {
            output.writeObject("SERVER :" + str);
            output.flush();
            showMessage("\nServer :" + str);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showMessage(String txt) {
        SwingUtilities.invokeLater(new Runnable(){
        
            @Override
            public void run() {
                chatWindow.append(txt);
            }
        });
    }

    private void waitForConnection() {
        try {
            sendMessage("รอการเชื่อมต่อจาก Client....\n");
            connection = server.accept();
            sendMessage("มีการเชื่อมต่อแล้ว :" + connection.getInetAddress().getHostAddress());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setUpStream() {
        try {
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}