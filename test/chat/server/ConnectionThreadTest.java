/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat.server;
import com.sun.corba.se.spi.activation.Server;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import org.mockito.Mockito.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author amit
 */
public class ConnectionThreadTest {
    
    public ConnectionThreadTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getIsStopped method, of class ConnectionThread.
     */
    @Test
    public void testGetIsStopped() {
        System.out.println("getIsStopped");
        ConnectionThread instance = null;
        Boolean expResult = null;
        Boolean result = instance.getIsStopped();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setIsStopped method, of class ConnectionThread.
     */
    @Test
    public void testSetIsStopped() {
        System.out.println("setIsStopped");
        Boolean isStopped = null;
        ConnectionThread instance = null;
        instance.safeStop();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of run method, of class ConnectionThread.
     * @throws java.io.IOException
     */
    @Test
    public void testRun() throws IOException {
        System.out.println("run");
        ListenerThread lt = new ListenerThread(5023);
        lt.start();
        Socket s;
        InetAddress addr = InetAddress.getByName("127.0.0.1"); 
        s = new Socket(addr, 5013);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
