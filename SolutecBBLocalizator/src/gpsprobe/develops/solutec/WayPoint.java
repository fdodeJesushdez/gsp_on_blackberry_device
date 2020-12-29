package gpsprobe.develops.solutec;

import net.rim.device.api.util.Persistable;

public class WayPoint implements Persistable
{
    long _startTime;
    long _endTime;
    float _distance;
    float _verticalDistance;
    
    WayPoint(long startTime,long endTime,float distance,float verticalDistance)
    {
        _startTime=startTime;
        _endTime=endTime;
        _distance=distance;
        _verticalDistance=verticalDistance;
    }
}
