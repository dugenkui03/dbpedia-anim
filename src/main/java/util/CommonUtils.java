package util;

import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLIndividual;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * 整体流程通用
 *
 * @author 杜艮魁
 * @date 2019/1/17
 */
public class CommonUtils {


    /**
     * 过滤集合中的实体，即只要后缀以 .ma 结尾的场景和模型
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