/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author amit
 */
public class ListenerThread extends Thread
{
    //vals 
    private int port;
    private ServerSocket serverSocket = null ;
    public ConcurrentHashMap <InetAddress,String> clientList  ;
    public ConcurrentHashMap <InetAddress,ConcurrentLinkedQueue<Message>> messages  ;
    

   

    //flags
    private boolean isStopped = false ;
     public ConcurrentHashMap<InetAddress, ConcurrentLinkedQueue<Message>> getMessages() {
        return messages;
    }
    public ListenerThread ( int port ) throws IOException
    {
        this.port = port ;
        serverSocket = new ServerSocket(port);
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public ConcurrentHashMap<InetAddress,String>getClientList() {
        return clientList;
    }

    public void setClientList(ConcurrentHashMap<InetAddress,String> clientList) {
        this.clientList = clientList;
    }
    @Override
    public void run ()
    {
        while(!isStopped)
        {
            Socket cSocket = getConnection();
            ConnectionThread cs = new ConnectionThread(cSocket, clientList, messages);//beware from gabage collector
            
            
        }
    }

    private Socket getConnection()
    {
        return serverSocket.accept();
    }
}
