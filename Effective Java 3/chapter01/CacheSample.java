import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class CacheSample {
    public static void main(String[] args) {
        // new : hard reference
        Object key1 = new Object();
        Object value1 = new Object();

        // ref: WeakHashMap(Weak 레퍼런스)
        Map<Object, Object> cache = new WeakHashMap<>();
        cache.put(key1, value1);
    }
}