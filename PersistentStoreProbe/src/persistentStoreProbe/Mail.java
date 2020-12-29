package persistentStoreProbe;

import net.rim.device.api.util.*;

public class Mail implements Persistable{ 
	String mMessage = null; 
	int mSenderId = -1;
	int[] mReceiverIdList = null;
	
	public Mail(String message, int senderId, int[] receiverIdList){ 
		mMessage = message; 
		mSenderId = senderId;
		mReceiverIdList = receiverIdList;
		}
	} 