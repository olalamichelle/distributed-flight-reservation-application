public class EventRecord {
    private Reservation reservation;
    private String operation;
    private String siteId;
    private int siteTimestamp;

    public EventRecord(String operation, String site_id, int site_timestamp) {
        this.setOperation(operation);
        this.setSiteId(site_id);
        this.setSiteTimestamp(site_timestamp);
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public int getSiteTimestamp() {
        return siteTimestamp;
    }

    public void setSiteTimestamp(int siteTimestamp) {
        this.siteTimestamp = siteTimestamp;
    }
}


