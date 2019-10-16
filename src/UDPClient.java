import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPClient extends Thread{
    private DatagramSocket socket;
    private boolean running;
    private byte[] buffer = new byte[65535];

    public UDPClient(DatagramSocket udpSocket) {
        this.socket = udpSocket;
        this.running = true;
    }

    public void run() {
        DatagramPacket packet = null;
        while (this.running) {
            packet = new DatagramPacket(this.buffer, this.buffer.length);
            try {
                socket.receive(packet);// blocks until a msg arrives
                System.out.println("Got sth!");
            } catch (IOException e) {
                e.printStackTrace();
            }
            CommunicateInfo whatIGot = null;
            try {
                whatIGot = (CommunicateInfo) deserialize(packet.getData());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            String senderIp = packet.getAddress().getHostAddress();
            System.out.println("successfully received from " + senderIp);
            assert whatIGot != null;
            Integer[][] timetable = whatIGot.getTimeTable();
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    System.out.print(timetable[i][j] + " ");
                }
                System.out.println("%n");
            }
            this.buffer = new byte[65535];
        }
        socket.close();
    }


    public static Object deserialize(byte[] buffer) throws IOException, ClassNotFoundException{
        ByteArrayInputStream byteStream = new ByteArrayInputStream(buffer);
        ObjectInputStream objStream = new ObjectInputStream(byteStream);
        return objStream.readObject();
    }
}
