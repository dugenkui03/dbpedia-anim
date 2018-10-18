package engine;

/**
 * 公理
 * @author 杜艮魁
 * @date 2018/10/18
 */
public class Axiom {
    /**
     * 如果同小或者同大则返回1
     */
    public static long birthDataAxiom(String ageX, String ageY) {
        if (((ageX.compareTo("1989-00-00")<0)&&(ageY.compareTo("1989-00-00")<0))
                || ((ageX.compareTo("1989-00-00")>0)&&(ageY.compareTo("1989-00-00")>0))
                ||((ageX.compareTo("1989-00-00")==0)&&(ageY.compareTo("1989-00-00")==0))
                ) {
            return 1;
        }
        return 0;
    }
}
