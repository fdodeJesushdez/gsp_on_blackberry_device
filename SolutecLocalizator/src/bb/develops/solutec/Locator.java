package bb.develops.solutec;

import javax.microedition.location.Location;
import javax.microedition.location.LocationException;
import javax.microedition.location.LocationListener;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.ui.component.*;

import javax.microedition.location.LocationProvider;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;

public class Locator extends UiApplication {
		
	
	private LocationProvider _locationProvider; 
	private static int _interval = 1;
	private static int captureInterval;
	private static final int CPT_INTERVAL = 5;
	
	private LabelField title;
	private VerticalFieldManager firstManager;
	private VerticalFieldManager secondManager;
	private VerticalFieldManager thirdManager;
	private VerticalFieldManager fourthManager;
	private VerticalFieldManager fifthManager;
	private HorizontalFieldManager bottonManager;
	private LabelField idMsg;
	private static EditField idNumber;
	private LabelField ipMsg;
	private EditField ipAddress;
	private LabelField portMsg;
	private EditField portNumber;
	private LabelField timeMsg;
	private EditField timeInterval;
	private ButtonField okButton;
	private BitmapField solutecImg;
	private Bitmap logo1;
	
	private StringBuffer _messageString;
	
	private String _datagram;
	private String ipAdd;
	private String ipAux;
	private String portAux;
	private String port;
	private String timeAux;
	private String time;
	private int lastIndex;

	
	
	
	
	
	
	public static void main(String[] args) {
	Locator loc = new Locator();
	loc.enterEventDispatcher();
	}
	public Locator() {
	
	_messageString = new StringBuffer();
	LocatorScreen screen = new LocatorScreen();
    
	pushScreen(screen);
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
	                // Only a single listener can be associated with a provider, and unsetting it 
	                // involves the same call but with null, therefore, no need to cache the listener
	                // instance request an update every second.
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
	 
	 private class LocationListenerImpl implements LocationListener
	 {
	     // Members ----------------------------------------------------------------------------------------------
	     private int captureCount;
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
	           
	             
	             
	             captureCount += _interval; //_interval
	             sendCount += _interval;
	            

	             // If we're mod zero then it's time to record this data.
	             
	             
	             
	             captureCount %= captureInterval;
	             
	             
	             if ( captureCount == 0 )
	             {
	                 // Minimize garbage creation by appending only character primitives, no extra String objects created that way.
	            	
	            	
	            	 _messageString.append(idNumber.getText());
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
	 

	 private final class LocatorScreen extends MainScreen {
			
			
			
			public LocatorScreen(){
				super();
				
				
				
				
			    title = new LabelField("Solutec Localizator",LabelField.ELLIPSIS | LabelField.USE_ALL_WIDTH);
				setTitle(title);
				
				 
				firstManager = new VerticalFieldManager();
				secondManager = new VerticalFieldManager();
				thirdManager = new VerticalFieldManager();
				fourthManager = new VerticalFieldManager();
				fifthManager = new VerticalFieldManager();
				bottonManager = new HorizontalFieldManager(Field.FIELD_HCENTER);
				add(firstManager);
				add(new SeparatorField());
				add(secondManager);
				add(new SeparatorField());
				add(thirdManager);
				add(new SeparatorField());
				add(fourthManager);
				add(new SeparatorField());
				add(fifthManager);
				add(new SeparatorField());
				add(bottonManager);
				logo1 = Bitmap.getBitmapResource("logo_only.png");
				solutecImg = new BitmapField();
				solutecImg.setBitmap(logo1);
				idMsg = new LabelField("Ingrese número de identificación");
				idNumber = new EditField("Numero ID:"," ");
				ipMsg = new LabelField("Ingrese dirección IP");
				ipAddress = new EditField("IP Servidor:"," ");
				portMsg = new LabelField("Ingrese número de puerto");
				portNumber = new EditField("Puerto Servidor:"," ");
				timeMsg = new LabelField("Ingrese intervalo de tiempo");
				timeInterval = new EditField("Intervalo de tiempo (Seg):"," ");
								
				FieldChangeListener buttonListener = new FieldChangeListener() {
					public void fieldChanged(Field field, int context) {
						ipAux = ipAddress.toString();
						lastIndex = ipAux.indexOf(' ');
						ipAdd = ipAux.substring(0, lastIndex);
						portAux = portNumber.toString();
						lastIndex = portAux.indexOf(' ');
						port = portAux.substring(0, lastIndex);
						timeAux = timeInterval.toString();
						lastIndex = timeAux.indexOf(' ');
						time = timeAux.substring(0, lastIndex);
						captureInterval = Integer.valueOf(time).intValue();
						_datagram = "datagram://" + ipAdd + ':' + port;
						startLocationUpdate();
						}
				};	
				
				okButton = new ButtonField("  Ok  ");
				okButton.setChangeListener(buttonListener);
				
				firstManager.add(solutecImg);
				secondManager.add(idMsg);
				secondManager.add(idNumber);
				thirdManager.add(ipMsg);
				thirdManager.add(ipAddress);
				fourthManager.add(portMsg);
				fourthManager.add(portNumber);
				fifthManager.add(timeMsg);
				fifthManager.add(timeInterval);
				bottonManager.add(okButton);
			}
						     
			
			
			
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
	 


	 
	 private void updateLS(final String msg)
	    {
	        invokeLater(new Runnable()
	        {
	            public void run()
	            {
	                idNumber.setText(msg);
	                
	            }
	        });
	    }
	
}
