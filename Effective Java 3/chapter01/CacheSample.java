import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class CacheSample {
    public static void main(String[] args) {
        // new : hard reference
        // Object key1 = new Object();
        // Object value1 = new Object();

        // ref: WeakHashMap(Weak 레퍼런스)
        // Map<Object, Object> cache = new WeakHashMap<>();

        // cache.put(key1, value1);

        Map<Integer, Integer> map = new HashMap<Integer, Integer>();

        map.put(1, 1);
        map.put(2, 2);

        Map<Integer, Integer> getMap1 = map;
        Map<Integer, Integer> getMap2 = map;

        getMap1.clear();

        System.out.println(getMap1);
        System.out.println(getMap2);

        List<String> list1 = new ArrayList<>();
        list1.add("1");
        list1.add("2");

        List<String> list2 = list1;
        List<String> list3 = list1;

        list2.remove("1");
        System.out.println(list2);
        System.out.println(list3);

    }
}