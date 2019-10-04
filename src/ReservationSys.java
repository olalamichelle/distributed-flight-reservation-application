import java.util.*;
import java.net.*;
import java.io.*;
import java.util.stream.Collectors;

public class ReservationSys {
    private ArrayList<Reservation> dict;
    private ArrayList<EventRecord> log;
    private Integer timeTable[][];

    private Integer siteTimeStamp;
    private String siteId;
    ArrayList<HashMap<String, String>> sitesInfo;

    public ReservationSys(ArrayList<HashMap<String, String>> sitesInfo, Integer siteNum, String siteId) {
        this.dict = new ArrayList<Reservation>();
        this.log = new ArrayList<EventRecord>();
        this.timeTable = new Integer[siteNum][siteNum];

        this.siteTimeStamp = 0;
        this.siteId = siteId;
        this.sitesInfo = sitesInfo;
    }

    public boolean isConflict(ArrayList<Integer> flights) {

        HashMap<Integer, Integer> ReservedFlights = new HashMap<>(); // keep track of reserved flights in dict
        for (int i = 0; i < this.dict.size(); i++) {
            ArrayList<Integer> curReservedFlights = dict.get(i).getFlights();
            Integer counter = 0;
            for (int j = 0; j < curReservedFlights.size(); j++) {
                counter++;
                ReservedFlights.put(curReservedFlights.get(j), counter);
            }
        }

        for (int i = 0; i < flights.size(); i++) {
            if (ReservedFlights.get(flights.get(i)) == 2) {
                return true;
            }
        }

        return false;
    }


    public boolean insert(String[] orderInfo) {
        String clientName = orderInfo[0];
        ArrayList<Integer> flights = new ArrayList<>();
        for (String s : orderInfo[1].split(",")) {
            flights.add(Integer.parseInt(s));
        }

        if (isConflict(flights)) {
            System.out.println("Cannot schedule reservation for " + clientName);
            return false;
        }

        // create Reservation object and EventRecord object
        this.siteTimeStamp += 1;
        Reservation newReserv = new Reservation(clientName, "pending", flights);
        EventRecord eventRecord = new EventRecord("insert", this.siteId, this.siteTimeStamp);

        // insert into dict and log
        this.dict.add(newReserv);
        this.log.add(eventRecord);

        // update timetable
        Integer curSiteIndex = 0;
        for (int i = 0; i < this.sitesInfo.size(); i++) {
            HashMap<String, String> curSite = sitesInfo.get(i);
            if (curSite.get("siteId").equals(this.siteId)) curSiteIndex = 0;
        }
        timeTable[curSiteIndex][curSiteIndex] = this.siteTimeStamp;

        System.out.println("Reservation submitted for " + clientName);
        return true;
    }

    public void delete() {

    }

    public void update() {

    }

    public boolean hasRec() {
        return false;
    }



}
