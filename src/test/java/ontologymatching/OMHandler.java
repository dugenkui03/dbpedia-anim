package ontologymatching;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFSNamedClass;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * todo 1. ͳ������ӵ�����;2.������֪ʶ���ʵ������DBpedia-schema�У�3.���������ϵ��ʱ����Ҫ�ݹ������������ϵ
 *
 * @author ���޿�
 * @date 2018/11/21
 */
public class OMHandler {
    public static int equalClzCount=0;

    public static int subClzCount=0;

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
        for (DefaultRDFSNamedClass clz : allClz) {
//            System.out.println(clz.getName());
//            System.out.println(clz.getPrefixedName()+"\n");
        }

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
//                try {
//                    anim2DBpediaClzUri = eleArr[3].substring(1, eleArr[3].length() - 1);
//                } catch (Exception e) {
//                    System.out.println("final"+ Arrays.asList(eleArr));
//                }
                /**
                 * ���������࣬��
                 *      1. ��DBpedia���д����˶����ࣻ
                 *      2.Ȼ��ָ�����ǵĵȼ۹�ϵ��
                 */
                if (Constants.ANIMCLZ_EQUAL_DBPEDIACLZ.contains(eleArr[2])) {
                    //��DBpedia schema�д���������
                    OWLNamedClass animClz;
                    if((animClz=dbpediaOwlModel.getOWLNamedClass(animUri))==null){
                        animClz=dbpediaOwlModel.createOWLNamedClass(animUri);
                    }

                    /**
                     * ָ���ȼ۹�ϵ���ҽ��������������DBpedia���д�����������Ϊ������ todo ƥ�����������ݣ��������Ͳ�����������ƥ��
                     */
                    if (anim2DBpediaClzUri.contains("http://dbpedia.org/datatype")) {
                        continue;
                    }
                    //fixme ��ͬ��uri��Ӧ��ͬ��XXNamedClass��������if-else�ж�
                    if (anim2DBpediaClzUri.contains("http://dbpedia.org")) {
                        equalClzCount++;
                        OWLNamedClass dbpediaClz = dbpediaOwlModel.getOWLNamedClass(anim2DBpediaClzUri);

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
//                    OWLNamedClass dbpediaClz;
//                    if((dbpediaClz=dbpediaOwlModel.getOWLNamedClass(animUri))==null){
//                        dbpediaClz=dbpediaOwlModel.createOWLNamedClass(animUri);
//                    }
//                    dbpediaClz.createOWLIndividual(anim2DBpediaClzUri);
                }
            }
        }
        OMUtils.saveOwlModel2File(dbpediaOwlModel);
        System.out.println(equalClzCount+":"+subClzCount);
    }
}
