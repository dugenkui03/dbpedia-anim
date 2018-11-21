package util;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;


/**
 * ����ƥ��ʱ��������֪ʶ�������
 *
 * @author ���޿�
 * @date 2018/10/29
 */
public class DataAnalyze {
    static OWLModel owlModel;

    static {
        try {
            owlModel = ProtegeOWL.createJenaOWLModelFromURI("file:///c:/ontologyOWL/dbpediaschema/dbpedia_2014.owl");
        } catch (OntologyLoadException e) {
            e.printStackTrace();
        }
    }

    static void func(String clzName) {
        OWLNamedClass entityClass = owlModel.getOWLNamedClass(clzName);
        System.out.println(clzName);
        System.out.println("����������\t" + entityClass.getSubclasses(true).size());
        System.out.println("ʵ��������\t" + entityClass.getInstanceCount(true));
        System.out.println();
    }

    public static void main(String[] args) throws OntologyLoadException, InterruptedException {

        //fixme ��ȡĳ���༰�������������ʵ����������������
        String[] clzNameArr = {"Altitude"};
//                {"BackgroundPicture"};
//                {"p1:Abstract"};
//                {"p2:Action","p5:ColorAndLight","p6:cartoon","p6:deform","p6:shape","p6:subject","p7:NewLight",
//                                "p8:Distence","p8:Event","p9:newFirework","p10:waterWave","p11:newFire","p12:Interaction",
//                                "p12:Camera","p14:AU","p14:FacilaExpression"};
        for (String ele : clzNameArr) {
            func(ele);
        }
    }
}