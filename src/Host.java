import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

public class Host {
    public static void main(String[] args) {
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

        // Crash and recover here
        // Operate later


        DatagramSocket udpSocket;

        ArrayList<Integer> ports = new ArrayList<>();
        for (int i = Integer.parseInt(curStartPort); i <= Integer.parseInt(curEndPort); i++) {
            ports.add(i);
        }

        // create another thread to keep receiving msgs from other sites


        // main thread keeps receiving msgs from user at this site
        while (true) {
            System.out.println("Please enter the command: ");
            Scanner in = new Scanner(System.in);
            String commandLine = in.nextLine();
            System.out.println("User input " + commandLine);
            String[] input = commandLine.split("\\s+");

            if (input[0] == "reserve") {
                String client = input[1];
                String[] flights = input[2].split(",");
                // Operates here

            } else if (input[0] == "cancel") {
                String client = input[1];
                // Operates here

            } else if (input[0] == "view") {
                // Print dictionary here

            } else if (input[0] == "log") {
                // Print log here

            } else if (input[0] == "send") {
                String recipient = input[1];
                // Send log to recipient

            } else if (input[0] == "sendall") {
                // Send log to all sites

            } else if (input[0] == "clock") {
                // Print the matrix clock

            } else if (input[0] == "smallsend") {
                // Implement later
                System.out.println("Why you do this to me?");

            } else if (input[0] == "smallsendall") {
                // Implement later
                System.out.println("I have no idea what's going on here.");

            } else if (input[0] == "quit") {
                System.exit(0);

            } else {
                System.out.println("Oops, something is going wrong here!");
            }
        }
    }
}
