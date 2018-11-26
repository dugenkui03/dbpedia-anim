package ontologyalignment;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import ontologymatching.Constants;
import ontologymatching.OMUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ���޿�
 * @date 2018/11/23
 */
public class OLHandler {
    public static void main(String[] args) {

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
         * ��ȡ֪ʶ���Ӧ��OWLModel����
         */
        OWLModel owlModel = OLUtils.getOwlModel("file:///C:/ontologyOWL/AllOwlFile/sumoOWL2/sumo_phone3.owl");
        RDFProperty equivalentClassProp = owlModel.getRDFProperty("http://www.w3.org/2002/07/owl#equivalentClass");

        /**
         * �������еĶ����࣬�������������
         *      1. ����еȼ۵�DBpedia�࣬��
         *          1��������֮��ĵȼ۹�ϵ��
         *          2���Է��������໥Ϊ�Լ��������ࣻ
         *          3������DBpedia���ʵ����
         *              [1].
         *      2. ����Ƕ�����ƥ�䵽DBpediaʵ��������ʵ�����ɡ�
         */
        for (String [] eleArr:lineInfo.stream().filter(x->x.length>2).collect(Collectors.toList())) {
            /**
             * ��ȡƥ�������е�animUri��dbpediaUri��ƥ������(��Ҫָ����ƥ�仹����ʵ��ƥ��)
             */
            String animUri = eleArr[0].substring(21, eleArr[0].length() - 1);
            String dbpediaUri,matchedType;
            if(eleArr.length==3){
                dbpediaUri = eleArr[2].substring(1, eleArr[2].length() - 1);
                matchedType= eleArr[1].substring(1, eleArr[1].length() - 1);
            }else{
                dbpediaUri = eleArr[3].substring(1, eleArr[3].length() - 1);
                matchedType = eleArr[2].substring(1, eleArr[2].length() - 1);
            }

            /**
             * ָ���ȼ۹�ϵ���ҽ��������������DBpedia���д�����������Ϊ������ todo ƥ�����������ݣ��������Ͳ�����������ƥ��
             */
            if (dbpediaUri.contains("http://dbpedia.org/datatype")) {
                continue;
            }

            /**
             * ���������֮���ƥ��
             */
            if(Constants.ANIMCLZ_EQUAL_DBPEDIACLZ.contains(matchedType)){
                //��ȡ����DBpedia-Schema��Ķ���֪ʶ���е�animClz��dbpediaClz
                OWLNamedClass animClz = owlModel.getOWLNamedClass(animUri), dbpediaClz = owlModel.getOWLNamedClass(dbpediaUri);

                //todo ���ø������ϵ

                //ָ���ȼ۹�ϵ todo ������ָ�����࣬Ȼ��ָ���ȼ۹�ϵ���������
                animClz.setPropertyValue(equivalentClassProp, dbpediaClz);
            }
            /**
             * ����Ƕ������DBpediaʵ��֮���ƥ��
             */
            else{
                //todo
            }
        }

        OLUtils.saveOwlModel2File(owlModel);
    }
}
