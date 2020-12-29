package persistentStoreProbe;

import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.ui.*;
import java.util.Vector;


class Scr extends MainScreen implements FieldChangeListener{     
	
	ButtonField mBtnInit = null;     
	BasicEditField mInputSenderId = null;     
	BasicEditField mInputReceiverId = null;     
	ButtonField mBtnSearch = null;     
	VerticalFieldManager mMailsList = null;     
	
	public Scr(){         
	mBtnInit = new ButtonField( "Init Persistenet Storage", ButtonField.CONSUME_CLICK );
	mBtnInit.setChangeListener( this );
	add( mBtnInit );
	mInputSenderId = new BasicEditField( "sender id:", "43" );
	add( mInputSenderId ); 
	mInputReceiverId = new BasicEditField( "receiver id:", "12" );
	add( mInputReceiverId );
	mBtnSearch = new ButtonField( "Search", ButtonField.CONSUME_CLICK );
	mBtnSearch.setChangeListener( this );         
	add( mBtnSearch );
	mMailsList = new VerticalFieldManager();
	add( mMailsList );     
	}     
	
	public Vector getMailByIds( int senderId, int recepientId ){         
		Vector result = new Vector();         
		Mail[] mails = PersistentStoreHelper.retrieveMails();         
		for( int i = 0; i < mails.length; i++ )             
			if( mails[ i ].mSenderId == senderId ){                 
				int[] receiverIdList = mails[ i ].mReceiverIdList;                 
				for( int j = 0; j < receiverIdList.length; j++ )                     
					if( recepientId == receiverIdList[ j ] )                         
						result.addElement( mails[ i ] );             }         
		return result;     
		}  
	
	public void initPersistentStorage(){         
		// create 100 contacts and save them         
		Contact[] contacts = new Contact[ 100 ];         
		for( int i = 0; i < 100; i++ ){             
			String name = "name" + String.valueOf( i );
			String adress = name + "@mail.com";
			contacts[ i ] = new Contact( i, adress, name );         
		}
		PersistentStoreHelper.saveContacts( contacts );    
		// create messages from each to every contact and save them
		Mail[] mails = new Mail[ 10000 - 100 ];
		int k = 0;
		for( int i = 0; i < 100; i++ )
			for( int j = 0; j < 100; j++ )
				if( i != j ){
					Mail mail = new Mail( "Hello!", contacts[ i ].mId, new int[]{ contacts[ j ].mId } );
					mails[ k ] = mail;
					k++;
					} 
		PersistentStoreHelper.saveMails( mails );
		}   
	
	public void fieldChanged( Field field, int context ){
		if( field == mBtnInit )
			initPersistentStorage();
		else if( field == mBtnSearch ){
			mMailsList.deleteAll();
			int senderId = Integer.parseInt( mInputSenderId.getText() );
			int receiverId = Integer.parseInt( mInputReceiverId.getText() );
			Contact[] contacts = PersistentStoreHelper.retrieveContacts();
			Vector result = getMailByIds( senderId, receiverId );
			for( int i = 0, cnt = result.size(); i < cnt; i++ ){
				Mail mail = ( Mail )result.elementAt( i );
				String from = "From: " + contacts[ mail.mSenderId ].mName + " <" + contacts[ mail.mSenderId ].mAdress + ">";
				String to = "To: ";
				for( int j = 0; j < mail.mReceiverIdList.length; j++ ){
					int id = mail.mReceiverIdList[ j ];
					to += contacts[ id ].mName + " <" + contacts[ id ].mAdress + ">; ";
					}
				to = to.substring( 0, to.length() - 2 );
				String msg = "Message: " + mail.mMessage;
				mMailsList.add( new LabelField( from ) );
				mMailsList.add( new LabelField( to ) );
				mMailsList.add( new LabelField( msg ) );
			}
		}
	
	}

}

		
