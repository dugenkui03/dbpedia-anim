package util;

import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLIndividual;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * ��������ͨ��
 *
 * @author ���޿�
 * @date 2019/1/17
 */
public class CommonUtils {


    /**
     * ���˼����е�ʵ�壬��ֻҪ��׺�� .ma ��β�ĳ�����ģ��
     */
    public static List<DefaultOWLIndividual> getUsefulInstances(Collection<DefaultOWLIndividual> instances) {
        List<DefaultOWLIndividual> res = new LinkedList<>();
        for (DefaultOWLIndividual ins : instances) {
            if (ins.getName().contains(".ma")) {
                res.add(ins);
            }
        }
        return res;
    }
}