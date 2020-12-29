package bb.develops.solutec;

import net.rim.device.api.system.Application;

import javax.microedition.location.Location;
import javax.microedition.location.LocationListener;
import javax.microedition.location.LocationException;
import javax.microedition.location.LocationProvider;

import net.rim.device.api.ui.component.Dialog;


public class GpsUdpBackGnd extends Application {
    /**
     * Entry point for application
     * @param args Command line arguments (not used)
     */ 
	
	private LocationProvider _locationProvider;
	private String _datagram;
	private StringBuffer _messageString;
	private String id;
	private String ipAdd;
	private String port;
	private static int sendInterval;
	private static int gpsInterval;
	
    public static void main(String[] args){
    	GpsUdpBackGnd backGnd = new GpsUdpBackGnd();
    	backGnd.enterEventDispatcher();
    }
    
    public GpsUdpBackGnd(){
    	//configurationInfo cnf = new configurationInfo ("0001","190.251.233.172","4003",10,1);
    	id = "0001";
    	ipAdd = "190.251.173.250";
    	port= "4003";
    	sendInterval = 10;
    	gpsInterval = 1;
    	_messageString = new StringBuffer();
    	_datagram = "datagram://" + ipAdd + ':' + port;
    	startLocationUpdate();    	
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
                
                //invokeLater( showGpsUnsupportedDialog );  // Ask event-dispatcher thread to display dialog ASAP.
            }
            else
            {
                // Only a single listener can be associated with a provider, and unsetting it 
                // involves the same call but with null, therefore, no need to cache the listener
                // instance request an update every second.
                _locationProvider.setLocationListener(new LocationListenerImpl(), gpsInterval, 1, 1);
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
    
    private class LocationListenerImpl implements LocationListener
	 {
	     // Members ----------------------------------------------------------------------------------------------
	     
	     private int sendCount;
	     
	     
	 	    // We record a location every 5 seconds.
	 	  
  


    
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
	           
	             
	             
	             
	             sendCount += gpsInterval;
	            

	             // If we're mod zero then it's time to record this data.
	             
	             
	             
	             sendCount %= sendInterval;
	             
	             
	             if ( sendCount == 0 )
	             {
	                 // Minimize garbage creation by appending only character primitives, no extra String objects created that way.
	            	
	            	
	            	 _messageString.append(id);
	                 _messageString.append('/');
	             	 _messageString.append(round(longitude,4));
	                 _messageString.append('/');
	                 _messageString.append(round(latitude,4));
	                 _messageString.append('/');
	                 _messageString.append(round(altitude,2));
	                 _messageString.append('/');
	                 _messageString.append(round(speed,2));
	                 _messageString.append('/');
	                 _messageString.append('!');
	                
	                            	 
	                  //Locator.this.updateLS(Integer.toString(sendCount));
				         	                	 
	                	 synchronized(this)
	                	 {
	                       	UdpClient udp = new UdpClient(_messageString.toString(),_datagram);
	                     	udp.start();
	                         _messageString.setLength(0); 
	                                      	                                        
	                	 }
	                       
	                 
	             }
	                                  

	         }
	         
	     }

	     public void providerStateChanged(LocationProvider provider, int newState)
	     {
	         // Not implemented.
	     }        
	     
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
	 

}
