package plot;

/**
 * @author 杜艮魁
 * @date 2018/9/27
 */
/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;
import org.apache.jena.util.FileManager;

/**
 * 使用数据源url和query对象构造QueryExecution类子类QueryEngineHTTP对象，使用他的对象执行查询
 */
public class ExampleARQ_03 {

    public static void main(String[] args) {
        FileManager.get().addLocatorClassLoader(ExampleARQ_03.class.getClassLoader());
        String apikey = System.getenv("KASABI_API_KEY");

        String queryString =
                "SELECT ?o2 ?p2 WHERE {" +
                        " ?o ?pp <http://dbpedia.org/resource/Ernest_Hemingway>;" +
                        "  ?o1 ?p1;" +
                        "  ?o2 ?p2." +
                        "} limit 10";
//                "SELECT ?pred ?obj WHERE {" +
//                        " <http://dbpedia.org/ontology/Company> ?pred ?obj ." +
//                        "} limit 10";h
        Query query = QueryFactory.create(queryString);
        //使用数据源url和query对象构造QueryExecution类子类QueryEngineHTTP对象，使用他的对象执行查询
        QueryEngineHTTP qexec = (QueryEngineHTTP)QueryExecutionFactory.createServiceRequest("http://dbpedia.org/sparql", query);
        qexec.addParam("apikey", apikey);
        try {
            //执行查询并返回结果
            ResultSet results = qexec.execSelect();
            while ( results.hasNext() ) {
                QuerySolution soln = results.nextSolution();
                Resource region = soln.getResource("?p2");
                System.out.println(region.getURI());
            }
        } finally {
            qexec.close();
        }
    }

}