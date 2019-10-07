import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

public class Host {
    public static void main(String[] args) throws IOException {
        // current site
        String curSiteId = args[1];
        String curStartPort = "";
        String curEndPort = "";
        String curIp = "";

        // store info into a hashmap, property -> info, arranged by index of each site
        ArrayList<HashMap<String, String>> sitesInfo = new ArrayList<>();
        Integer siteNum = 0;

        // read host name and port number from json
        try {

            JSONParser parser = new JSONParser();
            JSONObject data = (JSONObject) parser.parse(new FileReader("src/knownhosts.json"));
            JSONObject hosts = (JSONObject) data.get("hosts");

            ArrayList<String> allSiteId = new ArrayList<>();

            // indice each siteId by siteId comparison
            hosts.keySet().forEach(siteId ->
            {
                allSiteId.add(siteId.toString());
            });
            Collections.sort(allSiteId);

            // initialze array storing all informations of all sites
            siteNum = allSiteId.size();
            for (int i = 0; i < siteNum; i++) {
                HashMap<String, String> tmp = new HashMap<>();
                sitesInfo.add(tmp);
            }

            hosts.keySet().forEach(siteId ->
            {
                JSONObject siteInfo = (JSONObject) hosts.get(siteId);
                String Id = siteId.toString();

                String udpStartPort = siteInfo.get("udp_start_port").toString();
                String udpEndPort = siteInfo.get("udp_end_port").toString();
                String ipAddr = (String) siteInfo.get("ip_address");

//                System.out.println("udp start port: " + udpStartPort);
//                System.out.println("udp end port: " + udpEndPort);
//                System.out.println("ip address: " + ipAddr);

                Integer siteIndex = 0;
                for (int i = 0; i < allSiteId.size(); i++) {
                    if (allSiteId.get(i).equals(Id)) siteIndex = i;
                }

                HashMap<String, String> tmp = new HashMap<>();
                tmp.put("startPort", udpStartPort);
                tmp.put("endPort", udpEndPort);
                tmp.put("ip", ipAddr);
                tmp.put("siteId", siteId.toString());

                sitesInfo.set(siteIndex, tmp);
//                System.out.println("key: "+ siteId + " value: " + siteInfo);
            });

//            for (int i = 0; i < sitesInfo.size(); i++) {
//                HashMap<String,String> curMap = sitesInfo.get(i);
//                System.out.println("siteId: " + curMap.get("siteId") + " siteIndex: " + i);
//            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        // find current site info
        for (int i = 0; i < sitesInfo.size(); i++) {
            if (sitesInfo.get(i).get("siteId").equals(curSiteId)) {
                HashMap<String, String> curMap = sitesInfo.get(i);
                curStartPort = curMap.get("startPort");
                curEndPort = curMap.get("endPort");
                curIp = curMap.get("ip");
            }
        }

        // Construct current site
        ReservationSys mySite = new ReservationSys(sitesInfo, siteNum, curSiteId);

        // TODO: Crash and recover here


        // Start port is for listening
        // End port is for send
        // Create receive socket by start port number
        DatagramSocket receiveSocket = new DatagramSocket(null);
        InetSocketAddress receiveAddress = new InetSocketAddress(curIp, Integer.parseInt(curStartPort));
        receiveSocket.bind(receiveAddress);
        new Listener(receiveSocket, sitesInfo, mySite).start();// child thread go here

        // Create send socket by end port number
        DatagramSocket sendSocket = new DatagramSocket(null);
        InetSocketAddress sendAddress = new InetSocketAddress(curIp, Integer.parseInt(curEndPort));
        sendSocket.bind(sendAddress);

        // main thread keeps receiving msgs from user at this site
        while (true) {
            System.out.println("Please enter the command: ");
            Scanner in = new Scanner(System.in);
            String commandLine = in.nextLine();
            System.out.println("User input: " + commandLine);
            String[] input = commandLine.split("\\s+");

            if (input[0].equals("reserve")) {
                String client = input[1];
                String[] flights = input[2].split(",");
                // TODO: Reserve

            } else if (input[0].equals("cancel")) {
                String client = input[1];
                // TODO: Cancel

            } else if (input[0].equals("view")) {// Print dictionary here
                mySite.printDictionary();

            } else if (input[0].equals("log")) {// Print log here
                mySite.printLog();

            } else if (input[0].equals("send")) {// Send log to a specific site
                String recipient = input[1];
                ArrayList<String> recipients = new ArrayList<>();
                recipients.add(recipient);
                sendMsgToOthers(mySite, sendSocket, sitesInfo, recipients);

            } else if (input[0].equals("sendall")) {// Send log to all sites
                ArrayList<String> recipients = new ArrayList<>();
                for (int i = 0; i < sitesInfo.size(); i++) {
                    if (sitesInfo.get(i).get("siteId").equals(curSiteId)) continue;
                    recipients.add(sitesInfo.get(i).get("siteId"));
                }
                sendMsgToOthers(mySite, sendSocket, sitesInfo, recipients);


            } else if (input[0].equals("clock")) {// Print the matrix clock
                mySite.printClock();

            } else if (input[0].equals("smallsend")) {
                // Implement later
                System.out.println("Why you do this to me??");

            } else if (input[0].equals("smallsendall")) {
                // Implement later
                System.out.println("I have no idea what's going on here.");

            } else if (input[0].equals("quit")) {
                System.exit(0);

            } else {
                System.out.println("Oops, something is going wrong here!");
            }
        }
    }

    // TODO
    // Identify the the necessary partial log for sending to each target site
    public static CommunicateInfo buildMsg(ReservationSys mySite, String targetSite, ArrayList<HashMap<String, String>> sitesInfo) {
        // TODO: check what is target site knows about the other sites and truncate the log
        CommunicateInfo res = new CommunicateInfo();

        return res;
    }

    // Serialize the CommunicateInfo to byte array
    public static byte[] serialize(Object obj) throws IOException {
        try(ByteArrayOutputStream b = new ByteArrayOutputStream()){
            try(ObjectOutputStream o = new ObjectOutputStream(b)){
                o.writeObject(obj);
            }
            return b.toByteArray();
        }
    }

    // Only send the log that is identified as necessary
    public static void sendMsgToOthers(ReservationSys mySite, DatagramSocket sendSocket,
                                ArrayList<HashMap<String, String>> sitesInfo,
                                ArrayList<String> recipients) throws IOException {
        for (int i = 0; i < recipients.size(); i++) {
            String ipAddress = null;
            String receivePort = null;
            for (int j = 0; j < sitesInfo.size(); j++) {
                if (sitesInfo.get(j).get("siteId").equals(recipients.get(i))) {
                    ipAddress = sitesInfo.get(j).get("ip");
                    receivePort = sitesInfo.get(j).get("startPort");
                    break;
                }
            }
            InetAddress targetIP = InetAddress.getByName(ipAddress);
            byte[] sendArray = serialize(buildMsg(mySite, recipients.get(i), sitesInfo));
            DatagramPacket sendPacket = new DatagramPacket(sendArray, sendArray.length, targetIP, Integer.parseInt(receivePort));
            sendSocket.send(sendPacket);
        }
    }

}
