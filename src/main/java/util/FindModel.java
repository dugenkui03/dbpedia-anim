package util;

import invoke.invocation.KBRepo;
import invoke.invocation.RelationRepo;
import invoke.invoker.KnowledgeBaseInvoker;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * @author ���޿�
 * @date 2018/4/10
 */
public class FindModel {
    /**
     *  ������<term��term���Լ�������Լ��ϣ�����ֵ��<term,��term����ص�ģ��>��fixme��Ȩֵ��ͬ�Ŀ����ж�����á�����ʱ������"���ѡ��
     * @param termInfoMap
     * @return
     */
    public static Map<String,List<String>> findModel(Map<String,List<String>> termInfoMap){
        Map<String,List<String>> result=new HashMap<>();

        Set<String> keys=termInfoMap.keySet();
        for (String key:keys) {//key��term��values�������Լ���
            List<String> models=new LinkedList<>();//��term�йص�model

            List<String> props=termInfoMap.get(key);
            for (String prop:props) {
                List<String> resp=KBRepo.queryKBByperpOrValue(prop);
                if(resp.size()!=0){
                    models.addAll(resp);
                }
            }
            if(models.size()!=0){
                Map<String,Long> modelNum=
                        models.stream().collect(Collectors.groupingBy(Function.identity(),Collectors.counting()));

                Map<String,Long> modelNumOrder=new LinkedHashMap<>();//fixme LinkedHash���ܱ�֤˳��
                modelNum.entrySet().stream()
                        .sorted(Map.Entry.<String,Long>comparingByValue()
                        .reversed()).forEachOrdered(e->modelNumOrder.put(e.getKey(),e.getValue()));

                Long max=modelNumOrder.values().iterator().next();
                List<String> modelsMax=new LinkedList<>();//��term�йص�model,����еĳ��ֶ�Σ���ѡ����ִ�������
                for (String ele:modelNumOrder.keySet()) {
                    if(modelNumOrder.get(ele).equals(max)){
                        modelsMax.add(ele);
                    }
                }
                result.put(key,modelsMax);
            }
        }
        return result;
    }


    /**
     *  ���ƶ�=(func(����))/|ģ�ͼ���ģ��|������func���������������ҽ���ֵ���+2������ֵ����+1��
     * @param termModels key��term��value���������Ƶ�model
     * @return ����ȡ�������Ƶ�ģ��
     */
    public static Map<String,String> calSimil(Map<String,List<String>> termModels){
        Map<String,String> termModel=new HashMap<>();

        Iterator<String> keys=termModels.keySet().iterator();
        while(keys.hasNext()){//fixme ����ÿ��term���ҵ������������model
            String term=keys.next();
            List<String> models=termModels.get(term);

            //����ÿ��model����Ϣ����,Ȼ����term�Ƚϣ���model�������ƶȷ���Map similary��
            int maxSiml=-1;
            String maxModel="";
            for (String model:models) {//fixme ����ÿ��model����������term���ƶȣ�������������ѡ��һ��
                Map<String,String> termPropVals=KnowledgeBaseInvoker.termLabelTail(term);//fixme key������ֵ��valus�����ԣ���Ϊ�������ظ���
                Map<String,String> modelPropVals=KBRepo.queryKBByObj(model);
                //todo ������������ƶ�
                int similarity=mapSimil(termPropVals,modelPropVals);
                if(similarity>maxSiml){//������ƶȱ�֮ǰ��������ƶȴ����޸�������ƶȣ����滻����result�е�model
                    maxSiml=similarity;
                    maxModel=model;
                }else if(similarity==maxSiml){//������ƶ���ͬ�����ѡ��һ��
                    if(System.currentTimeMillis() % 2==0){
                        maxModel=model;
                    }
                }
            }
            termModel.put(term,maxModel);
        }
        return termModel;
    }

    /**
     * ��������map�����ƶ�:
     * fixme term��key������ֵ��valus�����ԣ���Ϊ�������ظ���
     * fixme model��key�����ԣ�value������ֵ
     * @return
     */
    private static int mapSimil(Map<String,String> termPropVals,Map<String,String> modelPropVals){
        int similarity=0;

        Set<String> modelPreds=modelPropVals.keySet();
        for (String modelPred:modelPreds) {//fixme ����model������ֵ

            Iterator<String> termPreds=termPropVals.values().iterator();
            while(termPreds.hasNext()){//fixme ����term��ÿ������
                int flag=similarity;//�����鿴ÿ�α�����ֵ�Ƿ�ı�
                String termPred=termPreds.next();
                //���������������Ȼ����ǵȼ۵�,��鿴������ֵ�Ƿ���Ȼ��ߵȼ�
                if(modelPred.equals(termPred)||RelationRepo.sameAs(modelPred,termPred)){
                    String modelVal=modelPropVals.get(modelPred);//model����ֵ
                    for (String xx:termPropVals.keySet()) {//fixme ����term����ֵ
                        if (termPropVals.get(xx).equals(termPred)){//�����������Ҫ���бȽ�termPred
                            // ���ң���model����ֵ��ͬ���ߵȼ�
                            if(xx.equals(modelVal) || RelationRepo.sameAs(modelVal,xx )){//fixme sameAs��˳��KB��ʵ����ǰ��term�ں�
                                similarity+=2;
                                break;
                            }
                            //�������������
                            else if(termPropVals.get(xx).equals("http://dbpedia.org/ontology/birthDate")){
//                                if(modelVal.equals("1990-01-01")&&xx.compareTo()){
//
//                                }
                                //todo ��������Ϊһ�㶼�����ˣ��Ȱ������˴���
                                if (modelVal.equals("1950-01-01")){//��������ˣ����ƶ�+2����
                                    similarity+=2;
                                    break;
                                }
                                similarity+=1;
                                break;
                            }
                            //���������ͬ������ֵ��ͬ�����Ҳ���typeҲ������Ŀ���ԣ���ôsimilarity+=1
                            else if(!termPropVals.get(xx).equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")
                                    &&!termPropVals.get(xx).equals("http://purl.org/dc/terms/subject")){
                                similarity++;
                                break;
                            }
                        }
                    }
                }
                if(flag!=similarity){//������ƶ�ֵ�иı䣬�����˴�ѭ��
                    break;
                }
            }
        }

        return similarity;
    }


}
