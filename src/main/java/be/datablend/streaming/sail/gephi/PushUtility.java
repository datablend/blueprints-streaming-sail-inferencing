package be.datablend.streaming.sail.gephi;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * User: dsuvee
 * Date: 20/11/11
 */
public class PushUtility {

    private static final String url = "http://localhost:8080/workspace0?operation=updateGraph";
    private static final String nodejson = "{\"an\":{\"%1$s\":{\"label\":\"%1$s\"}}}";
    private static final String edgejson = "{\"ae\":{\"%1$d\":{\"source\":\"%2$s\",\"target\":\"%3$s\",\"directed\":true,\"label\":\"%4$s\",\"inferred\":\"%5$b\"}}}";

    private static void push(String message) {
        try {
            // Create a connection and push the node or edge json message
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.getOutputStream().write(message.getBytes("UTF-8"));
            con.getInputStream();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    // Pushes a node
    public static void pushNode(String label) {
        push(String.format(nodejson, label));
    }

    // Pushes an edge
    public static void pushEdge(int id, String source, String target, String label, boolean inferred) {
        push(String.format(edgejson, id, source, target, label, inferred));
        System.out.println(String.format(edgejson, id, source, target, label, inferred));
    }

}
