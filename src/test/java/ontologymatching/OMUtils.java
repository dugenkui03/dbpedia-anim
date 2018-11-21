package ontologymatching;

import com.hp.hpl.jena.util.FileUtils;
import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFSNamedClass;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

/**
 * @author 杜艮魁
 * @date 2018/11/20
 */
public class OMUtils {

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
            return null;
        }
    }

    /**
     * 创建多个类，并且指定他们的父子关系，然后保存到文件。父子比例为多对一。TODO：最多用的应该是为一个父类创建多个子类
     *
     * @param owlModel
     * @param sonUri
     * @param fatherClassUri
     */
    public static void createSubclassForMultiFather(OWLModel owlModel, String sonUri, Collection<String> fatherClassUri) {
        Collection<OWLNamedClass> fatherClassList = new LinkedList<>();
        for (String uriEle : fatherClassUri) {
            fatherClassList.add(owlModel.createOWLNamedClass(uriEle));
        }
        owlModel.createSubclass(sonUri, fatherClassList);
        saveOwlModel2File(owlModel);
    }

    /**
     * 创建一对儿具有父子关系的类，并保存到文件中
     *
     * @param owlModel
     * @param sonUri
     * @param superClassUri
     */
    public static void createSubclass(OWLModel owlModel, String sonUri, String superClassUri) {
        owlModel.createSubclass(sonUri, owlModel.createRDFSNamedClass(superClassUri));
        saveOwlModel2File(owlModel);
    }

    /**
     * 为sonClass和superClass设置父子关系，并保存到文件中
     *
     * @param owlModel
     * @param sonClass
     * @param superClass
     */
    public static void setSuperClass(OWLModel owlModel, OWLNamedClass sonClass, OWLNamedClass superClass) {
        sonClass.addSuperclass(superClass);
        sonClass.removeSuperclass(owlModel.getOWLNamedClass("owl:Thing"));
        //fixme 其直接父类只有superClass
        saveOwlModel2File(owlModel);
    }

    /**
     * 为类设置属性值，并将结果保存到文件
     *
     * @param owlModel
     * @param clz
     * @param property
     * @param propertyValue
     */
    public static void setProperty(OWLModel owlModel, OWLNamedClass clz, RDFProperty property, Object propertyValue) {
        clz.setPropertyValue(property, propertyValue);
        saveOwlModel2File(owlModel);
    }

    public static void createSubclassForMultiSon(OWLModel owlModel, String superClassUri, Collection<String> sonClassUri) {

    }

    /**
     * 创建一个类并保存到文件中
     *
     * @param owlModel
     * @param clzUri
     */
    public static void createOWLNamedClass(OWLModel owlModel, String clzUri) {
        owlModel.createOWLNamedClass(clzUri);
        saveOwlModel2File(owlModel);
    }

    /**
     * 保存owl对象到文件
     *
     * @param owlModel
     */
    public static void saveOwlModel2File(OWLModel owlModel) {
        ((JenaOWLModel) owlModel).save(new File("c:/ontologyOWL/dbpediaschema/dbpedia_2014.owl").toURI(), FileUtils.langXMLAbbrev, new ArrayList());
    }

    /**
     * 将animModel对象中animClz类下的子孙类“平移”到dbpediaModel对象中的dbpediaClz类下，即dbpediaClz和animClz是等价类
     */
    public static void setDescClz(OWLModel dbpediaModel, OWLNamedClass dbpediaClz, OWLNamedClass animClz) {
        Collection<OWLNamedClass> animSubClzList = animClz.getSubclasses(false);

        for(OWLNamedClass subClz:animSubClzList){
            OWLNamedClass newDBpediaClz;
            if((newDBpediaClz=dbpediaModel.getOWLNamedClass(subClz.getName()))==null){
                newDBpediaClz=dbpediaModel.createOWLNamedClass(subClz.getName());
            }
            newDBpediaClz.addSuperclass(dbpediaClz);
            newDBpediaClz.removeSuperclass(dbpediaModel.getOWLNamedClass("owl:Thing"));

            setDescClz(dbpediaModel,newDBpediaClz,subClz);
        }
    }


    public static void printClassNameInfo(DefaultRDFSNamedClass clz) {
        System.out.println(clz.getName() + "\t number of all subClasses:" + clz.getSubclasses(true).size());
        if (clz.getSubclassCount() > 0) {
            Collection<DefaultRDFSNamedClass> clzList = clz.getSubclasses(false);
            for (DefaultRDFSNamedClass ele : clzList) {
                printClassNameInfo(ele);
            }
        }
    }


    public static void main(String[] args) {
        OMUtils om = new OMUtils();
        OWLModel owlModel = om.getOwlModel(Constants.KB_LOCATION);

        /**
         * 设置对象属性
         */
        OWLNamedClass XXXXXXXXXXXXX = owlModel.createOWLNamedClass("http://dbpedia.org/ontology/XXXXXXXXXXXxxxxXXXXXXXXXX");
        OWLNamedClass YYYYYYYYYYYYYY = owlModel.createOWLNamedClass("http://dbpedia.org/ontology/YYYYYYYYYYYYYYYYYYYyyyyy");
//        Collection x=owlModel.getRDFProperties();
//        Collection y=owlModel.getOWLOntologyProperties();
        RDFProperty z = owlModel.getRDFProperty("http://www.w3.org/2002/07/owl#equivalentClass");
        XXXXXXXXXXXXX.setPropertyValue(z, YYYYYYYYYYYYYY);
        om.saveOwlModel2File(owlModel);

//
//        sonClass.setPropertyValue(equivalentClassProp,superClass);
//        System.out.println(owlModel.getOWLNamedClass("XXSon").getSuperclasses());

//        om.setSuperClass(owlModel, owlModel.createOWLNamedClass("http://dbpedia.org/ontology/YYSON"), owlModel.createOWLNamedClass("http://dbpedia.org/ontology/YYFATHER"));


////        owlModel.createSubclass("http://dbpedia.org/ontology/luoming",owlModel.createRDFSNamedClass("http://dbpedia.org/ontology/luoming01"));
////        owlModel.createSubclass("http://dbpedia.org/ontology/luoming",owlModel.createRDFSNamedClass("http://dbpedia.org/ontology/luoming02"));
//        OWLNamedClass obj=owlModel.createOWLNamedClass("http://dbpedia.org/ontology/luoming01");
//        owlModel.createOWLNamedClass("http://dbpedia.org/ontology/newClass",obj);


//        OWLNamedClass topClass = owlModel.getOWLNamedClass(Constants.DEMO_CLASS);
//        Collection<DefaultRDFSNamedClass> allOwlNamedClassCollection = topClass.getSubclasses(false);
//        for (DefaultRDFSNamedClass ele : allOwlNamedClassCollection) {
//            om.printClassNameInfo(ele);
//        }
//        OWLNamedClass clz1 = owlModel.createOWLNamedClass("http://dbpedia.org/ontology/Dugenkui01"), clz2 = owlModel.createOWLNamedClass("http://dbpedia.org/ontology/Dugenkui02");
//        owlModel.createSubclass("http://dbpedia.org/ontology/Dugenkuix", Arrays.asList(clz1, clz2));
//        ((JenaOWLModel) owlModel).save(new File("c:/ontologyOWL/dbpediaschema/dbpedia_2014.owl").toURI(), FileUtils.langXMLAbbrev, new ArrayList());
//


//        for (DefaultRDFSNamedClass ele:allOwlNamedClassCollection) {
//            System.out.println(ele.getName());
//            if(ele.getSubclassCount()>0)
//                System.out.println("\t"+ele.getSubclasses(true));
//        }
    }
}
