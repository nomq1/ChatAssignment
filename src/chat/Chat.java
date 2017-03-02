/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

import chat.server.ListenerThread2;

/**
 *
 * @author amit
 */
public class Chat {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ListenerThread2 l = new ListenerThread2(1231);
        l.start();
    }
    
}
