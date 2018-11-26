package ontologyalignment;

import com.hp.hpl.jena.util.FileUtils;
import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

/**
 * ����֪ʶ�����ӵ�DBpedia��ʱ����Ҫ��һЩ���߷���
 *
 * @author ���޿�
 * @date 2018/11/23
 */
public class OLUtils {

    /**
     * ��ȡһ��owl�ļ���Ӧ��model����
     *
     * @param kbFilePath
     * @return
     */
    public static OWLModel getOwlModel(String kbFilePath) {
        try {
            return ProtegeOWL.createJenaOWLModelFromURI(kbFilePath);
        } catch (OntologyLoadException e) {
            System.out.println("��ȡ�����ļ�" + kbFilePath + "��Ӧ��owlModel����");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * ������������Ϊ���ӹ�ϵ�����ǲ��������󱣴浽�ļ���
     */
    public static void setBSRelaiton(OWLNamedClass baseClass, OWLNamedClass superClass) {
        baseClass.addSuperclass(superClass);
    }

    /**
     * ��owlModel���󱣴浽�ļ���
     *
     * @param owlModel
     */
    public static void saveOwlModel2File(OWLModel owlModel) {
        ((JenaOWLModel) owlModel).save(new File("C:/ontologyOWL/AllOwlFile/sumoOWL2/sumo_phone3.owl").toURI(), FileUtils.langXMLAbbrev, new ArrayList());
    }

    /**
     * ��fromClz��������ƽ�Ƶ�toClz��
     * @param owlModel ����Ϣ������model
     * @param fromClz
     * @param toClz
     */
    public static void setDescClz(OWLModel owlModel, OWLNamedClass fromClz, OWLNamedClass toClz) {
        //��ȡfrom��ֱ������
        Collection<OWLNamedClass> fromSubClzList = fromClz.getSubclasses(false);

        //��fromClz��ֱ��������ΪtoClz��ֱ������
        for(OWLNamedClass fromSubClz:fromSubClzList){
            OWLNamedClass newtoClzsubClz;
            if((newtoClzsubClz=owlModel.getOWLNamedClass(fromSubClz.getName()))==null){
                newtoClzsubClz=owlModel.createOWLNamedClass(fromSubClz.getName());
            }
            newtoClzsubClz.addSuperclass(toClz);
            newtoClzsubClz.removeSuperclass(owlModel.getOWLNamedClass("owl:Thing"));

            setDescClz(owlModel,newtoClzsubClz,fromSubClz);
        }
    }

}
