package engine;

import cons.Constants;
import cons.Triple;
import invoke.invoker.KnowledgeBaseInvoker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * �ӱ���֪ʶ��ѡ���ı�ѡ����ѡ����term��Ӧ��ʵ�����ص������Ч��
 * todo��������term���䱸ѡ��һһ��Ӧ������Ӧ��ȫ�濼�ǣ�
 * todo��    1.term�����ϵ�͹�ϵֵ��һ���ռ��������ӱ�ѡ����ѡ���������ƶ�����һ����
 * todo��    2.�ڽ��е�һ��todo֮ǰӦ�þ����ܵĲ�ȫ����֪ʶ��� ��XX��ϵ�� ����һ��ʵ�壻
 * todo��    3.����TransE��˼�룺����ʵ��Ͷ��ʶ�λ���Ĺ�ϵ������ȷ��һ�������Ƶ�ʵ�塪���������������һ��Ѱ��ʵ��ķ�ʽ
 *
 * @author ���޿�
 * @date 2018/10/17
 */
public class Similarity {

//    public static Map<String, String> findSimInstances(Map<String, List<Triple>> termsDesc, Map<String, Set<String>> terms2InstancesList) {
//        Map<String, String> termInstance = new HashMap<>();
//        /**
//         * ����term
//         */
//        for (String term : termsDesc.keySet()) {
//            List<Triple> termInfo = termsDesc.get(term);
//            /**
//             * ����term��Ӧ��instance,fixme ����instance���ҵ������Ƶ�һ��
//             */
//            Set<String> instanceList = terms2InstancesList.get(term);
//            /**
//             * �����ƶ�
//             */long maxScore=Integer.MIN_VALUE;String targetInstance="";
//            for (String instance : instanceList) {
//                long tmpScore;
//                if((tmpScore=calSimValue(termInfo, instance))>maxScore){
//                    maxScore=tmpScore;
//                    targetInstance=instance;
//                }
//            }
//            termInstance.put(term,targetInstance);
//        }
//        return termInstance;
//    }

//    /**
//     * �� term�� instance����ı���ʵ�������ƶȣ�
//     */
//    public static long calSimValue(List<Triple> termInfo, String animInsUri) {
//        long score = 0;
//        KnowledgeBaseInvoker invoker = KnowledgeBaseInvoker.newInvokerBuilder().localDir(Constants.ANIM_KB_DIR).build();
//        List<Triple> animInsInfo = invoker.hlt(Constants.QUERY_HEAD, animInsUri);
//        for (Triple instanceEle : animInsInfo) {
//            String io = (String) instanceEle.getH();
//            String il = (String) instanceEle.getL();
//            String it = (String) instanceEle.getT();
//            for (Triple termEle : termInfo) {
//                String to = (String) termEle.getH();
//                String tl = (String) termEle.getL();
//                String tt = (String) termEle.getT();
//
//                if (il.equals(tl)) {
//                    score++;
//                    score += calValue(il, it, tt);
//                }
//            }
//        }
//        return score;
//    }
//
//    public static long calValue(String relation, String valuex, String valuey) {
//        //������ֱ�ӷ��ؼ���
//        if (valuex.equals(valuey)) {
//            return 1;
//        }
//        /**
//         * ʹ�ù��������䡢�ȵ�
//         */
//        switch (relation) {
//            case "http://dbpedia.org/ontology/birthDate":
//                return Axiom.birthDataAxiom(valuex, valuey);
//            default:
//                return 0;
//        }
//    }
}
