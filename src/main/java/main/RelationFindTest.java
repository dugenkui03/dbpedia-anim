package main;

import org.junit.Test;

/**
 * https://music.163.com/#/playlist?id=440561933
 */
public class RelationFindTest {

    @Test
    public void testRelFin() throws Exception {
        RelationFind.relFin("海明威#nn 鲸鱼#nn 梧桐#nn 全聚德#nn");
    }
}
