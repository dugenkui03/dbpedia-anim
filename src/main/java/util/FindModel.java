package util;

import invoke.invocation.KBRepo;
import invoke.invocation.RelationRepo;
import invoke.invoker.KnowledgeBaseInvoker;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * @author 杜艮魁
 * @date 2018/4/10
 */
public class FindModel {
    /**
     *  参数是<term，term属性及相关属性集合，返回值是<term,与term最相关的模型>，fixme：权值相同的可能有多个，用“今日时间种子"随机选择
     * @param termInfoMap
     * @return
     */
    public static Map<String,List<String>> findModel(Map<String,List<String>> termInfoMap){
        Map<String,List<String>> result=new HashMap<>();

        Set<String> keys=termInfoMap.keySet();
        for (String key:keys) {//key是term，values是其属性集合
            List<String> models=new LinkedList<>();//与term有关的model

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

                Map<String,Long> modelNumOrder=new LinkedHashMap<>();//fixme LinkedHash才能保证顺序
                modelNum.entrySet().stream()
                        .sorted(Map.Entry.<String,Long>comparingByValue()
                        .reversed()).forEachOrdered(e->modelNumOrder.put(e.getKey(),e.getValue()));

                Long max=modelNumOrder.values().iterator().next();
                List<String> modelsMax=new LinkedList<>();//与term有关的model,如果有的出现多次，则选择出现次数最多的
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
     *  相似度=(func(交集))/|模型集合模长|，其中func代表交集个数，而且交集值相等+2，交集值不等+1；
     * @param termModels key是term，value是与其相似的model
     * @return 返回取其最相似的模型
     */
    public static Map<String,String> calSimil(Map<String,List<String>> termModels){
        Map<String,String> termModel=new HashMap<>();

        Iterator<String> keys=termModels.keySet().iterator();
        while(keys.hasNext()){//fixme 遍历每个term，找到与其最相近的model
            String term=keys.next();
            List<String> models=termModels.get(term);

            //查找每个model的信息集合,然后与term比较，将model及其相似度放在Map similary中
            int maxSiml=-1;
            String maxModel="";
            for (String model:models) {//fixme 遍历每个model，并计算与term相似度，在最大的里边随机选择一个
                Map<String,String> termPropVals=KnowledgeBaseInvoker.termLabelTail(term);//fixme key是属性值，valus是属性，因为属性有重复的
                Map<String,String> modelPropVals=KBRepo.queryKBByObj(model);
                //todo 在这里计算相似度
                int similarity=mapSimil(termPropVals,modelPropVals);
                if(similarity>maxSiml){//如果相似度比之前的最大相似度大，则修改最大相似度，并替换加入result中的model
                    maxSiml=similarity;
                    maxModel=model;
                }else if(similarity==maxSiml){//如果相似度相同，随机选择一个
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
     * 计算两个map的相似度:
     * fixme term的key是属性值，valus是属性，因为属性有重复的
     * fixme model的key是属性，value是属性值
     * @return
     */
    private static int mapSimil(Map<String,String> termPropVals,Map<String,String> modelPropVals){
        int similarity=0;

        Set<String> modelPreds=modelPropVals.keySet();
        for (String modelPred:modelPreds) {//fixme 遍历model的属性值

            Iterator<String> termPreds=termPropVals.values().iterator();
            while(termPreds.hasNext()){//fixme 遍历term的每个属性
                int flag=similarity;//用作查看每次遍历其值是否改变
                String termPred=termPreds.next();
                //如果这两个属性相等或者是等价的,则查看其属性值是否相等或者等价
                if(modelPred.equals(termPred)||RelationRepo.sameAs(modelPred,termPred)){
                    String modelVal=modelPropVals.get(modelPred);//model属性值
                    for (String xx:termPropVals.keySet()) {//fixme 遍历term属性值
                        if (termPropVals.get(xx).equals(termPred)){//如果遍历到了要进行比较termPred
                            // 而且：与model属性值相同或者等价
                            if(xx.equals(modelVal) || RelationRepo.sameAs(modelVal,xx )){//fixme sameAs有顺序，KB库实体在前，term在后
                                similarity+=2;
                                break;
                            }
                            //如果属性是年龄
                            else if(termPropVals.get(xx).equals("http://dbpedia.org/ontology/birthDate")){
//                                if(modelVal.equals("1990-01-01")&&xx.compareTo()){
//
//                                }
                                //todo 待处理，因为一般都是老人，先按照老人处理
                                if (modelVal.equals("1950-01-01")){//如果是老人，相似度+2返回
                                    similarity+=2;
                                    break;
                                }
                                similarity+=1;
                                break;
                            }
                            //如果属性相同而属性值不同，而且不是type也不是类目属性，那么similarity+=1
                            else if(!termPropVals.get(xx).equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")
                                    &&!termPropVals.get(xx).equals("http://purl.org/dc/terms/subject")){
                                similarity++;
                                break;
                            }
                        }
                    }
                }
                if(flag!=similarity){//如果相似度值有改变，跳出此次循环
                    break;
                }
            }
        }

        return similarity;
    }


}
