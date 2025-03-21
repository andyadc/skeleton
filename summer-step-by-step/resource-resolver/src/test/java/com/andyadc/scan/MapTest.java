package com.andyadc.scan;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class MapTest {

    @Test
    public void testRemove() {
        Map<String, String> map = new HashMap<>();
        map.put("name", "aa");
        map.put("alias", "aa");
        System.out.println(map.remove("age"));
        System.out.println(map.remove("name"));
        System.out.println(map);
    }
}
