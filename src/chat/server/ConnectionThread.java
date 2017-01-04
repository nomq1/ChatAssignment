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
 * @author james&amit
 */
public class ConnectionThread extends Thread
{
    //flages 
    Boolean isStopped = false;

   
    
    //vars
    Socket cSocket = null ;
    String name ;
    /**
     *  contains client list 
     */
    public ConcurrentHashMap <InetAddress,String> clientList  ;
    ConcurrentLinkedQueue<Message> messagesQ;
    ConcurrentHashMap <InetAddress,ConcurrentLinkedQueue<Message>> messages;
    public Boolean isStopped() {
        return isStopped;
    }
     public void safeStop() {
        this.isStopped = false;
    }

    /**
     *
     * @param cSocket 
     * @param clientList
     * @param messages
     */
    public ConnectionThread(Socket cSocket ,  ConcurrentHashMap <InetAddress,String> clientList ,  ConcurrentHashMap <InetAddress,ConcurrentLinkedQueue<Message>> messages ) 
    {
        this.cSocket = cSocket;
        this.clientList = clientList;
        this.messages = messages;
        messages.putIfAbsent(cSocket.getInetAddress(), new ConcurrentLinkedQueue<Message>()) ;
        this.messagesQ = messages.get(cSocket.getInetAddress());
    }
    @Override
    public synchronized void  run()
    {
        while(!isStopped)
        {
            Message m;
            try {
                m = getMessage();
            
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
                    sendMessageToClient(m);
                    break;
                case DISCONNECT : 
                    disconnect();
                    break;
                default:
                    break;
            }
            } catch (IOException ex) {
                Logger.getLogger(ConnectionThread.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ConnectionThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            sendMessagesInQueue(); // deliver message to all the other connections
        }
        
    }

    private Message getMessage() throws IOException, ClassNotFoundException
    {
        ObjectInputStream in = new ObjectInputStream(cSocket.getInputStream());
        Message mout =  (Message) in.readObject();
        in.close(); // Warning
        return mout;
    }

    private void addClient(Message m ) 
    {
        
        InetAddress newClientInet = cSocket.getInetAddress();
        if (clientList.containsKey(newClientInet))
        {
            ack();
            return;
        }
        clientList.put(newClientInet, m.getSender());
        Message mOut = new Message("Server", (InetAddress[]) clientList.values().toArray(), ""+ clientList.get(newClientInet) + " connected", MessageType.MESSAGEFROM);
        sendMessageToClient(mOut);
        ack();
        name = m.getSender();
    }

    private void ack()
    {
        Message m = new Message("Server", MessageType.ACK , cSocket.getInetAddress());
        sendMessageToClient(m);
    }

    private void returnClientList() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        
    }

    private void sendMessageToClient(Message m) {// sends message to the client of this specific connection
        try {
            ObjectOutputStream oos = new ObjectOutputStream(cSocket.getOutputStream());
            oos.writeObject(m);
            oos.close();
        } catch (IOException ex) {
            Logger.getLogger(ConnectionThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    private void sendMessageToClientList(Message m) 
    {
        m.setType(MessageType.MESSAGEFROM);
        InetAddress[] destClientList = m.getClientList();
        for (int i = 0; i < destClientList.length; i++) {
            InetAddress inetAddress = destClientList[i];
            if (clientList.containsKey(inetAddress)) 
            {
                if (messages.containsKey(inetAddress)) 
                {
                    messages.get(inetAddress).add(m);
                }
                else
                {
                    ConcurrentLinkedQueue<Message> absent;
                    absent = new ConcurrentLinkedQueue<Message>();
                    messages.putIfAbsent(inetAddress, absent); 
                }
            }
        }
    }

    private void sendMessagesInQueue() {
        while (!isStopped && !messagesQ.isEmpty()) 
        {            
            sendMessageToClient(messagesQ.poll());
        }
    }

    private void disconnect() 
    {
        //remove from hashmaps
        clientList.remove(cSocket.getInetAddress());
        messages.remove(cSocket.getInetAddress());
        //make message decliring of client disconnection
        Message mout = new Message("server", null, name + "disconnected", MessageType.DISCONNECTED);
        // send message to all clients ( using java 8 uniqe expression 
        clientList.forEachKey(NORM_PRIORITY, (iNetaddr ) -> { //TODO: chack if its really working
            mout.setClientList( new InetAddress[] {iNetaddr}); 
            sendMessageToClient(mout);
                });
        ack();
        //close connection
        try {
            cSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(ConnectionThread.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    
    
}
