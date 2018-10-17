package cons;

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
    public static final String ANIM_KB_DIR = "E:\\TDB\\KBTDB\\";
    /**
     * 动画知识库owl文件位置
     */
    public static final String ANIM_OWL_PATH="dbpedia_2014.owl";
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

    public static final String TERM_TYPE_DESC="http://www.w3.org/1999/02/22-rdf-syntax-ns#type";

    /**
     * 两种查询类型，分别将参数作为head和tail。
     * fixme 查询时使用QUERY_PARA参数替代,获取结果如果是三元组则使用HEAD、LABEL和TAIL
     */
    public static final String QUERY_HEAD = "select * where{<param> ?pred ?val}";

    public static final String QUERY_TAIL = "select * where{?object ?pred <param>}";

    /**
     * 查询term的类型 和 类型为term的数据
     */
    public static final String QUERY_TERM_CLASS = "select * where{ <param> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?val }";

    public static final String QUERY_BY_CLASS = "select * where{ ?object <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <param> }";

}
