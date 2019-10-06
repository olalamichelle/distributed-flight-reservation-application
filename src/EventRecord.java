public class EventRecord implements Comparable<EventRecord> {
    private Reservation reservation;
    private String operation;
    private String siteId;
    private int siteTimestamp;

    public EventRecord(String operation, String site_id, int site_timestamp, Reservation reservation) {
        this.setOperation(operation);
        this.setSiteId(site_id);
        this.setSiteTimestamp(site_timestamp);
        this.reservation = reservation;
    }

    // sort from small to large
    @Override
    public int compareTo(EventRecord r) {
        int Timecmp = this.siteTimestamp - r.getSiteTimestamp();
        return Timecmp != 0 ? Timecmp : this.reservation.getClientName().compareTo(r.reservation.getClientName());
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


