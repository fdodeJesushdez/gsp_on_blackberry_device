package configurator.bb.develops.solutec;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.UiApplication;

import javax.microedition.io.*;
import javax.microedition.io.file.*;
import java.io.IOException;
import java.io.OutputStream;
import java.io.DataOutputStream;


public class SolutecGpsConfigurator extends UiApplication {

	
	private LabelField title;
	private VerticalFieldManager firstManager;
	private VerticalFieldManager secondManager;
	private VerticalFieldManager thirdManager;
	private VerticalFieldManager fourthManager;
	private VerticalFieldManager fifthManager;
	private VerticalFieldManager sixthManager;
	private VerticalFieldManager seventhManager;
	private HorizontalFieldManager bottonManager;
	private LabelField idMsg;
	private static EditField idNumber;
	private LabelField ipMsg;
	private EditField ipAddress;
	private LabelField portMsg;
	private EditField portNumber;
	private LabelField timeMsg;
	private EditField timeInterval;
	private LabelField gpsMsg;
	private EditField gpsInterval;
	private LabelField enableMsg;
	private CheckboxField enable;
	private EditField confirmationMSG;
	private ButtonField okButton;
	private BitmapField solutecImg;
	private Bitmap logo1;
	
	private String id;
	private String ipAdd;
	private String port;
	private String sendInterval;
	private String captureInterval;
	private String enableService="stop";
	
	private StringBuffer configurationBuffer;
	private String configurationString;

	
	
		
	public static void main(String[] args) {
		
			SolutecGpsConfigurator stGPS = new SolutecGpsConfigurator();
        	stGPS.enterEventDispatcher();
        	
	}
	
	
	public SolutecGpsConfigurator() {
		
	SolutecGPSScreen screen = new SolutecGPSScreen();
    configurationBuffer = new StringBuffer();
	pushScreen(screen);
	}

	 

	 private final class SolutecGPSScreen extends MainScreen {
			
			
			
			public SolutecGPSScreen(){
				super();
				
				
				
				
			    title = new LabelField("Solutec Localizator",LabelField.ELLIPSIS | LabelField.USE_ALL_WIDTH);
				setTitle(title);
				
				 
				firstManager = new VerticalFieldManager();
				secondManager = new VerticalFieldManager();
				thirdManager = new VerticalFieldManager();
				fourthManager = new VerticalFieldManager();
				fifthManager = new VerticalFieldManager();
				sixthManager = new VerticalFieldManager();
				seventhManager = new VerticalFieldManager();
				bottonManager = new HorizontalFieldManager(Field.FIELD_LEFT);
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
				add(sixthManager);
				add(new SeparatorField());
				add(seventhManager);				
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
				gpsMsg = new LabelField("Ingrese periodo de verificación GPS");
				gpsInterval = new EditField("Periodo GPS:"," ");
				timeMsg = new LabelField("Ingrese intervalo de tiempo");
				timeInterval = new EditField("Intervalo de tiempo (Seg):"," ");
				enableMsg = new LabelField("Seleccione para habilitar o deshabilitar");
				enable = new CheckboxField("Habilitar", true);
				confirmationMSG = new EditField();
								
				FieldChangeListener buttonListener = new FieldChangeListener() {
					public void fieldChanged(Field field, int context) {
						
						id = idNumber.toString().trim();
						ipAdd = ipAddress.toString().trim();
						port = portNumber.toString().trim();
						sendInterval = timeInterval.toString().trim();
						captureInterval = gpsInterval.toString().trim();
						
						if(enable.getChecked())
							enableService = "start";
						
						configurationBuffer.append("!");
						configurationBuffer.append(id);
						configurationBuffer.append("!");
						configurationBuffer.append(ipAdd);
						configurationBuffer.append("!");
						configurationBuffer.append(port);
						configurationBuffer.append("!");
						configurationBuffer.append(sendInterval);
						configurationBuffer.append("!");
						configurationBuffer.append(captureInterval);
						configurationBuffer.append("!");
						configurationBuffer.append(enableService);
						configurationBuffer.append("!");
						configurationString = configurationBuffer.toString();
												
						if(writeTextFile(configurationString)){
								SolutecGpsConfigurator.this.updateLS("Datos Guardados");
							}
							else{
								SolutecGpsConfigurator.this.updateLS("Datos No Guardados");
							}
													
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
				fifthManager.add(gpsMsg);
				fifthManager.add(gpsInterval);
				sixthManager.add(timeMsg);
				sixthManager.add(timeInterval);
				seventhManager.add(enableMsg);
				seventhManager.add(enable);
				bottonManager.add(okButton);
				bottonManager.add(confirmationMSG);
			}
						     
			
			
			
			public void close()
		        {
		           		         
		            super.close();
		        }
			
		}
	 
	 private static boolean writeConfiguration (String configInfo){
		 boolean success = false;
		 try 
		    {
		      FileConnection fc = (FileConnection)Connector.open("file:///store/home/user/archivoInicio.txt");
		      // If no exception is thrown, then the URI is valid, but the file may or may not exist.
		      	if (fc.exists())
		      	{
		    	  OutputStream outStream = fc.openOutputStream(); 
		    	  outStream.write(configInfo.getBytes());
		    	  outStream.close();
		    	  fc.close();
		    	  success = true;
		      	}
		      	else{
		      		success = false;
		      	}
		     }
		     catch (IOException ioe) 
		     {
		        System.out.println(ioe.getMessage() );
		     }
		 return success;

	 }
	 
	 public static boolean writeTextFile(String text) {
		 	String fName = "file:///store/home/user/archivoInicio.txt";
	        DataOutputStream os = null;
	        FileConnection fconn = null;
	        boolean processCompleted=false;
	        try {
	        		
	                fconn = (FileConnection) Connector.open(fName);
	                if (!fconn.exists())
	                        fconn.create();

	                os = fconn.openDataOutputStream();
	                os.write(text.getBytes());
	                
	                processCompleted=true;
	        } catch (IOException e) {

	        }catch(IllegalModeException e){

	        }finally {
	                try {
	                        if (null != os)
	                                os.close();
	                        if (null != fconn)
	                                fconn.close();
	                } catch (IOException e) {
	                	
	                }catch (Exception e){
	                	
	                }
	        }
	        
	        return processCompleted;
	}


 
	 
	 private void updateLS(final String msg)
	    {
	        invokeLater(new Runnable()
	        {
	            public void run()
	            {
	                confirmationMSG.setText(msg);
	                
	            }
	        });
	    }
	
}
