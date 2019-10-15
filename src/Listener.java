import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.HashMap;

/*
The implementation of UDP listener
Listen the msgs from a specific site
 */
public class Listener extends Thread {
    private DatagramSocket socket;
    private boolean running;
    private byte[] buffer = new byte[65535];

    // CONSTRUCTOR
    public Listener(DatagramSocket updSocket,  ArrayList<HashMap<String, String>> sitesInfo, ReservationSys mySite) {
        this.socket = updSocket;
        this.running = false;
    }

    // HELPERS
    public void run(ArrayList<HashMap<String, String>> sitesInfo, ReservationSys mySite) throws IOException, ClassNotFoundException {
        this.running = true;
        DatagramPacket packet = null;
        while (this.running) {

            packet = new DatagramPacket(this.buffer, this.buffer.length);
            try {
                socket.receive(packet);// blocks until a msg arrives
            } catch (IOException e) {
                e.printStackTrace();
            }

            String senderIp = packet.getAddress().getHostAddress();
            String senderId = null;
            for (int i = 0; i < sitesInfo.size(); i++) {
                if (sitesInfo.get(i).get("ip").equals(senderIp)) {
                    senderId = sitesInfo.get(i).get("siteId");
                    System.out.println("Got something from site " + senderId);
                    break;
                }
            }
            System.out.println("successfully received from " + senderId);

            // Update the current site based on the received information
            mySite.update((CommunicateInfo) deserialize(packet.getData()), senderId);
            // TODO: send received confirmation?

            this.buffer = new byte[65535];//reset
        }
        socket.close();
    }

    // Deserialize the byte array and reconstruct the object
    public static Object deserialize(byte[] buffer) throws IOException, ClassNotFoundException{
        ByteArrayInputStream byteStream = new ByteArrayInputStream(buffer);
        ObjectInputStream objStream = new ObjectInputStream(byteStream);
        return objStream.readObject();
    }
}
