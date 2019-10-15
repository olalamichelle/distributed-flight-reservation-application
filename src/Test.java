import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


public class Test {

    public static void main(String[] args) {


        // TEST1: read host name and port number from json
//        try {
//
//            JSONParser parser = new JSONParser();
//            JSONObject data = (JSONObject) parser.parse(new FileReader("src/knownhosts.json"));
//            JSONObject hosts = (JSONObject) data.get("hosts");
//
//            ArrayList<String> allSiteId = new ArrayList<>();
//
//            // indice each siteId by siteId comparison
//            hosts.keySet().forEach(siteId ->
//            {
//                allSiteId.add(siteId.toString());
//            });
//            Collections.sort(allSiteId);
//
//            Integer siteNum = allSiteId.size();
//            // store info into a hashmap, property -> info, arranged by index of each site
//            ArrayList<HashMap<String, String>> Info = new ArrayList<>();
//            for (int i = 0; i < siteNum; i++) {
//                HashMap<String, String> tmp = new HashMap<>();
//                Info.add(tmp);
//            }
//
//            hosts.keySet().forEach(siteId ->
//            {
//                JSONObject siteInfo = (JSONObject) hosts.get(siteId);
//                String Id = siteId.toString();
//
//                String udpStartPort = siteInfo.get("udp_start_port").toString();
//                String udpEndPort = siteInfo.get("udp_end_port").toString();
//                String ipAddr = (String) siteInfo.get("ip_address");
//
//                System.out.println("udp start port: " + udpStartPort);
//                System.out.println("udp end port: " + udpEndPort);
//                System.out.println("ip address: " + ipAddr);
//
//                Integer siteIndex = 0;
//                for (int i = 0; i < allSiteId.size(); i++) {
//                    if (allSiteId.get(i).equals(Id)) siteIndex = i;
//                }
//
//                HashMap<String, String> tmp = new HashMap<>();
//                tmp.put("startPort", udpStartPort);
//                tmp.put("endPort", udpEndPort);
//                tmp.put("ip", ipAddr);
//                tmp.put("siteId", siteId.toString());
//
//                Info.set(siteIndex, tmp);
////                System.out.println("key: "+ siteId + " value: " + siteInfo);
//            });
//
//            for (int i = 0; i < Info.size(); i++) {
//                HashMap<String,String> curMap = Info.get(i);
//                System.out.println("siteId: " + curMap.get("siteId") + " siteIndex: " + i);
//            }
//
//        } catch (IOException | ParseException e) {
//            e.printStackTrace();
//        }

        // TEST2: Record and recover
        // Record timeTable
//        Integer[][] timeTable = new Integer[3][3];
//        for (int i = 0; i < 3; i++) {
//            for (int j = 0; j < 3; j++) {
//                timeTable[i][j] = 1;
//            }
//        }
//        StringBuilder tableBuilder = new StringBuilder();
//        for (int i = 0; i < timeTable.length; i++) {
//            for (int j = 0; j < timeTable.length; j++) {
//                tableBuilder.append(timeTable[i][j]);
//                if(j < timeTable.length - 1) {
//                    tableBuilder.append(",");
//                }
//            }
//            tableBuilder.append("\n");
//        }
//        try {
//            BufferedWriter writer = new BufferedWriter(new FileWriter("timeTable.txt"));
//            writer.write(tableBuilder.toString());
//            writer.close();
//        } catch (IOException e) {
//            System.out.println("write exception");
//        }
//
//        // Record dictionary
//        ArrayList<Reservation> dict = new ArrayList<Reservation>();
//        ArrayList<Integer> flights1 = new ArrayList<Integer>();
//        flights1.add(1);
//        flights1.add(2);
//        flights1.add(3);
//        Reservation res1 = new Reservation("abby", "pending", flights1);
//        ArrayList<Integer> flights2 = new ArrayList<Integer>();
//        flights2.add(4);
//        flights2.add(5);
//        flights2.add(6);
//        Reservation res2 = new Reservation("jade", "pending", flights2);
//        dict.add(res1);
//        dict.add(res2);
//
//        StringBuilder dictBuilder = new StringBuilder();
//        for (int i = 0; i < dict.size(); i++) {
//            dictBuilder.append(dict.get(i).flatten());
//            dictBuilder.append("\n");
//        }
//        try {
//            BufferedWriter writer = new BufferedWriter(new FileWriter("dictionary.txt"));
//            writer.write(dictBuilder.toString());
//            writer.close();
//        } catch (IOException e) {
//            System.out.println("write exception");
//        }
//
//        // Record log
//        ArrayList<EventRecord> log = new ArrayList<>();
//        EventRecord eve1 = new EventRecord("insert", "alpha", 2, res1);
//        EventRecord eve2 = new EventRecord("delete", "beta", 3, res2);
//        log.add(eve1);
//        log.add(eve2);
//
//        StringBuilder logBuilder = new StringBuilder();
//        for (int i = 0; i < log.size(); i++) {
//            logBuilder.append(log.get(i).flatten());
//            logBuilder.append("\n");
//        }
//        try {
//            BufferedWriter writer = new BufferedWriter(new FileWriter("log.txt"));
//            writer.write(logBuilder.toString());
//            writer.close();
//        } catch (IOException e) {
//            System.out.println("write exception");
//        }
//
//        // recover timeTable
//        try {
//            Integer[][] newTimeTable = new Integer[3][3];
//            BufferedReader timeReader = new BufferedReader(new FileReader("timeTable.txt"));
//            String line = "";
//            int row = 0;
//            while((line = timeReader.readLine()) != null) {
//                String[] cols = line.split(",");
//                int col = 0;
//                for(String  c : cols) {
//                    newTimeTable[row][col] = Integer.parseInt(c);
//                    col++;
//                }
//                row++;
//            }
//            timeReader.close();
//
//            System.out.println("now new timetable is:");
//            for (int i = 0; i < 3; i++) {
//                for (int j = 0; j < 3; j++) {
//                    System.out.print(newTimeTable[i][j]);
//                }
//                System.out.println();
//            }
//        } catch (IOException e) {
//            System.out.println("read time table file error");
//        }
//
//        // recover dictionary
//        try {
//            ArrayList<Reservation> newDict = new ArrayList<Reservation>();
//            BufferedReader dictReader = new BufferedReader(new FileReader("dictionary.txt"));
//            String line = "";
//            while ((line = dictReader.readLine()) != null) {
//                String[] cols = line.split(" ");
//                String[] curFlights = cols[1].split(",");
//                ArrayList<Integer> flights = new ArrayList<>();
//                for (int i = 0; i < curFlights.length; i++) {
//                    flights.add(Integer.parseInt(curFlights[i]));
//                }
//                Reservation curRes = new Reservation(cols[0], cols[2], flights);
//                newDict.add(curRes);
//            }
//
//            System.out.println("now dictionary is: ");
//            System.out.println(dict.get(0).getClientName());
//            System.out.println(dict.get(0).getStatus());
//            System.out.println(dict.get(1).getClientName());
//            System.out.println(dict.get(1).getStatus());
//        } catch (IOException e) {
//            System.out.println("read dictionary file error");
//        }

//        File tempFile = new File("timeTable.txt");
//        System.out.println("exists?" + tempFile.exists());

    }
}
