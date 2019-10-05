import java.util.*;
import java.net.*;
import java.io.*;
import java.util.stream.Collectors;

public class ReservationSys {
    // need to backup for site recovery
    private ArrayList<Reservation> dict; // array of reservation object
    private ArrayList<EventRecord> log; // array of eventRecord object
    private Integer timeTable[][];

    private Integer siteTimeStamp;
    private String siteId;

    // ip, start port, end port, siteId stored by its site idx
    private ArrayList<HashMap<String, String>> sitesInfo;

    public ReservationSys(ArrayList<HashMap<String, String>> sitesInfo, Integer siteNum, String siteId) {
        this.dict = new ArrayList<Reservation>();
        this.log = new ArrayList<EventRecord>();
        this.timeTable = new Integer[siteNum][siteNum];

        this.siteTimeStamp = 0;
        this.siteId = siteId;
        this.sitesInfo = sitesInfo;
    }

    public Integer siteIdToIdx(String siteId) {
        int siteIdx = 0;
        for (int i = 0; i < this.sitesInfo.size(); i++) {
            HashMap<String, String> curSite = this.sitesInfo.get(i);
            if (curSite.get("siteId").equals(siteId)) siteIdx = i;
        }
        return siteIdx;
    }

    public Reservation findReservInDict(String clientName) {
        for (int i = 0; i < this.dict.size(); i++) {
            if (dict.get(i).getClientName().equals(clientName)) {
                return dict.get(i);
            }
        }
        return null;
    }

    // flights: flights in msg
    public boolean isConflict(ArrayList<Integer> flights) {

        HashMap<Integer, Integer> ReservedFlights = new HashMap<>(); // keep track of reserved flights in dict

        for (int i = 0; i < this.dict.size(); i++) {
            // reserved flights provided in msg
            ArrayList<Integer> curReservedFlights = dict.get(i).getFlights();
            for (int j = 0; j < curReservedFlights.size(); j++) {
                Integer curFlight = curReservedFlights.get(j);
                // current flight counts in dictionary
                Integer curCnt = ReservedFlights.get(curReservedFlights.get(j));
                ReservedFlights.put(curFlight, (curCnt == null) ? 1 : curCnt + 1);
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
        EventRecord eventRecord = new EventRecord("insert", this.siteId, this.siteTimeStamp, newReserv);

        // insert into dict and log
        this.dict.add(newReserv);
        this.log.add(eventRecord);

        // update timetable
        Integer curSiteIdx = siteIdToIdx(this.siteId);
        this.timeTable[curSiteIdx][curSiteIdx] = this.siteTimeStamp;

        System.out.println("Reservation submitted for " + clientName);
        return true;
    }

    public boolean delete(String[] orderInfo) {
        String clientName = orderInfo[0];
        Reservation targetReserv = findReservInDict(clientName);
        if (targetReserv == null) return false;

        // create a event record
        this.siteTimeStamp += 1;
        EventRecord eventRecord = new EventRecord("delete", this.siteId, this.siteTimeStamp, targetReserv);

        // update log, dictionary and timetable
        this.log.add(eventRecord);
        this.dict.remove(targetReserv);

        Integer curSiteIdx = siteIdToIdx(this.siteId);
        this.timeTable[curSiteIdx][curSiteIdx] = this.siteTimeStamp;

        System.out.println("Reservation of client" + clientName + " cancelled.");
        return true;
    }

    public void update() {

    }

    public boolean hasRec(EventRecord eventRecord, String targetSiteId) {
        String siteWhereEventHappened = eventRecord.getSiteId();
        Integer eventSiteIdx = siteIdToIdx(eventRecord.getSiteId());
        Integer targetSiteIdx = siteIdToIdx(targetSiteId);
        return this.timeTable[targetSiteIdx][eventSiteIdx] >= eventRecord.getSiteTimestamp();
    }



}
