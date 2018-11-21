package ontologymatching;

import java.util.Arrays;
import java.util.List;

/**
 * @author ¶ÅôÞ¿ý
 * @date 2018/11/20
 */
public class Constants {

    public static final String KB_LOCATION="file:///c:/ontologyOWL/dbpediaschema/dbpedia_2014.owl";

    public static final String TOP_CLASS="owl:Thing";

    public static final List<String> ANIMCLZ_EQUAL_DBPEDIACLZ= Arrays.asList("equal","wordNet_syno","thesuaru_syno");

    public static final List<String> ANIMCLZ_EQUAL_DBPEDIAINS=Arrays.asList("animClz2Entity","animWordNetsynoClz2Entity","animThesuaruSynoClz2Entity");

    /*======================for demo============================*/

    public static final String DEMO_CLASS="UnitOfWork";
}
