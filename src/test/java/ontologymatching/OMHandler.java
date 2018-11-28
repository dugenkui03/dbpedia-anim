package ontologymatching;

import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFSNamedClass;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

/**
 * todo 1. ͳ������ӵ�����;2.������֪ʶ���ʵ������DBpedia-schema�У�3.���������ϵ��ʱ����Ҫ�ݹ������������ϵ
 *
 * @author ���޿�
 * @date 2018/11/21
 */
public class OMHandler {
    public static int equalClzCount=0;

    public static int subClzCount=0;

    public static int clz2Entity=0;

    public static int limitNum=0;

    public static void main(String[] args) {
        /**
         * ��ȡ֪ʶ���Ӧ��OWLModel����
         */
        OWLModel dbpediaOwlModel = OMUtils.getOwlModel("file:///E:/javaWorkspace/dbpedia-anim/knowledgebase/dbpedia_2014.owl");
        OWLModel animOwlModel = OMUtils.getOwlModel("file:///C:/ontologyOWL/AllOwlFile/sumoOWL2/sumo_phone3.owl");

        /**
         * ��ȡ֪ʶ�����е���
         */
        OWLNamedClass topClz = dbpediaOwlModel.getOWLNamedClass(Constants.TOP_CLASS);
        Collection<DefaultRDFSNamedClass> allClz = topClz.getSubclasses(true);

        /**
         * ��ӡ���������Ϣ
         */
//        for (DefaultRDFSNamedClass clz : allClz) {
//            System.out.println(clz.getName());
//            System.out.println(clz.getPrefixedName()+"\n");
//        }

        /**
         * ��ȡƥ�����ݷŽ�List<String[]>��
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
         * ��ȡ�Ѿ�ƥ���DBpedia�༰��DBpedia���ݼ��е�ʵ����1w��
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
         * ������������鿴��ǰ�������Ƿ��Ѿ���ƥ���DBpedia�����ʵ�壺
         *      1. ��Ļ��� http://www.w3.org/2002/07/owl#equivalentClass ���ӣ�
         *      2. ������ƥ�䵽DBpediaʵ��Ļ���
         */
        for (String[] eleArr : lineInfo) {
            /**
             * ����˶�����term�Ѿ���ƥ����DBpedia�����DBpedia��ʵ��
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
                 * ���������࣬��
                 *      1. ��DBpedia���д����˶����ࣻ
                 *      2.Ȼ��ָ�����ǵĵȼ۹�ϵ��
                 */
                if (Constants.ANIMCLZ_EQUAL_DBPEDIACLZ.contains(eleArr[2])) {
                    /**
                     * ָ���ȼ۹�ϵ���ҽ��������������DBpedia���д�����������Ϊ������ todo ƥ�����������ݣ��������Ͳ�����������ƥ��
                     */
                    if (anim2DBpediaClzUri.contains("http://dbpedia.org/datatype")) {
                        continue;
                    }

                    //��DBpedia schema�д���������
                    OWLNamedClass animClz;
                    if((animClz=dbpediaOwlModel.getOWLNamedClass(animUri))==null){
                        animClz=dbpediaOwlModel.createOWLNamedClass(animUri);
                    }

                    //fixme ��ͬ��uri��Ӧ��ͬ��XXNamedClass��������if-else�ж�
                    if (anim2DBpediaClzUri.contains("http://dbpedia.org")) {
                        equalClzCount++;
                        OWLNamedClass dbpediaClz = dbpediaOwlModel.getOWLNamedClass(anim2DBpediaClzUri);

                        //todo ��anim2DBpediaClzUri ��ʵ���ҵ��������±�
                        /**
                         * ��DBpedia���±ߵ�ʵ���ҵ�dbpedia_schema��͵ȼ۶������±ߣ�
                         *      1. ����ʵ��:�Ȳ��ҡ��󴴽���
                         *      2. ���������ֱ���������������ԣ�
                         *      3. �������������dbpediaClz������Ȼ��������������ΪanimClz�ࡣ
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
//                        //��ȡ�����������
//                        Collection<OWLNamedClass> animSubClassList = animOwlModel.getOWLNamedClass(animUri).getSubclasses(false);
//                        //��DBpedia�д������������࣬����ָ���������Ӧ��DBpedia��Ϊ����
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

                        //ָ���ȼ۹�ϵ todo ������ָ�����࣬Ȼ��ָ���ȼ۹�ϵ���������
                        RDFProperty equivalentClassProp = dbpediaOwlModel.getRDFProperty("http://www.w3.org/2002/07/owl#equivalentClass");
                        dbpediaClz.setPropertyValue(equivalentClassProp, animClz);

                    } else {
                        equalClzCount++;
                        RDFSNamedClass dbpediaClz = dbpediaOwlModel.getRDFSNamedClass(anim2DBpediaClzUri);

//                        //��ȡ�����������
//                        Collection<OWLNamedClass> animSubClassList = animOwlModel.getOWLNamedClass(animUri).getSubclasses(false);
//                        //��DBpedia�д������������࣬����ָ���������Ӧ��DBpedia��Ϊ����
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

                        //ָ���ȼ۹�ϵ todo ������ָ�����࣬Ȼ��ָ���ȼ۹�ϵ���������
                        RDFProperty equivalentClassProp = dbpediaOwlModel.getRDFProperty("http://www.w3.org/2002/07/owl#equivalentClass");
                        dbpediaClz.setPropertyValue(equivalentClassProp, animClz);
                    }
                }
                /**
                 * ����Ƕ������DBpediaʵ�壬��
                 *      1. ����DBpedia-schema�д�����Ӧ�Ķ����ࣻ
                 *      2. ������ʵ��������Ӧ��DBpediaʵ��;
                 *      todo 3. �˶������±ߵ������ʵ���Ƿ��ÿ���������
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
//                        System.out.println(anim2DBpediaClzUri+"�ظ�");
                    }
                }
            }
        }
        OMUtils.saveOwlModel2File(dbpediaOwlModel);
        System.out.println(equalClzCount+":"+subClzCount+":"+clz2Entity);
    }
}
