package engine;

/**
 * ����
 * @author ���޿�
 * @date 2018/10/18
 */
public class Axiom {
    /**
     * ���ͬС����ͬ���򷵻�1
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
