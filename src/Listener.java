import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/*
The implementation of UDP listener
Listen the msgs from other sites
 */
public class Listener extends Thread {
    private DatagramSocket socket;
    private boolean running;
    private byte[] buffer = new byte[1024];

    public Listener() {

    }

    public void run() {
        running = true;

        while (running) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            try {
                socket.receive(packet);// blocks until a msg arrives
            } catch (IOException e) {
                e.printStackTrace();
            }

            InetAddress address = packet.getAddress();
            int port = packet.getPort();
            packet = new DatagramPacket(buffer, buffer.length, address, port);
            // get the new class

        }
        socket.close();
    }
    // Deserialize the byte array and reconstruct the object
    /*
    public deserialize() {
        ByteArrayInputStream bis = new ByteArrayInputStream(this.buffer);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            Object o = in.readObject();

        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }
    }
    */
}
