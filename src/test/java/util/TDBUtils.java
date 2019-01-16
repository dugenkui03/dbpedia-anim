package util;

import cons.Constants;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import org.apache.jena.query.*;
import org.apache.jena.tdb.TDBFactory;

/**
 * 有关TDB的一些操作
 *
 * @author 杜艮魁
 * @date 2019/1/14
 */
public class TDBUtils {


    /**
     * 将TDB库种类为dbClz的
     *
     * @param animClz
     * @param dbClz
     */
    public static void setInstance(OWLModel owlModel, OWLNamedClass animClz, String dbClz) {
        Dataset dataset = TDBFactory.createDataset(Constants.LOD_TDB_DIR);
        dataset.begin(ReadWrite.READ);
        Query query = QueryFactory.create("select * where {?obj <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <" + dbClz + ">}");
        QueryExecution queryExecution = null;
        try {
            queryExecution = QueryExecutionFactory.create(query, dataset);
            ResultSet resultSet = queryExecution.execSelect();

            System.out.println("create instance for " + animClz + "(" + dbClz + "):");
            int cou = 0;
            while (resultSet.hasNext()) {
                if (cou++ == 10) {
                    break;
                }
                QuerySolution sol = resultSet.next();
                String insUri = sol.get("obj").toString();

                OWLIndividual ins = owlModel.getOWLIndividual(insUri);
                if (ins == null) {
                    animClz.createOWLIndividual(insUri);
                } else {
                    ins.addRDFType(animClz);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            queryExecution.close();
            dataset.end();
        }

    }
}
