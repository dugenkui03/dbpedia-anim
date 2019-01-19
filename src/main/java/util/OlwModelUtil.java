package util;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.model.OWLModel;

/**
 * @author ¶ÅôÞ¿ý
 * @date 2019/1/17
 */
public class OlwModelUtil {


    public static OWLModel getProtegeXModel(String modelPath) throws OntologyLoadException {
        OWLModel owlModel =ProtegeOWL.createJenaOWLModelFromURI(modelPath);
        return owlModel;
    }

}
