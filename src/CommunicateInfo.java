import java.io.Serializable;
import java.util.ArrayList;

public class CommunicateInfo implements Serializable {
    private static final long serialVersionUID = -6101917866430318484L;
    private ArrayList<EventRecord> eventRecords;
    private Integer timeTable[][];

    //-------test----------//
    // FIXME
    private String senderId;

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    // FIXME: delete senderId
    public CommunicateInfo(ArrayList<EventRecord> sendEventRecords, Integer[][] timeTable, String senderId) {
        this.eventRecords = sendEventRecords;
        this.timeTable = timeTable;
        this.senderId = senderId;
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
