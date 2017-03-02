/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat.server;

import java.net.InetAddress;
import java.util.Arrays;

/**
 *
 * @author James&Amit
 */

public class Message2 
{

    @Override
    public String toString() {
        return "Message2{" + "Sender=" + Sender + ", clientList=" + Arrays.toString(clientList) + ", massage=" + massage + ", type=" + type + '}';
    }
    private String Sender ;
    private String[] clientList ;
    private String massage ;
    private MessageType type ;

    public Message2(String Sender, String[] clientList, String massage, MessageType type) {
        this.Sender = Sender;
        this.clientList = clientList;
        this.massage = massage;
        this.type = type;
    }
      public Message2(String Sender, String[] clientList, MessageType type) {
        this.Sender = Sender;
        this.clientList = clientList;
        this.massage = null;
        this.type = type;
    }

    public String getSender() {
        return Sender;
    }

    public void setSender(String Sender) {
        this.Sender = Sender;
    }

    public String[] getClientList() {
        return clientList;
    }

    public void setClientList(String[] clientList) {
        this.clientList = clientList;
    }

    public String getMassage() {
        return massage;
    }

    public void setMassage(String massage) {
        this.massage = massage;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }
    
    
}
