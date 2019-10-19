import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

public class ReservationSys {
    // MEMBER VARIABLES
    // need to backup for site recovery
    private ArrayList<Reservation> dict; // array of reservation object
    private ArrayList<EventRecord> log; // array of eventRecord object
    private Integer[][] timeTable;

    private Integer siteTimeStamp;
    private String siteId;

    // mapping site properties: ip, start port, end port, siteId, stored and sorted by its site idx
    private ArrayList<HashMap<String, String>> sitesInfo;

    // CONSTRUCTOR
    public ReservationSys(ArrayList<HashMap<String, String>> sitesInfo, Integer siteNum, String siteId) {
        this.dict = new ArrayList<Reservation>();
        this.log = new ArrayList<EventRecord>();
        this.timeTable = new Integer[siteNum][siteNum];
        for (int i = 0; i < siteNum; i++) {
            for (int j = 0; j < siteNum; j++) {
                this.timeTable[i][j] = 0;
            }
        }

        this.siteTimeStamp = 0;
        this.siteId = siteId;
        this.sitesInfo = sitesInfo;
    }

    // GETTER and SETTERS
    public ArrayList<Reservation> getDict() {
        return dict;
    }

    public void setDict(ArrayList<Reservation> dict) {
        this.dict = dict;
    }

    public ArrayList<EventRecord> getLog() {
        return log;
    }

    public void setLog(ArrayList<EventRecord> log) {
        this.log = log;
    }

    public Integer[][] getTimeTable() {
        return timeTable;
    }

    public void setTimeTable(Integer[][] timeTable) {
        this.timeTable = timeTable;
    }

    public Integer getSiteTimeStamp() {
        return siteTimeStamp;
    }

    public void setSiteTimeStamp(Integer siteTimeStamp) {
        this.siteTimeStamp = siteTimeStamp;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public ArrayList<HashMap<String, String>> getSitesInfo() {
        return sitesInfo;
    }

    public void setSitesInfo(ArrayList<HashMap<String, String>> sitesInfo) {
        this.sitesInfo = sitesInfo;
    }

    // HELPER FUNCTIONS
    public void recover(Integer siteNum) {
        // recover time table
        try {
            this.timeTable = new Integer[siteNum][siteNum];
            for (int i = 0; i < siteNum; i++) {
                for (int j = 0; j < siteNum; j++) {
                    this.timeTable[i][j] = 0;
                }
            }
            BufferedReader timeReader = new BufferedReader(new FileReader("timeTable.txt"));
            String line = "";
            int row = 0;
            while((line = timeReader.readLine()) != null) {
                String[] cols = line.split(",");
                int col = 0;
                for(String  c : cols) {
                    this.timeTable[row][col] = Integer.parseInt(c);
                    col++;
                }
                row++;
            }

            // update site's timestamp
            Integer siteIdx = siteIdToIdx(this.siteId);
            this.siteTimeStamp = this.timeTable[siteIdx][siteIdx];

            timeReader.close();
        } catch (IOException e) {
            System.out.println("read time table file error");
        }

        // recover dictionary
        try {
            this.dict = new ArrayList<Reservation>();
            BufferedReader dictReader = new BufferedReader(new FileReader("dictionary.txt"));
            String line = "";
            while ((line = dictReader.readLine()) != null) {
                String[] cols = line.split(" ");
                String[] curFlights = cols[1].split(",");
                ArrayList<Integer> flights = new ArrayList<>();
                for (int i = 0; i < curFlights.length; i++) {
                    flights.add(Integer.parseInt(curFlights[i]));
                }
                Reservation curRes = new Reservation(cols[0], cols[2], flights);
                this.dict.add(curRes);
            }

        } catch (IOException e) {
            System.out.println("read dictionary file error");
        }

        // recover log
        try {
            this.log = new ArrayList<EventRecord>();
            BufferedReader logReader = new BufferedReader(new FileReader("log.txt"));
            String line = "";
            while ((line = logReader.readLine()) != null) {
                String[] cols = line.split(" ");
                String[] curFlights = cols[1].split(",");
                ArrayList<Integer> flights = new ArrayList<>();
                for (int i = 0; i < curFlights.length; i++) {
                    flights.add(Integer.parseInt(curFlights[i]));
                }
                Reservation curRes = new Reservation(cols[0], cols[2], flights);
                EventRecord curEvent = new EventRecord(cols[3], cols[4], Integer.parseInt(cols[5]), curRes);
                this.log.add(curEvent);
            }
        } catch (IOException e) {
            System.out.println("read log file error");
        }
    }

    // will cover original if file name is the same
    public void record() {
        // Date date = new Date();
        // record timeTable
        // as a format of
        // 1,2,3
        // 4,5,6
        StringBuilder tableBuilder = new StringBuilder();
        for (int i = 0; i < this.timeTable.length; i++) {
            for (int j = 0; j < this.timeTable.length; j++) {
                tableBuilder.append(this.timeTable[i][j]);
                if(j < this.timeTable.length - 1) {
                    tableBuilder.append(",");
                }
            }
            tableBuilder.append("\n");
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("timeTable.txt"));
            writer.write(tableBuilder.toString());
            writer.close();
        } catch (IOException e) {
            System.out.println("write exception");
        }

        // record dictionary
        // as a format of
        // clientName1 1,2,3 status1
        // clientName2 4,5,6 status2
        StringBuilder dictBuilder = new StringBuilder();
        for (int i = 0; i < this.dict.size(); i++) {
            dictBuilder.append(dict.get(i).flatten());
            dictBuilder.append("\n");
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("dictionary.txt"));
            writer.write(dictBuilder.toString());
            writer.close();
        } catch (IOException e) {
            System.out.println("write exception");
        }

