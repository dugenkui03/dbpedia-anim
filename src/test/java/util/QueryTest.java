package util;

import examples.ExampleARQ_01;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.util.FileManager;

import java.io.*;

/**
 * @author ¶ÅôÞ¿ý
 * @date 2018/10/13
 */
public class QueryTest {
    public static void main(String[] args) throws IOException{
        {
            FileManager.get().addLocatorClassLoader(ExampleARQ_01.class.getClassLoader());
            Model model = FileManager.get().loadModel("dbpedia_2014.owl");

            String queryString =
                    "select * where { " +
                            "<http://www.owl-ontologies.com/Ontology1290308675.owl#M_boy.ma> ?b ?c." +
                            "}";
            Query query = QueryFactory.create(queryString);
            QueryExecution qexec = QueryExecutionFactory.create(query, model);
            try {
                ResultSet results = qexec.execSelect();
                while ( results.hasNext() ) {
                    QuerySolution querySolution = results.next();
//                    String a = querySolution.get("a").toString();
                    String b = querySolution.get("b").toString();
                    String c = querySolution.get("c").toString();
                    System.out.println("\t"+b+"\t"+c+"\n");
                }
            } finally {
                qexec.close();
            }

        }
    }
}
