package bb.develops.solutec;

import javax.microedition.io.*;
import java.io.*; 

//import javax.microedition.location.*;

/**
 * This class represents the client in our client/server configuration.
 */
public final class UdpClient extends Thread
{    
    private String _msg;
    private String datagram;
    private UDPDatagramConnection _conn;  
    
    

    // Constructor
    /**
     * Creates a new UdpClient.
     * @param msg The message sent to the server.
     */
    public UdpClient(String msg, String dtrg)
    {
        _msg = msg;               
        datagram = dtrg;
     }
    
    /**
     * Implementation of Thread
     */
    public void run()
    {
    	
    
			
			
    	
		
        try
        {
            // Make a UDP(datagram) connection to the local loopback address
            // on our machine.  We specify 2000 (the port our server listens on)
            // as the destination port and specify 3000 as the
            // source port.
            _conn = (UDPDatagramConnection)Connector.open(datagram);           
            
            
            
            // Convert the message to a byte array for sending.
            byte [ ] bufOut = _msg.getBytes();
            
            // Create a datagram and send it across the connection.
           
            Datagram outDatagram = _conn.newDatagram(bufOut, bufOut.length);
            //_msg = "LA GLORIA SOLO ES DEL SEÑOR JESÚS";
            
            try{
            	if(outDatagram!=null){
            		_conn.send(outDatagram);
            	}
            }
            catch(NullPointerException ne){
           
            }
                                  
        }
        catch(IOException ioe)
        {
           
           
                     
        }
        finally
        {
            try
            {
                // Close the connection
                _conn.close();  
            }
            catch(IOException ioe)
            {    
            	
                
            }            
        }
        
    }        
}
