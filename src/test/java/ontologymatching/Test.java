package ontologymatching;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;

/**
 * @author ¶ÅôÞ¿ý
 * @date 2018/11/21
 */
public class Test {
    public static void main(String[] args) {
        OWLModel dbpediaOwlModel = OMUtils.getOwlModel("file:///c:/ontologyOWL/dbpediaschema/dbpedia_2014.owl");
        OWLNamedClass newClz = dbpediaOwlModel.createOWLNamedClass("http://www.owl-ontologies.com/Ontology1290308675.owl#SmileScene");
        newClz.createOWLIndividual("http://dbpedia.org/resource/Smile");

        OMUtils.saveOwlModel2File(dbpediaOwlModel);
    }
}
