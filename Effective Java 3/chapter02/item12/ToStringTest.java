package effectivejava3.chapter03.item12;

import java.util.HashMap;
import java.util.Map;

public class ToStringTest {
    public static void main(String[] args) {
        Map<String, UserInfo> setTest1 = new HashMap<>();

        UserInfo userInfo1 = new UserInfo("010-5673-2501", "서울특별시 관악", 29);
        UserInfo userInfo2 = new UserInfo("010-4904-9801", "서울특별시 영등", 27);

        setTest1.put("first", userInfo1);
        setTest1.put("second", userInfo2);

        System.out.println(setTest1.toString());
        System.out.println(setTest1.get("first").toString());

        System.out.println(setTest1.get("first").getPhoneNum());

    }
}
