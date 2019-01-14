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

package plot;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.FileManager;

public class ExampleARQ_01 {

    public static void main(String[] args) {
        FileManager.get().addLocatorClassLoader(ExampleARQ_01.class.getClassLoader());
        Model model = FileManager.get().loadModel("C:\\ontologyOWL\\AllOwlFile\\sumoOWL2\\sumo_phone3.owl");

        String queryString =
        		"SELECT * WHERE { " +
        		"    ?person ?prop ?value . " +
        		"}";
        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.create(query, model);
        try {
            ResultSet results = qexec.execSelect();
            while ( results.hasNext() ) {
                QuerySolution soln = results.nextSolution();
                RDFNode region = soln.get("?person");
                RDFNode prop = soln.get("?prop");
                RDFNode value = soln.get("?value");

                System.out.println(region.toString()+"\t"+prop.toString()+"\t"+value.toString());
            }
        } finally {
            qexec.close();
        }

    }

}
