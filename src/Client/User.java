/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author james&amit
 */
public class User extends Thread
{
    private Socket s = null ;
    private String name = null ;
    private Boolean isStopped = false ;
    private ConcurrentLinkedQueue<Message2> messagesOut ;
    private ConcurrentLinkedQueue<Message2> messagesIn ;
    private Boolean isConnected = false ;

    public void addMessage (Message2 m)
    {
        messagesOut.add(m);
    }
    public Boolean isConnected() {
        return isConnected;
    }
    ClientGUI gui;
    public void safeStop()
    {
        isStopped = true;
        try {
            s.close();
        } catch (IOException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public User(String Host , int port , ClientGUI gui) throws IOException 
    {
        s = new Socket(Host, port);
        this.gui = gui;
        isConnected = true;
    }

    @Override
    public void run() {
        try{
        ObjectInputStream in = new ObjectInputStream(s.getInputStream());
        ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
        while (!isStopped) { 
            
            handleMessage((Message2) in.readObject());
            if(!messagesOut.isEmpty())
            {
                if (messagesOut.peek().getType()==MessageType.DISCONNECT)
                {
                    out.writeObject(messagesOut.poll());
                    in.close();
                    out.close();
                    safeStop();
                }
                out.writeObject(messagesOut.poll());
            }
            
        }
        } catch (IOException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, "failed to send and recive data from server", ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    private void handleMessage(Message2 message2) {
        switch(message2.getType())
        {
            case DISCONNECTED:
                userDisconnected(message2);
                break;
            case MESSAGEFROM:
                addmessage(message2);
                break;
            case CONNECTED:
                addToClientList(message2);
                break;
            case DISCONNECT:
                diconnect();
                break;
            case CLIENTLIST:
                updateClientList(message2);
                break;
            case ACK:
                setOnline();
                updateClientList(message2);
                break;
            default:
                break;
        }
    }

    private void userDisconnected(Message2 message2) {
        gui.removeFromUserList(message2.getClientList());
        String message = message2.getClientList()[0] + " disconnected ";
        gui.appendToMessagesArea(message);
    }

    private void addmessage(Message2 message2) {
        gui.appendToMessagesArea(message2.getSender() + " : "+ message2.getMassage());
    }

    private void addToClientList(Message2 message2) {
        gui.addToUserList(message2.getClientList());
        String message = message2.getClientList()[0] + " connected ";
    }

    private void diconnect() {
        gui.disconnect();
        messagesIn.clear();
        messagesOut.clear();
        safeStop();
    }

    private void updateClientList(Message2 message2) {
        gui.updateClientList(message2.getClientList());
    }

    private void setOnline() {
        gui.setOnline();
    }
    
    
    
    
   
    
    
    
}
