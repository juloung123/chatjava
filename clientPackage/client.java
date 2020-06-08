package clientPackage;

import java.awt.BorderLayout;
import java.awt.event.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.JFrame;

public class client extends JFrame {
    private JTextField userText;
    private JTextArea chatWindow;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private Socket connection;
    private String serverIP;
    private String message = "";
    public client(String port) {
        super("Client Computer");
        serverIP = port;
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
            while (true) {
                try {
                    ConnectToServer();
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
    private void ConnectToServer(){
        try{
            showMessage("เชื่อมต่อกับเซิฟเวอร์");
            connection = new Socket(InetAddress.getByName("localhost"),6789);
            showMessage("Connect to :" +connection.getInetAddress().getHostName());
        }catch(UnknownHostException ex){
            ex.printStackTrace();
        }catch(IOException ex){
            ex.printStackTrace();
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
            output.writeObject("CLIENT :" + str);
            output.flush();
            showMessage("\n CLIENT :" + str);
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