package cons;

import java.util.Arrays;
import java.util.List;

/**
 * @author 杜艮魁
 * @date 2018/10/10
 */
public class Constants {
    /**
     * DBPedia在本地的位置
     */
    public static final String LOD_TDB_DIR = "E:\\TDB\\DBpedia\\";
    /**
     * 动画知识库的位置
     */
    public static final String ANIM_KB_DIR = "C:\\ontologyOWL\\sumoOWL2\\sumo_phone3.owl";
    /**
     * 桥接数据位置
     */
    public static final String RELATION_KB_PATH = "C:\\ontologyOWL\\dbpediarelationoutput\\dbpedia_2014.owl";
    /**
     * 关联库在本地位置
     */
    public static final String REL_KB_DIR = "E:\\TDB\\RELTDB\\";

    /*=====================================================一般QUERY通用写法========================================================*/
    /**
     * 查询语句中参数的字符串表示，只要不是查询关键字或者其他query中用到的就行.fixme 查询语句应该跟这个对齐
     */
    public static final String QUERY_PARAM = "param";

    /**
     * 查询锁的三元组三个位置的命名. fixme 查询语句应该跟这个对齐
     */
    public static final String HEAD = "object", LABEL = "pred", TAIL = "val";

    public static final String TERM_TYPE_DESC = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";

    /**
     * 两种查询类型，分别将参数作为head和tail。
     * fixme 查询时使用QUERY_PARA参数替代,获取结果如果是三元组则使用HEAD、LABEL和TAIL
     */
    public static final String QUERY_HEAD = "select * where{<param> ?pred ?val}";

//    public static final String QUERY_REL = "select * where{?object <param> ?val}";

    public static final String QUERY_TAIL = "select * where{?object ?pred <param>}";

    /**
     * 查询term的类型 和 类型为term的数据
     */
    public static final String QUERY_TERM_CLASS = "select * where{ <param> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?val }";

    public static final String QUERY_BY_CLASS = "select * where{ ?object <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <param> }";

    /*=====================================================知识库数据标识========================================================*/
    public static final List<String> ANIM_TERM_PREFIX = Arrays.asList("http://www.owl-ontologies.com", "http://www.ontologyportal.org", "http://swrl.stanford.edu");

    public static final List<String> DBPEDIA_BIG_TYPE =
            Arrays.asList("http://dbpedia.org/ontology/Agent","http://dbpedia.org/ontology/Person","http://dbpedia.org/ontology/Athlete","http://dbpedia.org/ontology/SportsTeamMember","http://dbpedia.org/ontology/SoccerPlayer"
                        ,"http://dbpedia.org/ontology/Place","http://dbpedia.org/ontology/PopulatedPlace","http://dbpedia.org/ontology/Location","http://dbpedia.org/ontology/Work"
                        ,"http://dbpedia.org/ontology/Species","http://dbpedia.org/ontology/Animal","http://dbpedia.org/ontology/Building");




    /*=====================================================知识库model形式路径========================================================*/

    public static final String ANIM_MODEL_PATH = "file:///C:/ontologyOWL/sumoOWL2/sumo_phone3.owl";

    public static final String RELATION_MODEL_PATH = "file:///C:/ontologyOWL/dbpediarelationoutput/dbpedia_2014.owl";

}
