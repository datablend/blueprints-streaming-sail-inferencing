package be.datablend.streaming.sail.inferencing;

import com.tinkerpop.blueprints.pgm.TransactionalGraph;
import com.tinkerpop.blueprints.pgm.impls.neo4j.Neo4jGraph;
import com.tinkerpop.blueprints.pgm.oupls.sail.GraphSail;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.sail.*;
import org.openrdf.sail.inferencer.fc.ForwardChainingRDFSInferencer;

import java.util.Scanner;

/**
 * User: dsuvee
 * Date: 20/11/11
 */
public class InferenceLoop {

    private Neo4jGraph neograph;
    private NotifyingSail sail;
    private NotifyingSailConnection connection;

    // Setup the Foward chaing RDFS inferencer
    public InferenceLoop() throws SailException {
        neograph = new Neo4jGraph("var/rdf");
        neograph.setMaxBufferSize(0);
        neograph.getRawGraph().registerTransactionEventHandler(new PushTransactionEventHandler());
        sail = new ForwardChainingRDFSInferencer(new GraphSail(neograph));
        sail.initialize();
        connection = sail.getConnection();
        neograph.stopTransaction(TransactionalGraph.Conclusion.SUCCESS);
    }

    // Add the inference
    public void inference(URI subject, URI predicate, URI object) throws SailException, InterruptedException {
        neograph.startTransaction();
        connection.addStatement(subject, predicate, object);
        connection.commit();
        neograph.stopTransaction(TransactionalGraph.Conclusion.SUCCESS);
    }

    // Parses and add the RDF statement accordingly
    public void inference(String statement) throws SailException, InterruptedException {
        String[] triple = statement.split(" ");
        inference(new URIImpl(triple[0]), new URIImpl(triple[1]), new URIImpl(triple[2]));
    }

    /* Try it out adding the following statements:
   *
   * http://datablend.be/example/teaches http://www.w3.org/2000/01/rdf-schema#domain http://datablend.be/example/teacher
   * http://datablend.be/example/teaches http://www.w3.org/2000/01/rdf-schema#range http://datablend.be/example/student
   * http://datablend.be/example/Davy http://datablend.be/example/teaches http://datablend.be/example/Bob
   *
   * http://datablend.be/example/teacher http://www.w3.org/2000/01/rdf-schema#subClassOf http://datablend.be/example/person
   * http://datablend.be/example/student http://www.w3.org/2000/01/rdf-schema#subClassOf http://datablend.be/example/person
   *
   * http://datablend.be/example/Bob http://datablend.be/example/teaches http://datablend.be/example/Davy
   *
   * */
    public static void main(String[] args) throws SailException, InterruptedException {
        InferenceLoop loop = new InferenceLoop();
        Scanner in = new Scanner(System.in);
        while (true) {
            System.out.println("Provide RDF statement:");
            System.out.print("=> ");
            String input = in.nextLine();
            System.out.println("The following edges were created:");
            loop.inference(input);
        }
    }

}
