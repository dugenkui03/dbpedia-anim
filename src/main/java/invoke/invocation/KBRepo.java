package invoke.invocation;

import org.apache.jena.query.*;
import org.apache.jena.tdb.TDBFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 杜艮魁
 * @date 2018/4/10
 */
public class KBRepo {
    private static final String KB_REPO_DIR="E:\\TDB\\KBTDB\\";


    /**
     * fixme： 我们需要根据他给定的属性或者类型/目录(即属性值）找到与这相关的实体，而本地知识库实体都在三元组首位
     *
     * 找到属性或者属性值为所给参数的实体
     */
    public static List<String> queryKBByperpOrValue(String perpOrValue){
        List<String> result=new ArrayList<>();

        Dataset dataset=null;
        try {
            dataset=TDBFactory.createDataset(KB_REPO_DIR);
            dataset.begin(ReadWrite.READ);

            String str1="SELECT * WHERE { ?obj ?perd " + perpOrValue + "}";
            String str2="SELECT * WHERE { ?obj " + perpOrValue + " ?value}";

            Query query1=QueryFactory.create(str1);
            Query query2=QueryFactory.create(str2);

            QueryExecution execution1=QueryExecutionFactory.create(query1,dataset);
            QueryExecution execution2=QueryExecutionFactory.create(query2,dataset);

            ResultSet res1=execution1.execSelect();
            ResultSet res2 = execution2.execSelect();
            try{
                while (res1.hasNext()) {
//                    System.out.println(res1.getRowNumber());
                    QuerySolution querySolution = res1.nextSolution();
                    //如果term属性集合里边没有，则追加进去
                    if(!result.contains("<" + querySolution.get("obj").toString() + ">")){
                        result.add("<" + querySolution.get("obj").toString() + ">");
                    }
                }
                while (res2.hasNext()) {
                    QuerySolution querySolution=res2.nextSolution();
                    //如果term属性集合里边没有，则追加进去
                    if(!result.contains("<" + querySolution.get("obj").toString() + ">")){
                        result.add("<" + querySolution.get("obj").toString() + ">");
                    }
                }
            }catch (Exception e){
                System.out.println("解析返回结果失败");
                e.printStackTrace();
            }finally {
                execution1.close();
                execution2.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            dataset.end();
        }
        return result;
    }

    /**
     * 返回某个obj的属性和属性值键值对
     */
    public static Map<String,String> queryKBByObj(String obj){
        Map<String,String> propVals=new HashMap<>();

        Dataset dataSet=null;
        QueryExecution execution=null;
        try {
            dataSet=TDBFactory.createDataset(KB_REPO_DIR);
            dataSet.begin(ReadWrite.READ);

            Query query=QueryFactory.create("select * where{ "+obj+" ?prop ?val }");
            execution=QueryExecutionFactory.create(query,dataSet);

            ResultSet resultSet=execution.execSelect();
            while(resultSet.hasNext()){
                QuerySolution sol=resultSet.next();
                propVals.put(sol.get("prop").toString(),sol.get("val").toString());
            }
        } catch (Exception e) {
            execution.close();
            dataSet.end();
            e.printStackTrace();
        }

        return propVals;
    }
}
