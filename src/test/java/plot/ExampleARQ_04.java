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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.query.ARQ;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.FileManager;

/**
 *  数据源url放在查询语句中，使用默认Model对象——创建QueryExecution对象
 */
public class ExampleARQ_04 {

    public static void main(String[] args) {
        FileManager.get().addLocatorClassLoader(ExampleARQ_04.class.getClassLoader());
        String uri = "http://dbpedia.org/sparql";
        String apikey = System.getenv("KASABI_API_KEY");//虽然系统没有定义这个环境变量，但是似乎没有问题

        String queryString =
//                "SELECT * WHERE { " +
//                        "	SERVICE <" + uri + "> {?s ?pred <http://dbpedia.org/resource/Ernest_Hemingway>. }" +
//                        "} limit 10";
                "SELECT * WHERE { " +
                        "	SERVICE <" + uri + "> { ?s ?pred <http://dbpedia.org/resource/Ernest_Hemingway> . }" +
                        "} limit 10";
        Query query = QueryFactory.create(queryString);
        //数据源url放在查询语句中，使用默认Model对象——创建QueryExecution对象
        QueryExecution qexec = QueryExecutionFactory.create(query, ModelFactory.createDefaultModel());

        // Set additional parameters on a per SERVICE basis, see also: JENA-195
        //为执行器添加额外的参数，下边的注视了也可以运行
        Map<String, Map<String,List<String>>> serviceParams = new HashMap<String, Map<String,List<String>>>();
        Map<String,List<String>> params = new HashMap<String,List<String>>();
        List<String> values = new ArrayList<String>();

        values.add(apikey);
        params.put("apikey", values);
        serviceParams.put(uri, params);
        qexec.getContext().set(ARQ.serviceParams, serviceParams);

        try {
            ResultSet results = qexec.execSelect();
            while ( results.hasNext() ) {
                QuerySolution soln = results.nextSolution();
                Resource region = soln.getResource("pred");
                System.out.println(region.getURI());
            }
        } finally {
            qexec.close();
        }
    }

}
