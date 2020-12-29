/*
 * PointScreen.java
 * 
 * Copyright © 1998-2010 Research In Motion Ltd.
 * 
 * Note: For the sake of simplicity, this sample application may not leverage
 * resource bundles and resource strings.  However, it is STRONGLY recommended
 * that application developers make use of the localization features available
 * within the BlackBerry development platform to ensure a seamless application
 * experience across a variety of languages and geographies.  For more information
 * on localizing your application, please refer to the BlackBerry Java Development
 * Environment Development Guide associated with this release.
 */

package com.rim.samples.device.gpsdemo;

import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.io.*;
import net.rim.device.api.system.*;
import javax.microedition.io.*;
import java.util.*;
import java.io.*;
import javax.microedition.location.*;
import com.rim.samples.device.gpsdemo.GPSDemo.WayPoint;

/**
 * A screen to render the saved WayPoints.
 */ 
class PointScreen extends MainScreen implements ListFieldCallback
{
    private Vector _points;
    private ListField _listField;
    
    PointScreen(Vector points)
    {
        
        LabelField title = new LabelField("Previous waypoints", LabelField.ELLIPSIS | LabelField.USE_ALL_WIDTH);
         setTitle(title);
         
        _points = points;
        _listField = new ListField();   
        _listField.setCallback(this);  
        add(_listField);
        addMenuItem(_viewPointAction);
        addMenuItem(_deletePointAction);
        
        reloadWayPointList();
    }    
    
    private void reloadWayPointList()
    {           
            // Refreshes wayPoint list on screen.
            _listField.setSize(_points.size());            
    }
    
    private void displayWayPoint()
    {
        int index = _listField.getSelectedIndex();
        ViewScreen screen = new ViewScreen( (WayPoint)_points.elementAt(index), index) ;
        UiApplication.getUiApplication().pushModalScreen( screen );            
    }
    
    /**
    * Overrides method in super class.
    * @see net.rim.device.api.ui.Screen#keyChar(char,int,int)
    */   
    protected boolean keyChar(char key, int status, int time)
     {
         // Intercept the ENTER key. 
        if (key == Characters.ENTER)
        {
            displayWayPoint();  
            return true;          
        }        
        return super.keyChar(key, status, time);
    }
    
    /**
     * Handles a trackball click and provides identical behavior to an
     * ENTER keypress event.  
     * @see net.rim.device.api.ui.Screen#invokeAction(int)
     */
    protected boolean invokeAction(int action)
    {        
        switch(action)
        {
            case ACTION_INVOKE: // Trackball click.
                displayWayPoint();                
                return true; // We've consumed the event.
        }    
        return  super.invokeAction(action);
    }
       
    // ListFieldCallback methods -----------------------------------------------------------------------------------
    /**
     * @see net.rim.device.api.ui.component.ListFieldCallback#drawListRow(ListField , Graphics , int , int , int)
     */
    public void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) 
    {
        if ( listField == _listField && index < _points.size())
        {
            String name = "Waypoint " + index;
            graphics.drawText(name, 0, y, 0, width);
        }
    }
    
    /**
     * @see net.rim.device.api.ui.component.ListFieldCallback#get(ListField , int)
     */
    public Object get(ListField listField, int index)
    {
        if ( listField == _listField )
        {
            // If index is out of bounds an exception will be thrown, but that's the behaviour we want in that case.
            return _points.elementAt(index);
        }
        
        return null;
    }
    
    /**
    * @see net.rim.device.api.ui.component.ListFieldCallback#getPreferredWidth(ListField)
    */
    public int getPreferredWidth(ListField listField) 
    {
        // Use all the width of the current LCD.
        return Display.getWidth();
    }
    
    /**
    * @see net.rim.device.api.ui.component.ListFieldCallback#indexOfList(ListField , String , int)
    */
    public int indexOfList(ListField listField, String prefix, int start) 
    {
        return -1; // Not implemented.
    }   
    
    // Menu items ---------------------------------------------------------------
    MenuItem _viewPointAction = new MenuItem("View" , 100000, 10)
    {       
        public void run()
        {
            displayWayPoint();
        }
    };
    
    MenuItem _deletePointAction = new MenuItem("Delete" , 100000, 11)
    {       
        public void run()
        {
            GPSDemo.removeWayPoint((WayPoint)_points.elementAt(_listField.getSelectedIndex()));  
            reloadWayPointList();    
        }
    };  
    
    /**
     * A screen to render a particular Waypoint.
     */
    private static class ViewScreen extends MainScreen 
    {       
        ViewScreen(WayPoint point, int count)
        {
            LabelField title = new LabelField("Waypoint" + count , LabelField.ELLIPSIS | LabelField.USE_ALL_WIDTH);        
            setTitle(title);
                                   
            Date date = new Date(point._startTime);
            String startTime = date.toString();
            date = new Date(point._endTime);
            String endTime = date.toString();
            
            float avgSpeed = point._distance/(point._endTime - point._startTime);                 
            
            add(new RichTextField("Start: " + startTime, Field.NON_FOCUSABLE));
            add(new RichTextField("End: " + endTime, Field.NON_FOCUSABLE));
            add(new RichTextField("Horizontal Distance (m): " + Float.toString(point._distance), Field.NON_FOCUSABLE));
            add(new RichTextField("Vertical Distance (m): " + Float.toString(point._verticalDistance), Field.NON_FOCUSABLE));
            add(new RichTextField("Average Speed(m/s): " + Float.toString(avgSpeed), Field.NON_FOCUSABLE));           
        }        
    }    
}
