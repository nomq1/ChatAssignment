/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat.server;

import java.net.InetAddress;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author amit
 */
public class Client 
{
    ConcurrentLinkedQueue<Message2> messages ;
    String name ;
    InetAddress anetAddr ;

    public Message2 getMessage2() 
    {
        return messages.poll();
    }

    public void addMessage2(Message2 m) {
        this.messages.add(m);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public InetAddress getAnetAddr() {
        return anetAddr;
    }

    public void setAnetAddr(InetAddress anetAddr) {
        this.anetAddr = anetAddr;
    }

    public Client(Queue<Message2> messages, String name, InetAddress anetAddr) {
        this.messages = new ConcurrentLinkedQueue<>();
        this.name = name;
        this.anetAddr = anetAddr;
        
    }

    public Client(String name, InetAddress anetAddr)
    {
        this.name = name;
        this.anetAddr = anetAddr;  
        this.messages = new ConcurrentLinkedQueue<>();
    }
    
}
