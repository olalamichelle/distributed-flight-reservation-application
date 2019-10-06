import java.util.ArrayList;

public class CommunicateInfo {
    private ArrayList<EventRecord> eventRecords;
    private Integer timeTable[][];

    public CommunicateInfo(ArrayList<EventRecord> sendEventRecords, Integer[][] timeTable) {
        this.eventRecords = eventRecords;
        this.timeTable = timeTable;
    }

    public ArrayList<EventRecord> getEventRecords() {
        return eventRecords;
    }

    public void setEventRecords(ArrayList<EventRecord> sendEventRecords) {
        this.eventRecords = sendEventRecords;
    }

    public Integer[][] getTimeTable() {
        return timeTable;
    }

    public void setTimeTable(Integer[][] timeTable) {
        this.timeTable = timeTable;
    }
}
