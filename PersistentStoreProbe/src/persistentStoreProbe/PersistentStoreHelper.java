package persistentStoreProbe;

import net.rim.device.api.system.*;

public class PersistentStoreHelper{   
	
	static PersistentObject contactStore = PersistentStore.getPersistentObject( 0xf140775afcb94f90L );
	static PersistentObject mailStore = PersistentStore.getPersistentObject( 0xd43b0423228ff7c0L );
	
	public static void saveContacts( Contact[] contacts ){         
		saveObject( contactStore, contacts );
	}     
	
	public static void saveMails( Mail[] mails ){         
		saveObject( mailStore, mails );     
	}     
	
	public static Contact[] retrieveContacts(){
		return ( Contact[] )retrieveObject( contactStore );
		}     
	
	public static Mail[] retrieveMails(){
		return ( Mail[] )retrieveObject( mailStore );
	}     
	
	public static void saveObject( PersistentObject store, Object object ){
		synchronized( store ){             
			store.setContents( object );
		}
	}
	
	public static Object retrieveObject( PersistentObject store ){
		Object result = null;
		synchronized( store ){
			result = store.getContents();
			}
		return result; 
	}
} 