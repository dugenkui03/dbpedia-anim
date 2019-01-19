package test;

import cons.Constants;
import cons.Triple;
import invoke.invoker.KnowledgeBaseInvoker;

import java.sql.SQLOutput;
import java.util.List;

/**
 * @author ¶ÅôÞ¿ý
 * @date 2019/1/18
 */
public class TestNum {
    public static void main(String[] args) {
        List<Triple> tripleList= KnowledgeBaseInvoker.hltNet(Constants.QUERY_TAIL,"http://dbpedia.org/resource/Alfalfa");

        for (Triple triple:tripleList) {
            System.out.println(triple.getH());
            System.out.print("\t"+triple.getL());
            System.out.print("\t"+triple.getT());
        }
    }
}
