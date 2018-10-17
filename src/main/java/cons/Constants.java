package cons;

/**
 * @author ���޿�
 * @date 2018/10/10
 */
public class Constants {
    /**
     * DBPedia�ڱ��ص�λ��
     */
    public static final String LOD_TDB_DIR = "E:\\TDB\\DBpedia\\";
    /**
     * ����֪ʶ���λ��
     */
    public static final String ANIM_KB_DIR = "E:\\TDB\\KBTDB\\";
    /**
     * ����֪ʶ��owl�ļ�λ��
     */
    public static final String ANIM_OWL_PATH="dbpedia_2014.owl";
    /**
     * �������ڱ���λ��
     */
    public static final String REL_KB_DIR = "E:\\TDB\\RELTDB\\";

    /*=====================================================һ��QUERYͨ��д��========================================================*/
    /**
     * ��ѯ����в������ַ�����ʾ��ֻҪ���ǲ�ѯ�ؼ��ֻ�������query���õ��ľ���.fixme ��ѯ���Ӧ�ø��������
     */
    public static final String QUERY_PARAM = "param";

    /**
     * ��ѯ������Ԫ������λ�õ�����. fixme ��ѯ���Ӧ�ø��������
     */
    public static final String HEAD = "object", LABEL = "pred", TAIL = "val";

    public static final String TERM_TYPE_DESC="http://www.w3.org/1999/02/22-rdf-syntax-ns#type";

    /**
     * ���ֲ�ѯ���ͣ��ֱ𽫲�����Ϊhead��tail��
     * fixme ��ѯʱʹ��QUERY_PARA�������,��ȡ����������Ԫ����ʹ��HEAD��LABEL��TAIL
     */
    public static final String QUERY_HEAD = "select * where{<param> ?pred ?val}";

    public static final String QUERY_TAIL = "select * where{?object ?pred <param>}";

    /**
     * ��ѯterm������ �� ����Ϊterm������
     */
    public static final String QUERY_TERM_CLASS = "select * where{ <param> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?val }";

    public static final String QUERY_BY_CLASS = "select * where{ ?object <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <param> }";

}
