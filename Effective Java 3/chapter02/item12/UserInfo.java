package effectivejava3.chapter03.item12;

public class UserInfo {
    private static String phoneNum;
    private static String address;
    private static int age;

    public UserInfo(String phoneNum, String address, int age) {
        this.phoneNum = phoneNum;
        this.address = address;
        this.age = age;
    }

    public static String getPhoneNum() {
        return phoneNum;
    }

    public static String getAddress() {
        return String.format("%03d-$03d-%04d");
    }

    public static int age() {
        return age;
    }

    @Override
    public String toString() {
        return String.format(phoneNum);
        // return "PhoneNum: " + phoneNum + "address: " + address + "age: " + age;
    }

}
