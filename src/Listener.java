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
    private String listenTo;// the site ID this socket listens to
    private boolean running;
    private byte[] buffer = new byte[65535];

    public Listener(DatagramSocket updSocket,  ArrayList<HashMap<String, String>> sitesInfo, ReservationSys mySite, String listenTo) {
        this.socket = updSocket;
        this.listenTo = listenTo;
        running = false;
    }

    public void run(ArrayList<HashMap<String, String>> sitesInfo, ReservationSys mySite) throws IOException, ClassNotFoundException {
        running = true;
        DatagramPacket packet = null;
        while (running) {
            packet = new DatagramPacket(buffer, buffer.length);
            try {
                socket.receive(packet);// blocks until a msg arrives
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("Got something from site " + listenTo);
            // Update the current site based on the received information
            mySite.update((CommunicateInfo) deserialize(packet.getData()), listenTo);
            // TODO: send received confirmation?

            buffer = new byte[65535];//reset
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