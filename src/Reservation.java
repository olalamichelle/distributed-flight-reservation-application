import java.util.ArrayList;

public class Reservation {
    private String clientName;
    private ArrayList<Integer> flights;
    private String status;

    Reservation(String clientName, String status, ArrayList<Integer> flights) {
        this.clientName = clientName;
        this.status = status;
        this.flights = flights;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public ArrayList<Integer> getFlights() {
        return flights;
    }

    public void setFlights(ArrayList<Integer> flights) {
        this.flights = flights;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String printFlight() {
        if (flights.isEmpty()) return "";
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < flights.size(); i++) {
            if (i != flights.size() - 1) res.append(flights.get(i)).append(",");
            res.append(flights.get(i));
        }
        return res.toString();
    }
}
