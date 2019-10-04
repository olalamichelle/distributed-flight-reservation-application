import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Host {
    public static void main(String[] args) {

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

            Integer siteNum = allSiteId.size();
            // store info into a hashmap, property -> info, arranged by index of each site
            ArrayList<HashMap<String, String>> sitesInfo = new ArrayList<>();
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

                System.out.println("udp start port: " + udpStartPort);
                System.out.println("udp end port: " + udpEndPort);
                System.out.println("ip address: " + ipAddr);

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

            for (int i = 0; i < sitesInfo.size(); i++) {
                HashMap<String,String> curMap = sitesInfo.get(i);
//                System.out.println("siteId: " + curMap.get("siteId") + " siteIndex: " + i);
            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        // create another thread to keep receiving msgs from other sites


        // main thread keeps receiving msgs from user at this site

    }
}
