package main;

import org.junit.Test;

/**
 * https://music.163.com/#/playlist?id=440561933
 */
public class RelationFindTest {

    @Test
    public void testRelFin() throws Exception {
        long now=System.currentTimeMillis();
        RelationFind.relFin("������#nn ����#nn ��ޣ#nn ���#nn");
        System.out.println(System.currentTimeMillis()-now);
    }
}
