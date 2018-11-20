package examples;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.util.FileManager;

/**
 * @author ¶ÅôÞ¿ý
 * @date 2018/10/29
 */
public class ExampleForAnimBase {
    public static void main(String[] args) {
        FileManager.get().addLocatorClassLoader(ExampleARQ_02.class.getClassLoader());
        Model model = FileManager.get().loadModel("sumo_phone3.owl");

        String queryString = "select ?x " +
                "where{" +
                "   ?x <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?t1 ." +
                "   ?t1 <http://www.w3.org/2000/01/rdf-schema#subClassOf> ?t2 ." +
                "   ?t2 <http://www.w3.org/2000/01/rdf-schema#subClassOf> ?t3 ." +
                "   ?t3 <http://www.w3.org/2000/01/rdf-schema#subClassOf> ?t4 ." +
                "   ?t4 <http://www.w3.org/2000/01/rdf-schema#subClassOf> <http://www.ontologyportal.org/translations/SUMO.owl.txt#Entity> ." +
                "}";
        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.create(query, model);
        try {
            ResultSetRewindable results = ResultSetFactory.makeRewindable(qexec.execSelect());
            System.out.println(results.size());
        } finally {
            qexec.close();
        }
    }
}
