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
 * @author ���޿�
 * @date 2018/11/20
 */
public class OMUtils {

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
            return null;
        }
    }

    /**
     * ��������࣬����ָ�����ǵĸ��ӹ�ϵ��Ȼ�󱣴浽�ļ������ӱ���Ϊ���һ��TODO������õ�Ӧ����Ϊһ�����ഴ���������
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
     * ����һ�Զ����и��ӹ�ϵ���࣬�����浽�ļ���
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
     * ΪsonClass��superClass���ø��ӹ�ϵ�������浽�ļ���
     *
     * @param owlModel
     * @param sonClass
     * @param superClass
     */
    public static void setSuperClass(OWLModel owlModel, OWLNamedClass sonClass, OWLNamedClass superClass) {
        sonClass.addSuperclass(superClass);
        sonClass.removeSuperclass(owlModel.getOWLNamedClass("owl:Thing"));
        //fixme ��ֱ�Ӹ���ֻ��superClass
        saveOwlModel2File(owlModel);
    }

    /**
     * Ϊ����������ֵ������������浽�ļ�
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
     * ����һ���ಢ���浽�ļ���
     *
     * @param owlModel
     * @param clzUri
     */
    public static void createOWLNamedClass(OWLModel owlModel, String clzUri) {
        owlModel.createOWLNamedClass(clzUri);
        saveOwlModel2File(owlModel);
    }

    /**
     * ����owl�����ļ�
     *
     * @param owlModel
     */
    public static void saveOwlModel2File(OWLModel owlModel) {
        ((JenaOWLModel) owlModel).save(new File("c:/ontologyOWL/dbpediaschema/dbpedia_2014.owl").toURI(), FileUtils.langXMLAbbrev, new ArrayList());
    }

    /**
     * ��animModel������animClz���µ������ࡰƽ�ơ���dbpediaModel�����е�dbpediaClz���£���dbpediaClz��animClz�ǵȼ���
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
         * ���ö�������
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
