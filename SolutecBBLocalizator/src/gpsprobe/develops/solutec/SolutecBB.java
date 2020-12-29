package gpsprobe.develops.solutec;

import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;

//import net.rim.device.api.system.*;
import javax.microedition.io.*;

import java.util.*;
import java.io.*;

import javax.microedition.location.*;



import net.rim.device.api.util.*;


class SolutecBB extends UiApplication 
{
    
    
        
    private static final int CAPTURE_INTERVAL=60;    // We record a location every 5 seconds.
    


    
    
   private static int _interval = 1;   // Seconds - this is the period of position query.
          
   private String datagram = "datagram://190.251.173.250:4004";
         
    
    private EditField _status;    
    private StringBuffer _messageString;
        
    private LocationProvider _locationProvider; 
    
  

    /**
     * Instantiate the new application object and enter the event loop.
     * @param args No args are supported for this application.
     */
    public static void main(String[] args)
    {
        new SolutecBB().enterEventDispatcher();
    }

    // Constructor 
    private SolutecBB()
    {
       
        
        
        _messageString = new StringBuffer();
        
        SolutecBBScreen screen = new SolutecBBScreen();
        screen.setTitle(new LabelField("Localizador Solutec" , LabelField.USE_ALL_WIDTH));
        
        _status = new EditField(Field.NON_FOCUSABLE);
        screen.add(_status);
        
        //System.out.println("Constructor Constructor Constructor Constructor Constructor Constructor Constructor Constructor ");
        // Try to start the GPS thread that listens for updates.
        startLocationUpdate();
        
            // If successful, start the thread that communicates with the server.
        
        

        pushScreen(screen);
    }
    
   
    /**
     * Update the GUI with the data just received.
     */
    private void updateLS(final String msg)
    {
        invokeLater(new Runnable()
        {
            public void run()
            {
                _status.setText(msg);
            }
        });
    }

    
    private boolean startLocationUpdate()
    {
        boolean retval = false;
        
        try
        {
            _locationProvider = LocationProvider.getInstance(null);
            
            if ( _locationProvider == null )
            {
                
                Runnable showGpsUnsupportedDialog = new Runnable() 
                {
                    public void run() {
                        Dialog.alert("GPS is not supported on this platform, exiting...");
                        System.exit( 1 );
                    }
                };
                
                invokeLater( showGpsUnsupportedDialog );  // Ask event-dispatcher thread to display dialog ASAP.
            }
            else
            {
                
                _locationProvider.setLocationListener(new LocationListenerImpl(), _interval, 1, 1);
                retval = true;
            }
        }
        catch (LocationException le)
        {
            System.err.println("Failed to instantiate the LocationProvider object, exiting...");
            System.err.println(le); 
            System.exit(0);
        }        
        return retval;
    }
    
        
        private static double round(double d, int decimal) 
    {
        double powerOfTen = 1;
        
        while (decimal-- > 0)
        {
            powerOfTen *= 10.0;
        }
        
        double d1 = d * powerOfTen;
        int d1asint = (int)d1; // Clip the decimal portion away and cache the cast, this is a costly transformation.
        double d2 = d1 - d1asint; // Get the remainder of the double.
        
        // Is the remainder > 0.5? if so, round up, otherwise round down (lump in .5 with > case for simplicity).
        return ( d2 >= 0.5 ? (d1asint + 1)/powerOfTen : (d1asint)/powerOfTen);
    }

    /**
     * Implementation of the LocationListener interface.
     */
    private class LocationListenerImpl implements LocationListener
    {
        // Members ----------------------------------------------------------------------------------------------
        private int captureCount;
        
        
        // Methods ----------------------------------------------------------------------------------------------
        /**
         * @see javax.microedition.location.LocationListener#locationUpdated(LocationProvider,Location)
         */
        public void locationUpdated(LocationProvider provider, Location location)
        {
            if(location.isValid())
            {
                
                double longitude = location.getQualifiedCoordinates().getLongitude();
                double latitude = location.getQualifiedCoordinates().getLatitude();
                float altitude = location.getQualifiedCoordinates().getAltitude();
                float speed = location.getSpeed();   
                String id = "7785";
               

                //You must be aware of replace this sentence
                captureCount += _interval;
                
                
                // If we're mod zero then it's time to record this data.
                captureCount %= CAPTURE_INTERVAL;
                
                if ( captureCount == 0 )
                {
                    // Minimize garbage creation by appending only character primitives, no extra String objects created that way.
                    _messageString.append(id);
                    _messageString.append('/');
                	_messageString.append(round(latitude,4));
                    _messageString.append('/');
                    _messageString.append(round(longitude,4));
                    _messageString.append('/');
                    _messageString.append(round(altitude,2));
                    _messageString.append('/');
                    _messageString.append(round(speed,2));
                    _messageString.append('/');
                    _messageString.append('!');
                    synchronized(this)
                    {
                       
                       
                            //_serverConnectThread.sendUpdate(_messageString.toString());
                        	UdpClient udp = new UdpClient(_messageString.toString(),datagram);
                        	udp.start();
                            _messageString.setLength(0); 
                            System.out.println("Paquete UDP enviado");
                                           
                    }                   
                     
                                    
                    
                }
                
                                
                StringBuffer sb = new StringBuffer();
                sb.append("Longitud: ");
                sb.append(longitude);
                sb.append("\n");
                sb.append("Latitud: ");
                sb.append(latitude);
                sb.append("\n");
                sb.append("Altura SNM: ");
                sb.append(altitude);
                sb.append(" m");
                sb.append("\n");
                sb.append("Velocidad : ");
                sb.append(speed);
                sb.append(" m/s");
                sb.append("\n");
                SolutecBB.this.updateLS(sb.toString());
            }
        }
  
        public void providerStateChanged(LocationProvider provider, int newState)
        {
            // Not implemented.
        }        
    }
    
    private final class SolutecBBScreen extends MainScreen
    {
        
        // Constructor
        SolutecBBScreen()
        {
            RichTextField instructions = new RichTextField("Datos GPS:",Field.NON_FOCUSABLE);
            this.add(instructions);
            
           
        }
        
        
        /**
         * @see net.rim.device.api.ui.Screen#close()
         */
        public void close()
        {
            if ( _locationProvider != null ) 
            {
                _locationProvider.reset();
                _locationProvider.setLocationListener(null, -1, -1, -1);
            }
         
            super.close();
        }
    }
     
  
    
}
