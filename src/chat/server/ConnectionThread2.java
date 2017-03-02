/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author amit
 */
public class ConnectionThread2 extends Thread
{

    private boolean isStopped = false;
    private Socket cSocket;
    private ConcurrentHashMap<String,Client> clients ;
    private int failCount = 0; 
    private String cName;
    public ConnectionThread2(Socket socket , ConcurrentHashMap<String,Client> clients ) 
    {
        this.cSocket = socket;
        this.clients = clients;
    }
    public void safeStop()
    {
        isStopped= true;
    }
    @Override
    public void run()
    {
        while(!isStopped)
        {
            failCount = 0 ;
            try {
                Message2 m;
                m = getMessage2();
                switch (m.getType())
                {
                    case CONNECT :
                        addClient(m);
                        break;
                    case MESSAGEFROM:
                        //server message only
                        break;
                    case ISCONNECTED:
                        ack();
                        break;
                    case CLIENTLIST :
                        returnClientList();
                        break;
                    case SEND :
                        m.setType(MessageType.MESSAGEFROM);
                        sendMessagestoAnotherClient(m);
                        break;
                    case DISCONNECT :
                        disconnect();
                        break;
                    default:
                        break;
                }
                sendMessagesFromAnotherClients(); // deliver message to all the other connections
            } catch (IOException ex) {
                Logger.getLogger(ConnectionThread2.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ConnectionThread2.class.getName()).log(Level.SEVERE, "massage format is invalid", ex);
            }
        }
    }

    private Message2 getMessage2() throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(cSocket.getInputStream());
        Message2 mout =  (Message2) in.readObject();
        in.close(); // Warning
        return mout;
    }

    private void addClient(Message2 m)
    {
        String name = m.getSender();
        InetAddress inetaddr = cSocket.getInetAddress();
        Client c = new Client( name, inetaddr);
        this.cName = name ;
        ack();
        Message2 msg = new Message2("Server",new String[]{ name }, MessageType.CONNECTED);
        clients.forEachValue(NORM_PRIORITY, (client)->client.addMessage2(m));//WARNING 
        clients.put(name, c);
        
    }
    private void ack() {
        Message2 m = new Message2("Server", (String[]) clients.keySet().toArray() ,MessageType.ACK );
        sendMessage2ToClient(m);
    }

    private void returnClientList() 
    {
        Message2 m = new Message2("Server", (String[]) clients.keySet().toArray() ,MessageType.CLIENTLIST );
        sendMessage2ToClient(m);
        
    }

    private void sendMessage2ToClient(Message2 m) 
    {
        try {
            ObjectOutputStream out = new  ObjectOutputStream(cSocket.getOutputStream());
            out.writeObject(m);
            out.close();// WARNING
        } catch (IOException ex) {
            Logger.getLogger(ConnectionThread2.class.getName()).log(Level.SEVERE, null, ex);
            failCount++;
            if (failCount < 5) {
                sendMessage2ToClient(m);
                return;
            }
            Logger.getLogger(ConnectionThread2.class.getName()).log(Level.SEVERE, "connection with client is not stable , closing client connection ");
            disconnect();
            safeStop();
        }
    }
    private void sendMessagesToClient(Message2 m , ObjectOutputStream out) 
    {
        try {
            out.writeObject(m);
        } catch (IOException ex) {
            Logger.getLogger(ConnectionThread2.class.getName()).log(Level.SEVERE, null, ex);
            failCount++;
            if (failCount < 5) {
                sendMessage2ToClient(m);
                return;
            }
            Logger.getLogger(ConnectionThread2.class.getName()).log(Level.SEVERE, "connection with client is not stable , closing client connection ");
            disconnect();
            safeStop();
        }
    }
    private void disconnect() 
    {
        this.clients.remove(this.cName);
        String[] recivers  = (String[]) clients.keySet().toArray() ;
        Message2 m = new Message2("Server", new String[] {cName},MessageType.DISCONNECTED );
        clients.forEachValue(NORM_PRIORITY, (client)->client.addMessage2(m));//WARNING 
        sendMessagestoAnotherClient(m);
        m.setType(MessageType.DISCONNECT);
        sendMessage2ToClient(m);
        ack();
        try {
            cSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(ConnectionThread.class.getName()).log(Level.SEVERE, "failed to disconnect with Client {0}", cName);
        }
        
    }

    private void sendMessagestoAnotherClient( Message2 m)
    {
        String[] recivers = m.getClientList();
        if (clients.size() == recivers.length)
        {
            clients.forEachValue(NORM_PRIORITY, (client)->client.addMessage2(m));//WARNING 
        }
        for (int i = 0; i < recivers.length; i++) 
        {
            Client c = clients.get(recivers[i]);
            c.addMessage2(m);
        }
        
    }

    private void sendMessagesFromAnotherClients() {
        ConcurrentLinkedQueue<Message2> messages = clients.get(cName).messages;
        while (!messages.isEmpty() && !isStopped) {            
            sendMessage2ToClient(messages.poll());
        }
        if (isStopped) 
        {
            messages.clear();
        }
    }
    
}
