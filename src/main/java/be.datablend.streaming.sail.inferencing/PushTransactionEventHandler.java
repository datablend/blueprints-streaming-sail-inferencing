package be.datablend.streaming.sail.inferencing;

import be.datablend.streaming.sail.gephi.PushUtility;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.event.TransactionData;
import org.neo4j.graphdb.event.TransactionEventHandler;

/**
 * User: dsuvee
 * Date: 20/11/11
 */
public class PushTransactionEventHandler implements TransactionEventHandler<Object> {

    private int id = 1;

    public void afterCommit(TransactionData transactionData, Object o) {
        // Retrieve the created relationships. (The relevant nodes will be retrieved through these relationships)
        Iterable<Relationship> relationships = transactionData.createdRelationships();

        // Iterate and add
        for (Relationship relationship : relationships) {
            // Retrieve the labels
            String start = (String)relationship.getStartNode().getProperty("value");
            String end = (String)relationship.getEndNode().getProperty("value");
            String predicate = relationship.getType().toString();

            // Limit the relationships that are shown to our own domain
            if (!start.startsWith("http://www.w3.org") && !end.startsWith("http://www.w3.org")) {
                // Check whether the relationship is inferred or not
                boolean inferred = (Boolean)relationship.getProperty("inferred",false);
                // Retrieve the more meaningful names
                start = getName(start);
                end = getName(end);
                predicate = getName(predicate);
                // Push the start and end nodes (they will only be created once)
                PushUtility.pushNode(start);
                PushUtility.pushNode(end);
                PushUtility.pushEdge(id++, start, end, predicate, inferred);
            }
        }
    }

    // Helper method to extract the name without the namespace
    private String getName(String uri) {
        return uri.substring(uri.lastIndexOf("/")+1);
    }

    public Object beforeCommit(TransactionData transactionData) throws Exception {
        return null;
    }

    public void afterRollback(TransactionData transactionData, Object o) {
    }

}
