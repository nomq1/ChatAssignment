/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author amit
 */
public class ListenerThread2 extends Thread
{
    private int port;
    private ServerSocket serverSocket = null ;
    private ConcurrentHashMap<String,Client> clients ;
    private Boolean isStopped = false ;
    public void safeStop()
    {
        isStopped = true;
    }
    public ListenerThread2(int port) 
    {
        try {
            this.port = port;
            this.serverSocket = new ServerSocket(port);
        } catch (IOException ex) {
            Logger.getLogger(ListenerThread2.class.getName()).log(Level.SEVERE, null, ex);
            isStopped= true;
        }
    }
    @Override
    public void run()
    {
        while(!isStopped)
        {
            try {
                Socket s = getConnection();
                addConnectionThread(s);
            } catch (IOException ex) {
                Logger.getLogger(ListenerThread2.class.getName()).log(Level.SEVERE, "connecttion creation failed");
            }
        }
        closeServer();
        
    }

    private Socket getConnection() throws IOException 
    {
        return serverSocket.accept();
    }

    private void addConnectionThread(Socket s) 
    {
        ConnectionThread2 conctioT = new ConnectionThread2(s, clients);
        conctioT.start();
    }

    private void closeServer() {
        try {
            serverSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(ListenerThread2.class.getName()).log(Level.SEVERE, "failed to close Server", ex);
        }
    }
    
    
}
