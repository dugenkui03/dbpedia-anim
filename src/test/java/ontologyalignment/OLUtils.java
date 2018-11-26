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
 * 动画知识库链接到DBpedia上时，需要的一些工具方法
 *
 * @author 杜艮魁
 * @date 2018/11/23
 */
public class OLUtils {

    /**
     * 获取一个owl文件对应的model对象
     *
     * @param kbFilePath
     * @return
     */
    public static OWLModel getOwlModel(String kbFilePath) {
        try {
            return ProtegeOWL.createJenaOWLModelFromURI(kbFilePath);
        } catch (OntologyLoadException e) {
            System.out.println("获取不了文件" + kbFilePath + "对应的owlModel对象");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将两个类设置为父子关系，但是并不将对象保存到文件中
     */
    public static void setBSRelaiton(OWLNamedClass baseClass, OWLNamedClass superClass) {
        baseClass.addSuperclass(superClass);
    }

    /**
     * 将owlModel对象保存到文件中
     *
     * @param owlModel
     */
    public static void saveOwlModel2File(OWLModel owlModel) {
        ((JenaOWLModel) owlModel).save(new File("C:/ontologyOWL/AllOwlFile/sumoOWL2/sumo_phone3.owl").toURI(), FileUtils.langXMLAbbrev, new ArrayList());
    }

    /**
     * 将fromClz的子孙类平移到toClz下
     * @param owlModel 类信息所属的model
     * @param fromClz
     * @param toClz
     */
    public static void setDescClz(OWLModel owlModel, OWLNamedClass fromClz, OWLNamedClass toClz) {
        //获取from的直接子类
        Collection<OWLNamedClass> fromSubClzList = fromClz.getSubclasses(false);

        //将fromClz的直接子类作为toClz的直接子类
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
