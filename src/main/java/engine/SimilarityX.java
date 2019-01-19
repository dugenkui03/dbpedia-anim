package engine;

import cons.Constants;
import cons.Triple;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLIndividual;
import invoke.invoker.InvokerBuilder;
import invoke.invoker.KnowledgeBaseInvoker;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author ���޿�
 * @date 2019/1/17
 */
public class SimilarityX {

    public static Map<String,String> findSimInstances(Map<String, List<Triple>> termsDesc, Map<String, Map<String, List<DefaultOWLIndividual>>> alternativeInstances) {
        System.out.println("alternativeInstances:\t"+alternativeInstances);
        Map<String, String> res = new HashMap<>();

        KnowledgeBaseInvoker kbi = KnowledgeBaseInvoker.newInvokerBuilder().localDir(Constants.ANIM_KB_DIR).build();
        for (String term : termsDesc.keySet()) {
            //term��DBpedia�е�����
            List<Triple> termDesc=termsDesc.get(term);


            //term��Ӧ�Ķ���֪ʶ��ʵ��
            List<DefaultOWLIndividual> animInstanceDesc=new LinkedList<>();
            System.out.println(alternativeInstances);
            System.out.println("term"+term);
            System.out.println(alternativeInstances.get(term));
            alternativeInstances.get(term).values();

            alternativeInstances.get(term).values().forEach(x->{
                x.forEach(ins->{
                    animInstanceDesc.add(ins);
                });
            });

            long maxScore=Long.MIN_VALUE;
            for (DefaultOWLIndividual animInd:animInstanceDesc) {
                long score=calSimValue(termDesc, animInd.getURI());
                if(score>maxScore){
                    maxScore=score;
                    res.put(term,animInd.getURI());
                }else if(score==maxScore){
                    if(System.currentTimeMillis()%2==0){//���
                        res.put(term,animInd.getURI());
                    }
                }
            }
        }

        return res;
    }

    /**
     * �� term�� instance����ı���ʵ�������ƶȣ�
     */
    public static long calSimValue(List<Triple> termInfo, String animInsUri) {
        long score = 0;
        KnowledgeBaseInvoker invoker = KnowledgeBaseInvoker.newInvokerBuilder().localDir(Constants.ANIM_KB_DIR).build();
        List<Triple> animInsInfo = invoker.hlt(Constants.QUERY_HEAD, animInsUri);
        for (Triple instanceEle : animInsInfo) {
            String io = (String) instanceEle.getH();
            String il = (String) instanceEle.getL();
            String it = (String) instanceEle.getT();
            for (Triple termEle : termInfo) {
                String to = (String) termEle.getH();
                String tl = (String) termEle.getL();
                String tt = (String) termEle.getT();

                if (il.equals(tl)) {
                    score++;
                    score += calValue(il, it, tt);
                }
            }
        }
        return score;
    }

    public static long calValue(String relation, String valuex, String valuey) {
        //������ֱ�ӷ��ؼ���
        if (valuex.equals(valuey)) {
            return 1;
        }
        /**
         * ʹ�ù��������䡢�ȵ�
         */
        switch (relation) {
            case "http://dbpedia.org/ontology/birthDate":
                return Axiom.birthDataAxiom(valuex, valuey);
            default:
                return 0;
        }
    }

}
