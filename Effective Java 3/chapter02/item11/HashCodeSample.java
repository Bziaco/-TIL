package effectivejava3.chapter03.item11;

public class HashCodeSample {
    private final String first, second;

    public HashCodeSample(String first, String second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof HashCodeSample)) {
            return false;
        }
        HashCodeSample hc = (HashCodeSample) o;
        return hc.first == first && hc.second == second;
    }

    @Override
    public int hashCode() {
        int result = 31 * first.hashCode();
        result = 31 * result + second.hashCode();
        return result;
    }

}
