package main;

import cons.Constants;
import cons.Triple;
import engine.Similarity;
import invoke.RelationTerm;
import invoke.invoker.KnowledgeBaseInvoker;
import util.LookUpUtil;
import util.NamedEntityIdentity;
import util.StringUtil;
import util.ZhToEng;

import java.util.*;

/**
 * �ҵ������Ĵ��ﶨλ����term��صı���֪ʶ��ʵ��
 */
public class RelationFind {

    /**
     * �������
     *
     * @param message �ֺô���Ķ�������
     * @return
     */
    public static Map<String, String> relFin(String message) throws Exception {
        Map<String, String> result = new HashMap();

        /**
         * ��ȡ���������õķִʼ��ϣ���ȥ��
         */
        Set<String> wordsZh = StringUtil.getEle(message);
        System.out.println("���������õķִ�:" + wordsZh);
        /**
         * ��ȡ�ִʼ������Ӧ��Ӣ�ļ��ϡ�ʵ����[��ͩ;wutong, ȫ�۵�;quanjude, ����;whale, ������;Hemingway]
         */
        List<String> wordsEng = ZhToEng.zhsToEngs(new ArrayList<>(wordsZh));
        System.out.println("�ִʼ������Ӧ��Ӣ�ĵ��ʼ���:" + wordsEng);
        /**
         * ��ȡ���Ŷ�Ӧ��term: 1.ʹ��label����(����ԭ���Ҳ�������Ӣ��ԭ��)��2.ʹ��pageRank˼�룻3. lookup todo:����ʵ��ʶ���Ǽ�����·��
         */

        Map<String,String> terms=NamedEntityIdentity.findEntities(wordsEng);
//        List<String> terms = LookUpUtil.lookUp(wordsEng); fixme��ʹ���Լ��ĳ�����Ҵ�����ص�term
        System.out.println("���Ŷ�Ӧ��term��" + terms);
        /*==============================================================================================*/
        /**
         * ��Ҫ�ҵ��������ݣ�fixme����term�������Ĵ˵���������������ʹ�Ǵ� ȫ�۵� �õ�һ���� ȫ�۵� ���ˡ�������׼ȷ������һ�����⣻
         *      1. �������ݣ�term���ڱ���֪ʶ����������ģ��/�ص�����ƣ����硰���꣬���ˡ���
         *      2. �������ݣ�term���������ݡ�
         *
         * һ.�������ݣ�
         *      1.��ȡterm�����ͣ�
         *      2.��ѯ����֪ʶ������ͬ���͵�ʵ����
         *      3.��term����Ѱ��term�����ƵĲ�ͬ���͵ı�����Ʒ�������ء������߾�ֻѡһ����
         *
         * ��.�������ݣ�
         *      1.��ȡterm������term�����͡������ǹ������ݵ����ͣ�
         *      2.3Ҳͬ�ϡ�
         */
        /*==============================================================================================*/
        /**
         * �鿴term�������� �� term��������:
         *      1.termsType��term��������ͣ�ÿ��term��Ӧһ�����ϣ�
         *      2.termInfoTriple��term�������Ϣ���������Ժ�����ֵ��
         */
        Map<String,Set<String>> termsTypeList = new HashMap<>();
        Map<String,List<Triple>> termsDesc = new HashMap<>();
        for (String term : terms.values()) {
            //��ѯterm�����ͣ�Ȼ���ȡ��������Ϣ
            Set<String> termsType = new LinkedHashSet<>();
            List<Triple> classTrips = KnowledgeBaseInvoker.hltNet(Constants.QUERY_TERM_CLASS, term);
            for (Triple triple : classTrips) {
                termsType.add((String) triple.getT());
            }
            termsTypeList.put(term,termsType);
            //��ѯterm����Ϣ
            List<Triple> infoTriple = KnowledgeBaseInvoker.hltNet(Constants.QUERY_HEAD, term);
            termsDesc.put(term,infoTriple);
        }
        /**
         * ���ұ��غ�term����ͬ���͵�ʵ��
         */
        Map<String,Set<String>> terms2InstancesList = new HashMap<>();
        KnowledgeBaseInvoker kbi = KnowledgeBaseInvoker.newInvokerBuilder().localDir(Constants.ANIM_OWL_PATH).build();


        for (Map.Entry<String,Set<String>> termTypes : termsTypeList.entrySet()) {
            Set<String> sameTypesInstanceList = new HashSet<>();
            //һ��term�����кܶ�����ͣ�sameTypesInstanceList����ĳ��term����type֮һ������ͬ��ʵ������
            for (String type:termTypes.getValue()) {
                List<Triple> locakInstance = kbi.hlt(Constants.QUERY_BY_CLASS, type);
                for (Triple triple : locakInstance) {
                    String animTermType = triple.getH().toString();
                    sameTypesInstanceList.add(animTermType);
                }
            }
            terms2InstancesList.put(termTypes.getKey(),sameTypesInstanceList);
        }
        /**
         * fixme ��ȡ����ʵ��������Ȼ��Ƚ����Ƴ̶�ѡ�� ����term ��Ӧ��term
         * termsDesc��terms2InstancesList�Ƚϣ��Ӻ���ѡ��������Ƶ�ʵ��
         */
        Map<String,String> term2Instance = Similarity.findSimInstances(termsDesc,terms2InstancesList);


        /**
         * fixme: �������ݲ�ѯ
         *       1.����term������connectedTerm������ �� ���ǵ���Ϣ��
         *       2.���Ҹ�connnectedTerm������ͬ�ı���֪ʶ���ʵ�壬�ҳ������Ƶ�ʵ��
         * TODO��category �����кܴ�Ĳο�����
         */
        Set<String> connectedTerm = RelationTerm.findConnectedTermList(terms.values());
        Map<String,Set<String>> connectedTermsTypeList = new HashMap<>();
        Map<String,List<Triple>> connectedTermsDesc = new HashMap<>();
        for (String term : connectedTerm) {
            //��ѯterm�����ͣ�Ȼ���ȡ��������Ϣ
            Set<String> termsType = new LinkedHashSet<>();
            List<Triple> classTrips = KnowledgeBaseInvoker.hltNet(Constants.QUERY_TERM_CLASS, term);
            for (Triple triple : classTrips) {
                termsType.add((String) triple.getT());
            }
            connectedTermsTypeList.put(term,termsType);
            //��ѯterm����Ϣ
            List<Triple> infoTriple = KnowledgeBaseInvoker.hltNet(Constants.QUERY_HEAD, term);
            connectedTermsDesc.put(term,infoTriple);
        }
        /**
         * ���ұ��غ�term����ͬ���͵�ʵ��
         */
        Map<String,Set<String>> connectedTerms2InstancesList = new HashMap<>();
        for (Map.Entry<String,Set<String>> termTypes : termsTypeList.entrySet()) {
            Set<String> sameTypesInstanceList = new HashSet<>();
            //һ��term�����кܶ�����ͣ�sameTypesInstanceList����ĳ��term����type֮һ������ͬ��ʵ������
            for (String type:termTypes.getValue()) {
                List<Triple> locakInstance = kbi.hlt(Constants.QUERY_BY_CLASS, type);
                for (Triple triple : locakInstance) {
                    String animTermType = triple.getH().toString();
                    sameTypesInstanceList.add(animTermType);
                }
            }
            connectedTerms2InstancesList.put(termTypes.getKey(),sameTypesInstanceList);
        }
        /**
         * fixme ��ȡ����ʵ��������Ȼ��Ƚ����Ƴ̶�ѡ�� ����term ��Ӧ��term
         * termsDesc��terms2InstancesList�Ƚϣ��Ӻ���ѡ��������Ƶ�ʵ��
         */
        Map<String,String> connectedTerm2Instance = Similarity.findSimInstances(connectedTermsDesc,connectedTerms2InstancesList);
        term2Instance.putAll(connectedTerm2Instance);

        return term2Instance;
    }
}
