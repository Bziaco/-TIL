package effectivejava3.chapter03.item11;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HashCodeTest {
    public static void main(String[] args) {

        Set<HashCodeSample> hsSet = new HashSet<>();

        hsSet.add(new HashCodeSample("first", "second"));

        System.out.println(hsSet.contains(new HashCodeSample("first", "second")));

        HashCodeSample hs1 = new HashCodeSample("first", "second");
        HashCodeSample hs2 = new HashCodeSample("first", "second");

        Map<HashCodeSample, String> hsMap = new HashMap<>();

        hsMap.put(hs1, "1");
        hsMap.put(hs2, "2");

        System.out.println(hsMap.size());

        System.out.println(hsMap.keySet());

    }
}
