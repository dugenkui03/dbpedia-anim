package ontologymatching;

import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFSNamedClass;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

/**
 * todo 1. 统计新添加的数据;2.将动画知识库的实例挂在DBpedia-schema中；3.设置子类关系的时候需要递归设置子孙类关系
 *
 * @author 杜艮魁
 * @date 2018/11/21
 */
public class OMHandler {
    public static int equalClzCount=0;

    public static int subClzCount=0;

    public static int clz2Entity=0;

    public static int limitNum=0;

    public static void main(String[] args) {
        /**
         * 获取知识库对应的OWLModel对象
         */
        OWLModel dbpediaOwlModel = OMUtils.getOwlModel("file:///E:/javaWorkspace/dbpedia-anim/knowledgebase/dbpedia_2014.owl");
        OWLModel animOwlModel = OMUtils.getOwlModel("file:///C:/ontologyOWL/AllOwlFile/sumoOWL2/sumo_phone3.owl");

        /**
         * 获取知识库所有的类
         */
        OWLNamedClass topClz = dbpediaOwlModel.getOWLNamedClass(Constants.TOP_CLASS);
        Collection<DefaultRDFSNamedClass> allClz = topClz.getSubclasses(true);

        /**
         * 打印所有类的信息
         */
//        for (DefaultRDFSNamedClass clz : allClz) {
//            System.out.println(clz.getName());
//            System.out.println(clz.getPrefixedName()+"\n");
//        }

        /**
         * 获取匹配数据放进List<String[]>中
         */
        List<String[]> lineInfo = new LinkedList<>();
        try {
            File file = new File("knowledgebase/finalMatchedAnim");
            BufferedReader bf = new BufferedReader(new FileReader(file));
            String line = "";
            while ((line = bf.readLine()) != null) {
                lineInfo.add(line.split(";"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        /**
         * 获取已经匹配的DBpedia类及其DBpedia数据集中的实例（1w）
         */
        Map<String,String> clzEntitiesMap=new HashMap();
        try{
            File file=new File("knowledgebase/clzEntities");
            BufferedReader bf=new BufferedReader(new FileReader(file));
            String line="";
            while((line=bf.readLine())!=null){
                clzEntitiesMap.put(line,bf.readLine());
            }
        }catch (Exception e){
            System.out.println("can't open file:knowledgebase/clzEntities");
            e.printStackTrace();
        }


        /**
         * 遍历结果集，查看当前动画类是否已经有匹配的DBpedia类或者实体：
         *      1. 类的话用 http://www.w3.org/2002/07/owl#equivalentClass 链接；
         *      2. 动画类匹配到DBpedia实体的话用
         */
        for (String[] eleArr : lineInfo) {
            /**
             * 如果此动画的term已经被匹配上DBpedia类或者DBpedia的实体
             */
            if (eleArr.length > 2) {
                String animUri = eleArr[0].substring(21, eleArr[0].length() - 1);
                String anim2DBpediaClzUri = null;
                if(eleArr.length==3){
                    anim2DBpediaClzUri = eleArr[2].substring(1, eleArr[2].length() - 1);
                }else{
                    anim2DBpediaClzUri = eleArr[3].substring(1, eleArr[3].length() - 1);
                }

                System.out.println("animUri:"+animUri+";\tanim2DBpediaClzUri:"+anim2DBpediaClzUri);

                /**
                 * 如果是类对类，则：
                 *      1. 在DBpedia库中创建此动画类；
                 *      2.然后指定他们的等价关系。
                 */
                if (Constants.ANIMCLZ_EQUAL_DBPEDIACLZ.contains(eleArr[2])) {
                    /**
                     * 指定等价关系并且将动画类的子类在DBpedia类中创建并且设置为其子类 todo 匹配有误差的数据，数据类型不能用来进行匹配
                     */
                    if (anim2DBpediaClzUri.contains("http://dbpedia.org/datatype")) {
                        continue;
                    }

                    //在DBpedia schema中创建动画类
                    OWLNamedClass animClz;
                    if((animClz=dbpediaOwlModel.getOWLNamedClass(animUri))==null){
                        animClz=dbpediaOwlModel.createOWLNamedClass(animUri);
                    }

                    //fixme 不同的uri对应不同的XXNamedClass，所以有if-else判断
                    if (anim2DBpediaClzUri.contains("http://dbpedia.org")) {
                        equalClzCount++;
                        OWLNamedClass dbpediaClz = dbpediaOwlModel.getOWLNamedClass(anim2DBpediaClzUri);

                        //todo 将anim2DBpediaClzUri 的实例挂到两个类下边
                        /**
                         * 将DBpedia类下边的实例挂到dbpedia_schema类和等价动画类下边：
                         *      1. 创建实例:先查找、后创建；
                         *      2. 如果存在着直接设置其类型属性；
                         *      3. 如果不存在则用dbpediaClz创建、然后设置类型属性为animClz类。
                         */
                        String entitiesStr="";
                        if((entitiesStr=clzEntitiesMap.get((String)("<"+anim2DBpediaClzUri+">")))!=null){
                            String entityArr[]= entitiesStr.substring(1,entitiesStr.length()-1).split(",");
                            for (String entityStr:entityArr) {
                                if(!entityStr.contains("http")){
                                    continue;
                                }
                                OWLIndividual newIndividual;
                                if((newIndividual=dbpediaOwlModel.getOWLIndividual(entityStr.substring(2,entityStr.length()-1)))==null){
//                                    System.out.println("xcreate individual:\t"+entityStr.substring(2,entityStr.length()-1)+" for clz:\t"+animUri+":\t"+anim2DBpediaClzUri);
                                    newIndividual=dbpediaClz.createOWLIndividual(entityStr.substring(2,entityStr.length()-1));
                                    newIndividual.addRDFType(animClz);
                                }else{
                                    newIndividual.addRDFType(dbpediaClz);
                                    newIndividual.addRDFType(animClz);
//                                    System.out.println("ycreate individual:\t"+entityStr.substring(2,entityStr.length()-1)+" for clz:\t"+animUri+":\t"+anim2DBpediaClzUri);
                                }
                            }
                        }
//                        //获取动画类的子类
//                        Collection<OWLNamedClass> animSubClassList = animOwlModel.getOWLNamedClass(animUri).getSubclasses(false);
//                        //在DBpedia中创建动画类子类，并且指定动画类对应的DBpedia类为父类
//                        for (OWLNamedClass subAnimClz : animSubClassList) {
//                            subClzCount++;
//                            OWLNamedClass animClz2DBpediaClz;
//                            if((animClz2DBpediaClz=dbpediaOwlModel.getOWLNamedClass(subAnimClz.getName()))==null){
//                                animClz2DBpediaClz=dbpediaOwlModel.createOWLNamedClass(subAnimClz.getName());
//                            }
//                            animClz2DBpediaClz.addSuperclass(dbpediaClz);
//                            animClz2DBpediaClz.removeSuperclass(dbpediaOwlModel.getOWLNamedClass("owl:Thing"));
//                        }
                        OMUtils.setDescClz(dbpediaOwlModel,dbpediaClz,animOwlModel.getOWLNamedClass(animUri));

                        //指定等价关系 todo 必须先指定子类，然后指定等价关系，否则出错
                        RDFProperty equivalentClassProp = dbpediaOwlModel.getRDFProperty("http://www.w3.org/2002/07/owl#equivalentClass");
                        dbpediaClz.setPropertyValue(equivalentClassProp, animClz);

                    } else {
                        equalClzCount++;
                        RDFSNamedClass dbpediaClz = dbpediaOwlModel.getRDFSNamedClass(anim2DBpediaClzUri);

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
                        OMUtils.setDescClzX(dbpediaOwlModel,dbpediaClz,animOwlModel.getOWLNamedClass(animUri));

                        //指定等价关系 todo 必须先指定子类，然后指定等价关系，否则出错
                        RDFProperty equivalentClassProp = dbpediaOwlModel.getRDFProperty("http://www.w3.org/2002/07/owl#equivalentClass");
                        dbpediaClz.setPropertyValue(equivalentClassProp, animClz);
                    }
                }
                /**
                 * 如果是动画类对DBpedia实体，则：
                 *      1. 先在DBpedia-schema中创建对应的动画类；
                 *      2. 创建其实例，即对应的DBpedia实体;
                 *      todo 3. 此动画类下边的子类和实例是否用拷贝过来。
                 */
                else if (Constants.ANIMCLZ_EQUAL_DBPEDIAINS.contains(eleArr[2])) {
                    clz2Entity++;
                    OWLNamedClass dbpediaClz;
                    if((dbpediaClz=dbpediaOwlModel.getOWLNamedClass(animUri))==null){
                        dbpediaClz=dbpediaOwlModel.createOWLNamedClass(animUri);
                    }
                    OWLIndividual newIndividual;
                    if((newIndividual=dbpediaOwlModel.getOWLIndividual(anim2DBpediaClzUri))==null){
                        dbpediaClz.createOWLIndividual(anim2DBpediaClzUri);
                        System.out.println("create clz individual:"+anim2DBpediaClzUri);
                    }else{
                        newIndividual.addRDFType(dbpediaClz);
//                        System.out.println(anim2DBpediaClzUri+"重复");
                    }
                }
            }
        }
        OMUtils.saveOwlModel2File(dbpediaOwlModel);
        System.out.println(equalClzCount+":"+subClzCount+":"+clz2Entity);
    }
}
