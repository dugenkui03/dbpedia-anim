package ontologymatching;

import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFSNamedClass;
import util.TDBUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * todo 1. 统计新添加的数据;2.将动画知识库的实例挂在DBpedia-schema中；3.设置子类关系的时候需要递归设置子孙类关系
 *
 * @author 杜艮魁
 * @date 2018/11/21
 */
public class OMHandler {
    public static int equalClzCount = 0;

    public static int subClzCount = 0;

    public static int clz2Entity = 0;


    public static void main(String[] args) {

        /**
         * 获取匹配成功的数据放进List<String[]>中
         */
        List<String[]> matchedInfoLines = new LinkedList<>();
        File matchedFile = new File("knowledgebase/finalMatchedAnim");
        try (BufferedReader bf = new BufferedReader(new FileReader(matchedFile))) {
            String line = "";
            while ((line = bf.readLine()) != null) {
                String[] tmpArr = line.split(";");
                if (tmpArr.length > 2 && !tmpArr[3].contains("http://dbpedia.org/datatype")) {
                    matchedInfoLines.add(tmpArr);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        /**
         * 获取知识库对应的OWLModel对象:
         *      1. dbpedia_2014是数据桥梁的基础；
         *      2. 对数据桥梁进行补充；
         */
        OWLModel dbpediaOwlModel = OMUtils.getOwlModel("file:///E:/javaWorkspace/dbpedia-anim/knowledgebase/dbpedia_2014.owl");
        OWLModel animOwlModel = OMUtils.getOwlModel("file:///C:/ontologyOWL/AllOwlFile/sumoOWL2/sumo_phone3.owl");

        /**
         * 获取DBpedia所有的类
         */
        OWLNamedClass topClz = dbpediaOwlModel.getOWLNamedClass(Constants.TOP_CLASS);
        Collection<DefaultRDFSNamedClass> allDBpediaClz = topClz.getSubclasses(true);

        /**
         * 获取已经匹配的DBpedia类及其DBpedia数据集中的实例（1w）todo:动画知识库中234个类匹配到DBpedia150个类上
         */
        Map<String, String> clzEntitiesMap = new HashMap();
        try {
            File file = new File("knowledgebase/clzEntities");
            BufferedReader bf = new BufferedReader(new FileReader(file));
            String line = "";
            while ((line = bf.readLine()) != null) {
                clzEntitiesMap.put(line, bf.readLine());
            }
        } catch (Exception e) {
            System.out.println("can't open file:knowledgebase/clzEntities");
            e.printStackTrace();
        }


        /**
         * 遍历已经匹配的结果集：
         *      1. 类的话用 http://www.w3.org/2002/07/owl#equivalentClass 链接；
         *      2. 动画类匹配到DBpedia实体的话用 todo
         */

        for (String[] matchedInfoLine : matchedInfoLines) {
            String animUri = matchedInfoLine[0].substring(21, matchedInfoLine[0].length() - 1);
            String matchedDBpediaUri = matchedDBpediaUri = matchedInfoLine[3].substring(1, matchedInfoLine[3].length() - 1);
            String matchedType = matchedInfoLine[2];

            System.out.println("matchedLine:" + animUri + "\t" + matchedType + "\t" + matchedDBpediaUri);

            /**
             * 如果是类对类，则 在DBpedia_Schema中创建此动画类并设置为相应的dbpedia类的等价类，并将动画类的子类在DBpedia类中创建并且设置为对应dbpedia类的子类
             */
            if (Constants.ANIMCLZ_EQUAL_DBPEDIACLZ.contains(matchedType)) {
                equalClzCount++;

                //在DBpedia schema中创建动画类
                OWLNamedClass animClz;
                if ((animClz = dbpediaOwlModel.getOWLNamedClass(animUri)) == null) {
                    animClz = dbpediaOwlModel.createOWLNamedClass(animUri);
                }

                //fixme 不同的uri对应不同的XXNamedClass，所以有if-else判断
                if (matchedDBpediaUri.contains("http://dbpedia.org")) {
                    OWLNamedClass dbpediaClz = dbpediaOwlModel.getOWLNamedClass(matchedDBpediaUri);

                    /**
                     * 将DBpedia类下边的实例挂到 等价动画类 下边：
                     *      1. 创建实例:先查找、后创建；
                     *      2. 如果存在着直接设置其类型属性；
                     *      3. 如果不存在则用dbpediaClz创建、然后设置类型属性为animClz类。
                     */
                    TDBUtils.setInstance(animClz,matchedDBpediaUri);

                    OMUtils.setDescClz(dbpediaOwlModel, animClz,animOwlModel.getOWLNamedClass(animUri));//将关联的数据导入动画知识库后，子类关系直接有呀

                    //指定等价关系 todo 必须先指定子类，然后指定等价关系，否则出错
                    System.out.println("equvilentClass_relation:" + animUri + "\t" + matchedDBpediaUri);
                    dbpediaClz.addEquivalentClass(animClz);
                } else {//
                    RDFSNamedClass dbpediaClz = dbpediaOwlModel.getRDFSNamedClass(matchedDBpediaUri);

//                        //获取动画类的子类
//                        Collection<OWLNamedClass> animSubClassList = animOwlModel.getOWLNamedClass(animUri).getSubclasses(false);
//                        //在DBpedia中创建动画类子类，并且指定动画类对应的DBpedia类为父类
//                        for (OWLNamedClass subAnimClz : animSubClassList) {
//                            equalClzCount++;
//                            OWLNamedClass animClz2DBpediaClz;
//                            if((animClz2DBpediaClz=dbpediaOwlModel.getOWLNamedClass(subAnimClz.getName()))==null){
//                                animClz2DBpediaClz=dbpediaOwlModel.createOWLNamedClass(subAnimClz.getName());
//                            }
//                            animClz2DBpediaClz.addSuperclass(dbpediaClz);
//                            animClz2DBpediaClz.removeSuperclass(dbpediaOwlModel.getOWLNamedClass("owl:Thing"));
//                        }
                    OMUtils.setDescClzX(dbpediaOwlModel, dbpediaClz, animOwlModel.getOWLNamedClass(animUri));

                    //指定等价关系 todo 必须先指定子类，然后指定等价关系，否则出错
//                    System.out.println("equvilentClass_relation:" + animUri + "\t" + matchedDBpediaUri);
                    RDFProperty equivalentClassProp = dbpediaOwlModel.getRDFProperty("http://www.w3.org/2002/07/owl#equivalentClass");
//                    dbpediaClz.setPropertyValue(equivalentClassProp, animClz);
                }
            }// end of handle Class
            /**
             * 如果是动画类对DBpedia实体，则：
             *      1. 先在DBpedia-schema中创建对应的动画类；
             *      2. 创建其实例，即对应的DBpedia实体;
             *      todo 3. 此动画类下边的子类和实例是否用拷贝过来。
             */
            else if (Constants.ANIMCLZ_EQUAL_DBPEDIAINS.contains(matchedInfoLine[2])) {
//                clz2Entity++;
//                OWLNamedClass dbpediaClz;
//                if ((dbpediaClz = dbpediaOwlModel.getOWLNamedClass(animUri)) == null) {
//                    dbpediaClz = dbpediaOwlModel.createOWLNamedClass(animUri);
//                }
//                OWLIndividual newIndividual;
//                if ((newIndividual = dbpediaOwlModel.getOWLIndividual(matchedDBpediaUri)) == null) {
//                    dbpediaClz.createOWLIndividual(matchedDBpediaUri);
//                    System.out.println("create clz individual:" + matchedDBpediaUri);
//                } else {
//                    newIndividual.addRDFType(dbpediaClz);
////                        System.out.println(anim2DBpediaClzUri+"重复");
//                }
            }
        }
        OMUtils.saveOwlModel2File(dbpediaOwlModel);
        System.out.println(equalClzCount + ":" + subClzCount + ":" + clz2Entity);
    }
}
