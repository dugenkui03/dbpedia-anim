//package tdb;
//
//import cons.Constants;
//import org.apache.jena.query.*;
//import org.apache.jena.rdf.model.Model;
//import org.apache.jena.tdb.TDBFactory;
//
///**
// * @author ¶ÅôÞ¿ý
// * @date 2019/1/14
// */
//public class TDBDemo {
//    public static void main(String[] args) {
//        Dataset dataset=TDBFactory.createDataset(Constants.LOD_TDB_DIR);
//        dataset.begin(ReadWrite.READ);
//
//        Query query=QueryFactory.create("select * where {?obj <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/Person>}");
//
//        QueryExecution queryExecution=QueryExecutionFactory.create(query,dataset);
//
//        ResultSet resultSet=queryExecution.execSelect();
//
//        System.out.println(resultSet.getRowNumber());
//        int count=0;
//        while (resultSet.hasNext()){
//            QuerySolution sol = resultSet.next();
////            System.out.println(sol.get("obj").toString());
//            count++;
//        }
//
//        System.out.println(count);
//
//        queryExecution.close();
//        dataset.end();
//
//    }
//}
