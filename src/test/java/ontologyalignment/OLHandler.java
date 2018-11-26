package ontologyalignment;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import ontologymatching.Constants;
import ontologymatching.OMUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 杜艮魁
 * @date 2018/11/23
 */
public class OLHandler {
    public static void main(String[] args) {

        /**
         * 获取匹配数据放进List<String[]>中
         */
        List<String[]> lineInfo = new LinkedList<>();
        try {
            File file = new File("knowledgebase/finalMatchedAnim");
            BufferedReader bf = new BufferedReader(new FileReader(file));
            String line = "";
            while ((line = bf.readLine()) != null) {
                lineInfo.add(line.split(";"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        /**
         * 获取知识库对应的OWLModel对象
         */
        OWLModel owlModel = OLUtils.getOwlModel("file:///C:/ontologyOWL/AllOwlFile/sumoOWL2/sumo_phone3.owl");
        RDFProperty equivalentClassProp = owlModel.getRDFProperty("http://www.w3.org/2002/07/owl#equivalentClass");

        /**
         * 遍历所有的动画类，分如下情况处理：
         *      1. 如果有等价的DBpedia类，则：
         *          1）设置类之间的等价关系；
         *          2）对方的子孙类互为自己的子孙类；
         *          3）引入DBpedia类的实例：
         *              [1].
         *      2. 如果是动画类匹配到DBpedia实例：创建实例即可。
         */
        for (String [] eleArr:lineInfo.stream().filter(x->x.length>2).collect(Collectors.toList())) {
            /**
             * 获取匹配数据中的animUri和dbpediaUri和匹配类型(主要指类类匹配还是类实例匹配)
             */
            String animUri = eleArr[0].substring(21, eleArr[0].length() - 1);
            String dbpediaUri,matchedType;
            if(eleArr.length==3){
                dbpediaUri = eleArr[2].substring(1, eleArr[2].length() - 1);
                matchedType= eleArr[1].substring(1, eleArr[1].length() - 1);
            }else{
                dbpediaUri = eleArr[3].substring(1, eleArr[3].length() - 1);
                matchedType = eleArr[2].substring(1, eleArr[2].length() - 1);
            }

            /**
             * 指定等价关系并且将动画类的子类在DBpedia类中创建并且设置为其子类 todo 匹配有误差的数据，数据类型不能用来进行匹配
             */
            if (dbpediaUri.contains("http://dbpedia.org/datatype")) {
                continue;
            }

            /**
             * 如果是类类之间的匹配
             */
            if(Constants.ANIMCLZ_EQUAL_DBPEDIACLZ.contains(matchedType)){
                //获取导入DBpedia-Schema库的动画知识库中的animClz和dbpediaClz
                OWLNamedClass animClz = owlModel.getOWLNamedClass(animUri), dbpediaClz = owlModel.getOWLNamedClass(dbpediaUri);

                //todo 设置父子类关系

                //指定等价关系 todo 必须先指定子类，然后指定等价关系，否则出错
                animClz.setPropertyValue(equivalentClassProp, dbpediaClz);
            }
            /**
             * 如果是动画类和DBpedia实例之间的匹配
             */
            else{
                //todo
            }
        }

        OLUtils.saveOwlModel2File(owlModel);
    }
}
