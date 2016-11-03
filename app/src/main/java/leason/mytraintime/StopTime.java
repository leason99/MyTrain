package leason.mytraintime;

import java.util.List;

/**
 * Created by leason on 2016/11/3.
 */
public class StopTime {
    int StopSequence;
    int StationID;
    List<StationName> StationName ;
      String   ArrivalTime;
    String  DepartureTime;

    public int getStopSequence() {
        return StopSequence;
    }

    public int getStationID() {
        return StationID;
    }

    public List<StopTime.StationName> getStationName() {
        return StationName;
    }

    public String getArrivalTime() {
        return ArrivalTime;
    }

    public String getDepartureTime() {
        return DepartureTime;
    }

    class StationName{
    String Zh_tw,En;
}


}
