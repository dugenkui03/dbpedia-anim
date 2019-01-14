package ontologymatching;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSClass;

import java.util.Collection;

/**
 * @author ���޿�
 * @date 2018/11/21
 */
public class Test {
    public static void main(String[] args) {
        OWLModel dbpediaOwlModel = OMUtils.getOwlModel("file:///E:/javaWorkspace/dbpedia-anim/knowledgebase/dbpedia_2014.owl");


        /**
         * ���ö�����ĸ���
         */
        OWLNamedClass animClz = dbpediaOwlModel.createOWLNamedClass("http://www.dugenkui.org/Human");
        OWLNamedClass dbpediaClz = dbpediaOwlModel.getOWLNamedClass("http://dbpedia.org/ontology/Person");

//        RDFSClass dbpediaSuperClz = dbpediaClz.getFirstSuperclass();
//        animClz.addSuperclass(dbpediaSuperClz);


        /**
         * ���ö�����ĵȼ���
         */
        RDFProperty equivalentClassProp = dbpediaOwlModel.getRDFProperty("http://www.w3.org/2002/07/owl#equivalentClass");
        dbpediaClz.addEquivalentClass(animClz);


        /**
         * �����ļ�
         */
        OMUtils.saveOwlModel2File(dbpediaOwlModel);
    }
}
