/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat.server;

import java.net.InetAddress;

/**
 *
 * @author James&amit
 */
public class Message 
{
    private String Sender ;
    private InetAddress[] clientList ; 
    private String massage ;
    private MessageType type ;
public Message(String Sender, InetAddress[] clientList, String massage, MessageType type) {
        this.Sender = Sender;
        this.clientList = clientList;
        this.massage = massage;
        this.type = type;
    }

    public Message(String Sender, MessageType type , InetAddress receiver) {
        this.Sender = Sender;
        this.type = type;
        this.clientList = new InetAddress [1];
        this.clientList[0]= receiver;
        this.massage = null; 
    }

    public String getSender() {
        return Sender;
    }

    public void setSender(String Sender) {
        this.Sender = Sender;
    }

    public InetAddress[] getClientList() {
        return clientList;
    }

    public void setClientList(InetAddress[] clientList) {
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
