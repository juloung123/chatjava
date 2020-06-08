package clientPackage;

import javax.swing.JFrame;

public class clientTest{
    public static void main(String[] args){
        client clie=new client("localhost");
        clie.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        clie.startRunning();
    }
}