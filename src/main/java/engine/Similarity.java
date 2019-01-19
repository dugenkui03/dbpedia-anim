package engine;

import cons.Constants;
import cons.Triple;
import invoke.invoker.KnowledgeBaseInvoker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 从本地知识库选出的备选集中选出与term对应的实例、地点或者特效；
 * todo：现在是term与其备选集一一对应，但是应该全面考虑；
 * todo：    1.term及其关系和关系值是一个空间向量，从备选集中选出余弦相似度最大的一个；
 * todo：    2.在进行第一步todo之前应该尽可能的补全本地知识库的 “XX关系” 的另一端实体；
 * todo：    3.基于TransE的思想：名词实体和动词定位到的关系，可以确定一个最相似的实体——除了搜索外的另一种寻找实体的方式
 *
 * @author 杜艮魁
 * @date 2018/10/17
 */
public class Similarity {

//    public static Map<String, String> findSimInstances(Map<String, List<Triple>> termsDesc, Map<String, Set<String>> terms2InstancesList) {
//        Map<String, String> termInstance = new HashMap<>();
//        /**
//         * 遍历term
//         */
//        for (String term : termsDesc.keySet()) {
//            List<Triple> termInfo = termsDesc.get(term);
//            /**
//             * 遍历term对应的instance,fixme 并从instance中找到最相似的一个
//             */
//            Set<String> instanceList = terms2InstancesList.get(term);
//            /**
//             * 求相似度
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
//     * 求 term和 instance代表的本地实例的相似度：
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
//        //如果相等直接返回即可
//        if (valuex.equals(valuey)) {
//            return 1;
//        }
//        /**
//         * 使用公理处理年龄、等等
//         */
//        switch (relation) {
//            case "http://dbpedia.org/ontology/birthDate":
//                return Axiom.birthDataAxiom(valuex, valuey);
//            default:
//                return 0;
//        }
//    }
}
