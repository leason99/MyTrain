package leason.mytraintime;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by leason on 2016/11/3.
 */
public class StopTime {

    private String stopsequence;
    private String stationid;
    private stationname stationname;
    private   String arrivaltime;
    private  String departuretime;


    public String getstopsequence() {
        return stopsequence;
    }

    public void setstopsequence(String stopsequence) {
        this.stopsequence = stopsequence;
    }

    public String getstationid() {
        return stationid;
    }

    public void setstationid(String stationid) {
        this.stationid = stationid;
    }

    public StopTime.stationname getstationname() {
        return stationname;
    }

    public void setstationname(StopTime.stationname stationname) {
        this.stationname = stationname;
    }

    public String getarrivaltime() {
        return arrivaltime;
    }

    public void setarrivaltime(String arrivaltime) {
        this.arrivaltime = arrivaltime;
    }

    public String getdeparturetime() {
        return departuretime;
    }

    public void setDeparturetime(String departureTime) {
        this.departuretime = departureTime;
    }

    static class stationname{
    String zh_tw,en;

        public String getzh_tw() {
            return zh_tw;
        }

        public void setzh_tw(String zh_tw) {
            this.zh_tw = zh_tw;
        }

        public String geten() {
            return en;
        }

        public void seten(String en) {
            this.en = en;
        }
    }


}
