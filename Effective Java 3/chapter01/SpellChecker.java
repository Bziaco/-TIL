import java.util.Objects;
import java.util.function.Supplier;

public class SpellChecker {
    private Lexicon dictionary;

    public SpellChecker(Supplier<Lexicon> dictionary) {
        this.dictionary = Objects.requireNonNull(dictionary.get());
    }

    public void isValid() {
        throw new AssertionError();
    }

    public void isBoolean() {
        throw new AssertionError();
    }

    public static void main(String[] args) {
        Lexicon koreanLexicon = new KoreanDictionary();
        SpellChecker spellChecker = new SpellChecker(() -> koreanLexicon);
        spellChecker.isValid();
        spellChecker.dictionary.write();
    }

}

interface Lexicon {
    public void write();
}

class KoreanDictionary implements Lexicon {

    @Override
    public void write() {
        System.out.println("korean");
    }

}