        // record log
        // as a format of
        // clientName1 1,2,3 status1 operation siteId siteTimestamp
        // clientName2 4,5,6 status2 operation siteId siteTimestamp
        StringBuilder logBuilder = new StringBuilder();
        for (int i = 0; i < this.log.size(); i++) {
            logBuilder.append(this.log.get(i).flatten());
            logBuilder.append("\n");
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("log.txt"));
            writer.write(logBuilder.toString());
            writer.close();
        } catch (IOException e) {
            System.out.println("write exception");
        }
    }

    /**
     * change site Id to corresponding index
     * @param siteId Id of denoted site
     * @return index of denoted site
     */
    public Integer siteIdToIdx(String siteId) {
        int siteIdx = 0;
        for (int i = 0; i < this.sitesInfo.size(); i++) {
            HashMap<String, String> curSite = this.sitesInfo.get(i);
            if (curSite.get("siteId").equals(siteId)) siteIdx = i;
        }
        return siteIdx;
    }

//    /**
//     * find reservation record in dictionary by client name
//     * @param clientName denoted client name
//     * @return reservation record of denoted client
//     */
//    public Reservation findReservInDict(String clientName) {
//        for (int i = 0; i < this.dict.size(); i++) {
//            if (dict.get(i).getClientName().equals(clientName)) {
//                return dict.get(i);
//            }
//        }
//        return null;
//    }
//
//    /**
//     * find latest event record in log by flight number
//     * @param flight denoted flight number
//     * @return latest event record of denoted flight number
//     */
//    public EventRecord latestRecord(Integer flight) {
//        Collections.sort(this.log);
//        for (int i = this.log.size() - 1; i >= 0; i--) {
//            ArrayList<Integer> curFlights = log.get(i).getReservation().getFlights();
//            if (curFlights.contains(flight)) return log.get(i);
//        }
//        return null;
//    }

    /**
     * to indicate in current site's knowledge, whether target site knows about denoted event record
     * @param eventRecord event record to be sent
     * @param targetSiteId receiver site
     * @return true then event record shouldn't be sent. False otherwise.
     */
    public boolean hasRec(EventRecord eventRecord, String targetSiteId) {
        String siteWhereEventHappened = eventRecord.getSiteId();
        Integer eventSiteIdx = siteIdToIdx(siteWhereEventHappened);
        Integer targetSiteIdx = siteIdToIdx(targetSiteId);
        return this.timeTable[targetSiteIdx][eventSiteIdx] >= eventRecord.getSiteTimestamp();
    }

    // flights: flights in msg
    // return arraylist of conflicted record in current dict

    /**
     * given a certain event record,
     * find out whether this event is conflicted with local event records in dictionary
     * @param flight flights in denoted event record
     * @return a list of event records that are conflicted with denoted event record
     */
    public ArrayList<EventRecord> isConflict(Integer flight) {
        ArrayList<EventRecord> conflictRecord = new ArrayList<>(); // to store conflicted event record in local dict

        // keep track of reserved flights in dict, mapping flight number to count
        HashMap<Integer, Integer> ReservedFlights = new HashMap<>();
//        // mapping flight number to latest event record
//        HashMap<Integer, EventRecord> flightsToReserv = new HashMap<>();

        for (int i = 0; i < this.dict.size(); i++) {
            // reserved flights for each reservation provided in local dictionary
            ArrayList<Integer> curLocalReservedFlights = this.dict.get(i).getFlights();
            for (int j = 0; j < curLocalReservedFlights.size(); j++) {
                Integer curFlight = curLocalReservedFlights.get(j);

//                // map current reserved flight number to reservation
//                EventRecord curLatestRecord = null;
//                Collections.sort(this.log);
//                for (int p = this.log.size() - 1; p >= 0; p--) {
//                    ArrayList<Integer> curFlights = this.log.get(p).getReservation().getFlights();
//                    if (curFlights.contains(curFlight)) {
//                        curLatestRecord = this.log.get(p);
//                        break;
//                    }
//                }
//                flightsToReserv.put(curFlight, curLatestRecord);

                // update current flight counts in dictionary
                Integer curCnt = ReservedFlights.get(curFlight); // current flight counts in dictionary
                ReservedFlights.put(curFlight, (curCnt == null) ? 1 : curCnt + 1);
            }
        }

        if (!ReservedFlights.isEmpty() && ReservedFlights.get(flight) != null) {
            if (ReservedFlights.get(flight) == 2) {
                // find all event record for the conflicted flight
                for (int p = 0; p < this.log.size(); p++) {
                    ArrayList<Integer> curFlights = this.log.get(p).getReservation().getFlights();
                    if (curFlights.contains(flight) && this.log.get(p).getOperation().equals("insert")) {
                        conflictRecord.add(this.log.get(p));
                    }
                }
            }
        }

        Collections.sort(conflictRecord);

//        System.out.println(conflictRecord.size() + "???anything wron? but I firstly got ");
//        for (int i = 0; i < conflictRecord.size(); i++) {
//            System.out.println(conflictRecord.get(i).getReservation().flatten());
//        }

        return conflictRecord;
    }


    public boolean insert(String[] orderInfo) {
        // 1. resolve information in order
        String clientName = orderInfo[1];
        ArrayList<Integer> flights = new ArrayList<>();
        for (String s : orderInfo[2].split(",")) {
            flights.add(Integer.parseInt(s));
        }

        // 2. detect conflict
        for (int i = 0; i < flights.size(); i++) {
            if (!isConflict(flights.get(i)).isEmpty()) {
                System.out.println("Cannot schedule reservation for " + clientName + " because of conflict");

//                System.out.println("[test] part of the conflicted event records are: ");
                ArrayList<EventRecord> conflicts = isConflict(flights.get(i));
                for (int j = 0; j < conflicts.size(); j++) {
//                    System.out.println("[test] " + conflicts.get(j).flatten());
                }

                return false;
            }
        }

        // 3. create Reservation object and EventRecord object
        this.siteTimeStamp += 1;
        Reservation newReserv = new Reservation(clientName, "pending", flights);
        EventRecord eventRecord = new EventRecord("insert", this.siteId, this.siteTimeStamp, newReserv);

        // 4. insert into dict, log and update timetable
        this.dict.add(newReserv);
        this.log.add(eventRecord);

        Integer curSiteIdx = siteIdToIdx(this.siteId);
        this.timeTable[curSiteIdx][curSiteIdx] = this.siteTimeStamp;

        System.out.println("Reservation submitted for " + clientName + ".");
        // backup for site recovery
        this.record();
        return true;
    }

    public boolean delete(String[] orderInfo) {
        String clientName = orderInfo[1];

        // 1. find the reservation in dictionary
        Reservation targetReserv = null;
        for (int i = 0; i < this.dict.size(); i++) {
            if (dict.get(i).getClientName().equals(clientName)) {
                targetReserv = dict.get(i);
            }
        }
        if (targetReserv == null) return false;

        // 2. create a event record
        this.siteTimeStamp += 1;
        EventRecord eventRecord = new EventRecord("delete", this.siteId, this.siteTimeStamp, targetReserv);

        // 3. update log, dictionary and timetable
        this.log.add(eventRecord);
        this.dict.remove(targetReserv);

        Integer curSiteIdx = siteIdToIdx(this.siteId);
        this.timeTable[curSiteIdx][curSiteIdx] = this.siteTimeStamp;

        System.out.println("Reservation of client" + clientName + " cancelled.");
        // backup for site recovery
        this.record();
        return true;
    }

    public void update(CommunicateInfo recInfo, String senderId) {
        // 1. resolve received information
        ArrayList<EventRecord> recEventRecords = recInfo.getEventRecords();

//        System.out.println("[test] now received event record is: ");
//        for (int i = 0; i < recEventRecords.size(); i++) {
//            System.out.println("[test] " + recEventRecords.get(i).flatten());
//        }
//        System.out.println("[test] size is: " + recEventRecords.size());

        Integer siteNum = this.sitesInfo.size();
        Integer[][] recTimeTable = recInfo.getTimeTable();

        // store only event records new to local knowledge
        ArrayList<EventRecord> NE = new ArrayList<>();
        for (int i = 0; i < recEventRecords.size(); i++) {
            if (!hasRec(recEventRecords.get(i), this.siteId)) {
                NE.add(recEventRecords.get(i));
            }
        }

//        System.out.println("&&&&&&now new records received are");
//        for (int i = 0; i < NE.size(); i++) {
//            System.out.println(NE.get(i).getReservation().flatten());
//        }

        // 2. update timetable
        Integer curSiteIdx = siteIdToIdx(this.siteId);
        Integer senderSiteIdx = siteIdToIdx(senderId);

        // 1) update direct knowledge
        for (int j = 0; j < siteNum; j++) {
            this.timeTable[curSiteIdx][j] = Integer.max(this.timeTable[curSiteIdx][j], recTimeTable[senderSiteIdx][j]);
        }

        // 2) update indirect knowledge
        for (int j = 0; j < siteNum; j++) {
            for (int l = 0; l < siteNum; l++) {
                this.timeTable[j][l] = Integer.max(this.timeTable[j][l], recTimeTable[j][l]);
            }
        }

        // 3. update log for every new record
        for (int i = 0; i < NE.size(); i++) {
            this.log.add(NE.get(i));
        }

        // 4. update dictionary for every new record
        for (int i = 0; i < NE.size(); i++) {

//            System.out.println("******now dealing with event record " + NE.get(i).getReservation().flatten());
//            System.out.println("******and now dictionary is ");
//            printDictionary();

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

                boolean delNew = false; // to indicate whether to delete the new record
                // loop through all the flight number in new record to detect conflicts
                for (int p = 0; p < NE.get(i).getReservation().getFlights().size(); p++) {
                    ArrayList<EventRecord> conflictsRecords = new ArrayList<>(); // store all the conflicted records
                    conflictsRecords = isConflict(NE.get(i).getReservation().getFlights().get(p));
                    Collections.sort(conflictsRecords); // sort conflict records by timestamp and client name

//                    System.out.println("???????altogether " + conflictsRecords.size() + " conflicted events");
//                    System.out.println("!!!!!!!now conflicted records are");
//                    for (int j = 0; j < conflictsRecords.size(); j++) {
//                        System.out.println(conflictsRecords.get(j).getReservation().flatten());
//                    }

                    // no conflict
                    if (conflictsRecords.size() == 0) break;
                    // there is conflict
                    for (int j = conflictsRecords.size() - 1; j >= 2; j--) {
                        // when new record is to delete
                        if (conflictsRecords.get(j) == NE.get(i)) delNew = true;
                    }
                }

                // when new record should be deleted
                if (delNew) {

//                    System.out.println("[test] now new record should be deleted");

                    this.siteTimeStamp += 1;
                    EventRecord recToDelete = new EventRecord("delete", this.siteId, this.siteTimeStamp, NE.get(i).getReservation());
                    this.log.add(recToDelete);
                    this.timeTable[siteIdToIdx(this.siteId)][siteIdToIdx(this.siteId)] = this.siteTimeStamp;
                }
                // when new record should be kept, delete all conflicted records
                else {

//                    System.out.println("[test] now local record should be deleted");

                    this.siteTimeStamp += 1;
                    for (int p = 0; p < NE.get(i).getReservation().getFlights().size(); p++) {
                        ArrayList<EventRecord> conflictsRecords = new ArrayList<>(); // store all the conflicted records
                        conflictsRecords = isConflict(NE.get(i).getReservation().getFlights().get(p));
                        // no conflicts
                        if (conflictsRecords.size() == 0) continue;
                        Collections.sort(conflictsRecords); // sort conflict records by timestamp and client name

                        // delete the one or more last conflicted records
                        for (int j = conflictsRecords.size() - 1; j >= 2; j--) {
                            EventRecord recToDelete = new EventRecord("delete", this.siteId, this.siteTimeStamp, conflictsRecords.get(j).getReservation());
                            this.log.add(recToDelete);
                            this.timeTable[siteIdToIdx(this.siteId)][siteIdToIdx(this.siteId)] = this.siteTimeStamp;
                            this.dict.remove(recToDelete.getReservation());

//                            System.out.println("<Update Cancel> Reservation record for: " + conflictsRecords.get(j).getReservation().getClientName() + " canceled.");
                        }
                    }

                    // add new record into dictionary
                    this.dict.add(NE.get(i).getReservation());
                }
//                else if (conflictsRecords.get(0) != NE.get(i)) {
//                    this.siteTimeStamp += 1;
//                    EventRecord deleteCurRec = new EventRecord("delete", this.siteId, this.siteTimeStamp, NE.get(i).getReservation());
//                    this.log.add(deleteCurRec);
//                    this.dict.remove(NE.get(i).getReservation());
//                    this.timeTable[siteIdToIdx(this.siteId)][siteIdToIdx(this.siteId)] = this.siteTimeStamp;
//
//                    System.out.println("<Update Cancel(new)> Reservation record for: " + NE.get(i).getReservation().getClientName() + " canceled.");
//                }
//                // delete local record
//                else {
//                    this.siteTimeStamp += 1;
//                    EventRecord deleteLocalRec = new EventRecord("delete", this.siteId, this.siteTimeStamp, conflictsRecords.get(1).getReservation());
//                    this.log.add(deleteLocalRec);
//                    this.timeTable[siteIdToIdx(this.siteId)][siteIdToIdx(this.siteId)] = this.siteTimeStamp;
//                    for (int j = 1; j < conflictsRecords.size(); j++) {
//                        this.dict.remove(conflictsRecords.get(j).getReservation());
//                    }
//                    this.dict.add(NE.get(i).getReservation());
//
//
//                    System.out.println("<Update Cancel(local)> Reservation record for: " + conflictsRecords.get(1).getReservation().getClientName() + " canceled.");
//
            }
        }

        // status change
        for (int i = 0; i < this.log.size(); i++) {
            EventRecord targetRecord = this.log.get(i);
            Integer targetColumn = siteIdToIdx(targetRecord.getSiteId());
            Integer targetTimestamp = targetRecord.getSiteTimestamp();
            int row = 0;
            for (; row < siteNum; row++) {
                if (this.timeTable[row][targetColumn] < targetTimestamp) break;
            }
            // when all other sites know about this event, change status to confirmed
            if (row == siteNum) {
                for (int j = 0; j < this.dict.size(); j++) {
                    if (this.dict.get(j).equals(targetRecord.getReservation())) {
                        dict.get(j).setStatus("confirmed");
                    }
                }
            }
        }

        // truncate log
        for (int i = 0; i < this.log.size(); i++) {
            EventRecord curRec = log.get(i);
            int flag = 0; // to keep flag
            for (int j = 0; j < sitesInfo.size(); j++) {
                if (!hasRec(curRec, sitesInfo.get(j).get("siteId"))) flag = 1;
            }
            // to delete
            if (flag == 0) {
//                System.out.println("[test] now truncating log for event record: " + curRec.flatten());

                log.remove(curRec);
            }
        }

        // backup for site recovery
        this.record();
    }

    public void printLog() {//private ArrayList<EventRecord> log;
        for (int i = 0; i < log.size(); i++) {
            EventRecord cur = log.get(i);
            if (cur.getOperation().equals("delete")) {
                System.out.println(cur.getOperation() + " " +
                        cur.getReservation().getClientName());
            } else {
                System.out.println(cur.getOperation() + " " +
                        cur.getReservation().getClientName() + " "
                        + cur.getReservation().printFlight());
            }
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
