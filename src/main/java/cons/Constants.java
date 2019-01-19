package cons;

import java.util.Arrays;
import java.util.List;

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
    public static final String ANIM_KB_DIR = "C:\\ontologyOWL\\sumoOWL2\\sumo_phone3.owl";
    /**
     * �Ž�����λ��
     */
    public static final String RELATION_KB_PATH = "C:\\ontologyOWL\\dbpediarelationoutput\\dbpedia_2014.owl";
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

    public static final String TERM_TYPE_DESC = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";

    /**
     * ���ֲ�ѯ���ͣ��ֱ𽫲�����Ϊhead��tail��
     * fixme ��ѯʱʹ��QUERY_PARA�������,��ȡ����������Ԫ����ʹ��HEAD��LABEL��TAIL
     */
    public static final String QUERY_HEAD = "select * where{<param> ?pred ?val}";

//    public static final String QUERY_REL = "select * where{?object <param> ?val}";

    public static final String QUERY_TAIL = "select * where{?object ?pred <param>}";

    /**
     * ��ѯterm������ �� ����Ϊterm������
     */
    public static final String QUERY_TERM_CLASS = "select * where{ <param> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?val }";

    public static final String QUERY_BY_CLASS = "select * where{ ?object <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <param> }";

    /*=====================================================֪ʶ�����ݱ�ʶ========================================================*/
    public static final List<String> ANIM_TERM_PREFIX = Arrays.asList("http://www.owl-ontologies.com", "http://www.ontologyportal.org", "http://swrl.stanford.edu");

    public static final List<String> DBPEDIA_BIG_TYPE =
            Arrays.asList("http://dbpedia.org/ontology/Agent","http://dbpedia.org/ontology/Person","http://dbpedia.org/ontology/Athlete","http://dbpedia.org/ontology/SportsTeamMember","http://dbpedia.org/ontology/SoccerPlayer"
                        ,"http://dbpedia.org/ontology/Place","http://dbpedia.org/ontology/PopulatedPlace","http://dbpedia.org/ontology/Location","http://dbpedia.org/ontology/Work"
                        ,"http://dbpedia.org/ontology/Species","http://dbpedia.org/ontology/Animal","http://dbpedia.org/ontology/Building");




    /*=====================================================֪ʶ��model��ʽ·��========================================================*/

    public static final String ANIM_MODEL_PATH = "file:///C:/ontologyOWL/sumoOWL2/sumo_phone3.owl";

    public static final String RELATION_MODEL_PATH = "file:///C:/ontologyOWL/dbpediarelationoutput/dbpedia_2014.owl";

}
