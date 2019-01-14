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
 * todo 1. ͳ������ӵ�����;2.������֪ʶ���ʵ������DBpedia-schema�У�3.���������ϵ��ʱ����Ҫ�ݹ������������ϵ
 *
 * @author ���޿�
 * @date 2018/11/21
 */
public class OMHandler {
    public static int equalClzCount = 0;

    public static int subClzCount = 0;

    public static int clz2Entity = 0;


    public static void main(String[] args) {

        /**
         * ��ȡƥ��ɹ������ݷŽ�List<String[]>��
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
         * ��ȡ֪ʶ���Ӧ��OWLModel����:
         *      1. dbpedia_2014�����������Ļ�����
         *      2. �������������в��䣻
         */
        OWLModel dbpediaOwlModel = OMUtils.getOwlModel("file:///E:/javaWorkspace/dbpedia-anim/knowledgebase/dbpedia_2014.owl");
        OWLModel animOwlModel = OMUtils.getOwlModel("file:///C:/ontologyOWL/AllOwlFile/sumoOWL2/sumo_phone3.owl");

        /**
         * ��ȡDBpedia���е���
         */
        OWLNamedClass topClz = dbpediaOwlModel.getOWLNamedClass(Constants.TOP_CLASS);
        Collection<DefaultRDFSNamedClass> allDBpediaClz = topClz.getSubclasses(true);

        /**
         * ��ȡ�Ѿ�ƥ���DBpedia�༰��DBpedia���ݼ��е�ʵ����1w��todo:����֪ʶ����234����ƥ�䵽DBpedia150������
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
         * �����Ѿ�ƥ��Ľ������
         *      1. ��Ļ��� http://www.w3.org/2002/07/owl#equivalentClass ���ӣ�
         *      2. ������ƥ�䵽DBpediaʵ��Ļ��� todo
         */

        for (String[] matchedInfoLine : matchedInfoLines) {
            String animUri = matchedInfoLine[0].substring(21, matchedInfoLine[0].length() - 1);
            String matchedDBpediaUri = matchedDBpediaUri = matchedInfoLine[3].substring(1, matchedInfoLine[3].length() - 1);
            String matchedType = matchedInfoLine[2];

            System.out.println("matchedLine:" + animUri + "\t" + matchedType + "\t" + matchedDBpediaUri);

            /**
             * ���������࣬�� ��DBpedia_Schema�д����˶����ಢ����Ϊ��Ӧ��dbpedia��ĵȼ��࣬�����������������DBpedia���д�����������Ϊ��Ӧdbpedia�������
             */
            if (Constants.ANIMCLZ_EQUAL_DBPEDIACLZ.contains(matchedType)) {
                equalClzCount++;

                //��DBpedia schema�д���������
                OWLNamedClass animClz;
                if ((animClz = dbpediaOwlModel.getOWLNamedClass(animUri)) == null) {
                    animClz = dbpediaOwlModel.createOWLNamedClass(animUri);
                }

                //fixme ��ͬ��uri��Ӧ��ͬ��XXNamedClass��������if-else�ж�
                if (matchedDBpediaUri.contains("http://dbpedia.org")) {
                    OWLNamedClass dbpediaClz = dbpediaOwlModel.getOWLNamedClass(matchedDBpediaUri);

                    /**
                     * ��DBpedia���±ߵ�ʵ���ҵ� �ȼ۶����� �±ߣ�
                     *      1. ����ʵ��:�Ȳ��ҡ��󴴽���
                     *      2. ���������ֱ���������������ԣ�
                     *      3. �������������dbpediaClz������Ȼ��������������ΪanimClz�ࡣ
                     */
                    TDBUtils.setInstance(animClz,matchedDBpediaUri);

                    OMUtils.setDescClz(dbpediaOwlModel, animClz,animOwlModel.getOWLNamedClass(animUri));//�����������ݵ��붯��֪ʶ��������ϵֱ����ѽ

                    //ָ���ȼ۹�ϵ todo ������ָ�����࣬Ȼ��ָ���ȼ۹�ϵ���������
                    System.out.println("equvilentClass_relation:" + animUri + "\t" + matchedDBpediaUri);
                    dbpediaClz.addEquivalentClass(animClz);
                } else {//
                    RDFSNamedClass dbpediaClz = dbpediaOwlModel.getRDFSNamedClass(matchedDBpediaUri);

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
                    OMUtils.setDescClzX(dbpediaOwlModel, dbpediaClz, animOwlModel.getOWLNamedClass(animUri));

                    //ָ���ȼ۹�ϵ todo ������ָ�����࣬Ȼ��ָ���ȼ۹�ϵ���������
//                    System.out.println("equvilentClass_relation:" + animUri + "\t" + matchedDBpediaUri);
                    RDFProperty equivalentClassProp = dbpediaOwlModel.getRDFProperty("http://www.w3.org/2002/07/owl#equivalentClass");
//                    dbpediaClz.setPropertyValue(equivalentClassProp, animClz);
                }
            }// end of handle Class
            /**
             * ����Ƕ������DBpediaʵ�壬��
             *      1. ����DBpedia-schema�д�����Ӧ�Ķ����ࣻ
             *      2. ������ʵ��������Ӧ��DBpediaʵ��;
             *      todo 3. �˶������±ߵ������ʵ���Ƿ��ÿ���������
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
////                        System.out.println(anim2DBpediaClzUri+"�ظ�");
//                }
            }
        }
        OMUtils.saveOwlModel2File(dbpediaOwlModel);
        System.out.println(equalClzCount + ":" + subClzCount + ":" + clz2Entity);
    }
}
