import java.io.Serializable;
import java.util.ArrayList;

public class CommunicateInfo implements Serializable {
    private static final long serialVersionUID = -6101917866430318484L;
    private ArrayList<EventRecord> eventRecords;
    private Integer[][] timeTable;
    private boolean smallFlag;// true if it is smallsend
    private Integer[] timeRow;// for smallSend

    public CommunicateInfo(ArrayList<EventRecord> sendEventRecords, Integer[][] timeTable,
                           Integer[] row, boolean type) {
        this.eventRecords = sendEventRecords;
        this.timeTable = timeTable;
        this.timeRow = row;
        this.smallFlag = type;
    }

    public ArrayList<EventRecord> getEventRecords() {
        return this.eventRecords;
    }

    public void setEventRecords(ArrayList<EventRecord> sendEventRecords) {
        this.eventRecords = sendEventRecords;
    }

    public Integer[][] getTimeTable() {
        return this.timeTable;
    }

    public void setTimeTable(Integer[][] timeTable) {
        this.timeTable = timeTable;
    }

    public boolean getType() {
        return this.smallFlag;
    }

    public Integer[] getSmallSend() {
        return this.timeRow;
    }
}
