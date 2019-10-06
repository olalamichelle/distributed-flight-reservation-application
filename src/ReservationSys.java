import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class ReservationSys {
    // need to backup for site recovery
    private ArrayList<Reservation> dict; // array of reservation object
    private ArrayList<EventRecord> log; // array of eventRecord object
    private Integer[][] timeTable;

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

    // get the latest flight reservation record
    public EventRecord latestRecord(Integer flight) {
        Collections.sort(this.log);
        for (int i = this.log.size() - 1; i >= 0; i--) {
            ArrayList<Integer> curFlights = log.get(i).getReservation().getFlights();
            if (curFlights.contains(flight)) return log.get(i);
        }
        return null;
    }

    // flights: flights in msg
    // return arraylist of conflicted record in current dict
    public ArrayList<EventRecord> isConflict(ArrayList<Integer> flights) {
        ArrayList<EventRecord> conflictRecord = new ArrayList<>();

        // keep track of reserved flights in dict, mapping flight number to count
        HashMap<Integer, Integer> ReservedFlights = new HashMap<>();
        // mapping flight number to latest event record
        HashMap<Integer, EventRecord> flightsToReserv = new HashMap<>();

        for (int i = 0; i < this.dict.size(); i++) {
            // reserved flights provided in msg
            ArrayList<Integer> curReservedFlights = this.dict.get(i).getFlights();
            for (int j = 0; j < curReservedFlights.size(); j++) {
                Integer curFlight = curReservedFlights.get(j);

                // map flight number to reservation
                EventRecord curLatestRecord = latestRecord(curFlight);
                flightsToReserv.put(curFlight, curLatestRecord);

                // current flight counts in dictionary
                Integer curCnt = ReservedFlights.get(curReservedFlights.get(j));
                ReservedFlights.put(curFlight, (curCnt == null) ? 1 : curCnt + 1);
            }
        }

        for (int i = 0; i < flights.size(); i++) {
            if (ReservedFlights.get(flights.get(i)) == 2) {
                conflictRecord.add(flightsToReserv.get(flights.get(i)));
            }
        }

        return conflictRecord;
    }


    public boolean insert(String[] orderInfo) {
        String clientName = orderInfo[0];
        ArrayList<Integer> flights = new ArrayList<>();
        for (String s : orderInfo[1].split(",")) {
            flights.add(Integer.parseInt(s));
        }

        if (isConflict(flights) != null) {
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

    // TODO: status change
    // TODO: truncate log
    public void update(CommunicateInfo recInfo, String senderId) {
        // resolve received information
        ArrayList<EventRecord> recEventRecords = recInfo.getEventRecords();
        Integer siteNum = this.sitesInfo.size();
        Integer[][] recTimeTable = recInfo.getTimeTable();

        // store only new event record
        ArrayList<EventRecord> NE = new ArrayList<>();
        for (int i = 0; i < recEventRecords.size(); i++) {
            if (!hasRec(recEventRecords.get(i), this.siteId)) {
                NE.add(recEventRecords.get(i));
            }
        }

        // update timetable
        Integer curSiteIdx = siteIdToIdx(this.siteId);
        Integer senderSiteIdx = siteIdToIdx(senderId);

        for (int j = 0; j < siteNum; j++) {
            this.timeTable[curSiteIdx][j] = Integer.max(this.timeTable[curSiteIdx][j], recTimeTable[senderSiteIdx][j]);
        }

        for (int j = 0; j < siteNum; j++) {
            for (int l = 0; l < siteNum; l++) {
                this.timeTable[j][l] = Integer.max(this.timeTable[j][l], recTimeTable[j][l]);
            }
        }

        // update log for every new record
        for (int i = 0; i < NE.size(); i++) {
            this.log.add(NE.get(i));
        }

        // update dictionary for every new record
        for (int i = 0; i < NE.size(); i++) {
            if (NE.get(i).getOperation().equals("delete")) {
                this.dict.remove(NE.get(i).getReservation());
            }
            else if (NE.get(i).getOperation().equals("insert")) {
                // find out whether this event record is deleted later
                int deletedLater = 0;
                for (int j = 0; j < NE.size(); j++) {
                    if (NE.get(j).getOperation().equals("delete")
                            && NE.get(j).getReservation().equals(NE.get(i).getReservation())) {
                        deletedLater = 1;
                    }
                }
                if (deletedLater == 1) continue;

                // detect conflicts
                ArrayList<EventRecord> conflictsRecords = new ArrayList<>(); // store all the conflicted records
                conflictsRecords = isConflict(NE.get(i).getReservation().getFlights());
                conflictsRecords.add(NE.get(i));
                Collections.sort(conflictsRecords);
                // no conflict
                if (conflictsRecords.size() == 1) {
                    this.dict.add(NE.get(i).getReservation());
                }
                // delete current record
                else if (conflictsRecords.size() >= 2 && conflictsRecords.get(1) == NE.get(i)) {
                    this.siteTimeStamp += 1;
                    EventRecord deleteCurRec = new EventRecord("delete", this.siteId, this.siteTimeStamp, NE.get(i).getReservation());
                    this.log.add(deleteCurRec);
                    this.timeTable[siteIdToIdx(this.siteId)][siteIdToIdx(this.siteId)] = this.siteTimeStamp;

                    System.out.println("Reservation record for: " + NE.get(i).getReservation().getClientName() + " canceled.");
                }
                // delete local record
                else {
                    this.siteTimeStamp += 1;
                    EventRecord deleteLocalRec = new EventRecord("delete", this.siteId, this.siteTimeStamp, conflictsRecords.get(1).getReservation());
                    this.log.add(deleteLocalRec);
                    this.timeTable[siteIdToIdx(this.siteId)][siteIdToIdx(this.siteId)] = this.siteTimeStamp;
                    this.dict.remove(conflictsRecords.get(1).getReservation());
                    this.dict.add(NE.get(i).getReservation());

                    System.out.println("Reservation record for: " + conflictsRecords.get(1).getReservation().getClientName() + " canceled.");
                }
            }
        }
    }

    // current site's knowledge about what targetsite knows about event
    public boolean hasRec(EventRecord eventRecord, String targetSiteId) {
        String siteWhereEventHappened = eventRecord.getSiteId();
        Integer eventSiteIdx = siteIdToIdx(siteWhereEventHappened);
        Integer targetSiteIdx = siteIdToIdx(targetSiteId);
        return this.timeTable[targetSiteIdx][eventSiteIdx] >= eventRecord.getSiteTimestamp();
    }

    public void printLog() {//private ArrayList<EventRecord> log;
        for (int i = 0; i < log.size(); i++) {
            EventRecord cur = log.get(i);
            System.out.println(cur.getOperation() + " " +
                    cur.getReservation().getClientName() + " "
                    + cur.getReservation().printFlight());
        }
    }

    public void printDictionary() {//private ArrayList<Reservation> dict;
        for (int i = 0; i < dict.size(); i++) {
            Collections.sort(dict, new CustomComparator());
            System.out.println(dict.get(i).getClientName() + " " + dict.get(i).printFlight()
                    + " " +dict.get(i).getStatus());
        }
    }

    public void printClock() {//private Integer[][] timeTable;
        for (int i = 0; i < timeTable.length; i++) {
            for (int j = 0; j < timeTable[0].length; j++) {
                System.out.print(timeTable[i][j] + " ");
            }
            System.out.printf("%n");
        }
    }

}

class CustomComparator implements Comparator<Reservation> {
    @Override
    public int compare(Reservation o1, Reservation o2) {
        return o1.getClientName().compareTo(o2.getClientName());
    }
}
