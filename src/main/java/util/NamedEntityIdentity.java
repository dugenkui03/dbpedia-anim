package util;

import com.google.common.base.Strings;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;
import org.apache.jena.util.FileManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 杜艮魁
 * @date 2019/1/15
 */
public class NamedEntityIdentity {

    /**
     * @param wordList
     * @return
     */
    public static Map<String, String> findEntities(List<String> wordList) {
        Map<String, String> res = new HashMap<>();

        for (String word : wordList) {
            String entity=findEntity(word.split(";")[0]);

            if(entity==null||entity.length()==0){
                entity=findEntity(word.split(";")[1]);
            }

            res.put(word,entity);
        }

        return res;
    }

    public static String findEntity(String word){
        String entityUri="";
        String queryStr =
                "select ?s\n" +
                        " where{\n" +
                        "?someobj ?p ?s .\n" +
                        "?s <http://www.w3.org/2000/01/rdf-schema#label> ?labelx .\n" +
                        "?labelx <bif:contains> \'\"" + word.split(";")[0] + "\"\' .\n" +
                        "FILTER REGEX(STR(?s),\"http://dbpedia.org/\")\n"+
                        "}group by ?s order by desc(count(?s)) limit 1";

        System.out.println(queryStr);
        Query query = QueryFactory.create(queryStr);
        QueryEngineHTTP qexec = (QueryEngineHTTP) QueryExecutionFactory.createServiceRequest("https://dbpedia.org/sparql", query);

        ResultSet resultSet = qexec.execSelect();
        while (resultSet.hasNext()) {
            QuerySolution querySolution = resultSet.next();
            entityUri=querySolution.get("s").toString();
        }
        qexec.close();

        return entityUri;
    }

    public static void main(String[] args) {
        List<String> list=Arrays.asList("吃下下下下;eat");
        findEntities(list);
    }
}
