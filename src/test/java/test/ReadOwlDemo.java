package test;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ¶ÅôÞ¿ý
 * @date 2019/1/17
 */
public class ReadOwlDemo {
    public static void main(String[] args) throws OntologyLoadException {
//        OWLModel owlModel =ProtegeOWL.createJenaOWLModelFromURI("file:///C:/ontologyOWL/sumoOWL2/sumo_phone3.owl");
//        System.out.println(owlModel.getOWLNamedClass("http://www.ontologyportal.org/translations/SUMO.owl.txt#Human").getInstances());


        Map<String,String>  xx=new HashMap<>();
        xx.put("a","bb");
        xx.put("a","cc");
        System.out.println(xx);
    }
}
