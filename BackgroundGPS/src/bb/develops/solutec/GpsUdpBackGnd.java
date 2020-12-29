package bb.develops.solutec;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.io.IOUtilities;

import javax.microedition.location.Location;
import javax.microedition.location.LocationListener;
import javax.microedition.location.LocationException;
import javax.microedition.location.LocationProvider;
import javax.microedition.io.Connector;
import javax.microedition.io.file.*;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;






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
    	
    	    String initInfo = readConfiguration();
    		
    	
    	    String [] configInfo = split(initInfo, "!");
    		if(configInfo[5].equals("start")){
    		
       		id = configInfo[0];
    	    ipAdd = configInfo[1];
    		port = configInfo[2];
    		sendInterval = Integer.valueOf(configInfo[3]).intValue();
    		gpsInterval = Integer.valueOf(configInfo[4]).intValue();
    		_messageString = new StringBuffer();
    		_datagram = "datagram://" + ipAdd + ':' + port;
    		startLocationUpdate();
    		
    		}
    	
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
	 
	    		 // Pilas con esto quiero moverlo a la clase
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
	             	 _messageString.append(round(latitude,4));
	                 _messageString.append('/');
	                 _messageString.append(round(longitude,4));
	                 _messageString.append('/');
	                 _messageString.append(round(altitude,1));
	                 _messageString.append('/');
	                 _messageString.append(round(speed,1));
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
	
 
	 private  String readConfiguration() {
		    String fName = "file:///store/home/user/archivoInicio.txt";
	        String result = null;
	        FileConnection fconn = null;
	        DataInputStream is = null;
	        try {
	                fconn = (FileConnection) Connector.open(fName);
	                is = fconn.openDataInputStream();
	                byte[] data = IOUtilities.streamToBytes(is);
	                result = new String(data);
	            } 
	        catch (IOException e) {
	                System.out.println(e.getMessage());
	        } finally {
	                try {
	                        if (null != is)
	                                is.close();
	                        if (null != fconn)
	                                fconn.close();
	                } catch (IOException e) {
	                        System.out.println(e.getMessage());
	                }
	        }
	        return result;
	}
	 
	

	 
	 private  String[] split(String strString, String strDelimiter) { 
		 String[] strArray;
		 int iOccurrences = 0;
		 int iIndexOfInnerString = 0;
		 int iIndexOfDelimiter = 0;
		 int iCounter = 0;
		 //Check for null input strings.
		 if (strString == null) {
			 throw new IllegalArgumentException("Input string cannot be null.");
			 }
		 //Check for null or empty delimiter strings.
		 if (strDelimiter.length() <= 0 || strDelimiter == null) {
			 throw new IllegalArgumentException("Delimeter cannot be null or empty.");
			 }
		 //strString must be in this format: (without {} )    
		 //"{str[0]}{delimiter}str[1]}{delimiter} ...
		 // {str[n-1]}{delimiter}{str[n]}{delimiter}"
		 //If strString begins with delimiter then remove it in order
		 //to comply with the desired format.
		 if (strString.startsWith(strDelimiter)) {
			 strString = strString.substring(strDelimiter.length());
			 }
		 //If strString does not end with the delimiter then add it    
		 //to the string in order to comply with the desired format.    
		 if (!strString.endsWith(strDelimiter)) {
			 strString += strDelimiter;
			 }
		 //Count occurrences of the delimiter in the string.
		 //Occurrences should be the same amount of inner strings.
		 while((iIndexOfDelimiter = strString.indexOf(strDelimiter,iIndexOfInnerString)) != -1) {
			 iOccurrences += 1;
			 iIndexOfInnerString = iIndexOfDelimiter+strDelimiter.length();
			 }
		 //Declare the array with the correct size.
		 strArray = new String[iOccurrences];
		 //Reset the indices.
		 iIndexOfInnerString = 0;
		 iIndexOfDelimiter = 0;
		 //Walk across the string again and this time add the
		 //strings to the array.
		 while((iIndexOfDelimiter = strString.indexOf(strDelimiter,iIndexOfInnerString)) != -1) {
			 //Add string to array.
			 strArray[iCounter] = strString.substring(iIndexOfInnerString,iIndexOfDelimiter);
			 //Increment the index to the next character after
			 //the next delimiter.
			 iIndexOfInnerString = iIndexOfDelimiter + strDelimiter.length();
			 //Inc the counter.
			 iCounter += 1;
			 }
		 return strArray;
		 }
	 
}


