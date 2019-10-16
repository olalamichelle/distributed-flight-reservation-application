import java.io.Serializable;

public class EventRecord implements Comparable<EventRecord>, Serializable {
    private static final long serialVersionUID = 2946811424018097277L;
    private Reservation reservation;
    private String operation;
    private String siteId;
    private int siteTimestamp;

    // CONSTRUCTOR
    public EventRecord(String operation, String site_id, int site_timestamp, Reservation reservation) {
        this.setOperation(operation);
        this.setSiteId(site_id);
        this.setSiteTimestamp(site_timestamp);
        this.reservation = reservation;
    }

    // GETTER and SETTER
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


    // HELPERS
    /**
     * flatten object into a format of: "clientName flightsNumber status operation siteId siteTimestamp"
     */
    public String flatten() {
        StringBuilder builder = new StringBuilder();
        // reservation
        builder.append(this.reservation.flatten());
        // operation
        builder.append(this.operation);
        builder.append(" ");
        // siteId
        builder.append(this.siteId);
        builder.append(" ");
        // siteTimestamp
        builder.append(this.siteTimestamp);
        return builder.toString();
    }

    /**
     * helper function to sort event record by its timestamp and client name
     * @param r
     * @return
     */
    @Override
    public int compareTo(EventRecord r) {
        int Timecmp = this.siteTimestamp - r.getSiteTimestamp();
        return Timecmp != 0 ? Timecmp : this.reservation.getClientName().compareTo(r.reservation.getClientName());
    }

}


