package bb.develops.solutec;
//import net.rim.device.api.util.*;


public class configurationInfo {
	
	String idNumber;
	String ipAddress;
	String ipPort;
	int intervalTime;
	int gpsTime;
	
	configurationInfo (String id, String address, String port, int interval, int gpsTm){
		idNumber = id;
		ipAddress = address;
		ipPort= port;
		intervalTime = interval;
		gpsTime = gpsTm;
	}

}
