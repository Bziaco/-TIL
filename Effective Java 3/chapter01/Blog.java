import java.time.*;
import java.time.format.*;

public class Blog implements BlogInterface {
    private String text;
    private String writer;
    private LocalDate date;

    public Blog() {

    }

    public static class Builder {
        private final String text;
        private final String writer;
        private LocalDate date;

        public Builder(String text, String writer) {
            this.text = text;
            this.writer = writer;
        }

        public Builder date(LocalDate val) {
            date = val;
            return this;
        }

        public Blog build() {
            return new Blog(this);
        }

    }

    private Blog(Builder builder) {
        text = builder.text;
        writer = builder.writer;
        date = builder.date;
    }

    @Override
    public String write() {
        Blog blog = new Blog.Builder("자바 빌더 패턴과 인터페이스 정적메서드 선언", "배재환").date(dateFormat()).build();
        String thumbNail = "text: " + blog.text + " writer: " + blog.writer + " date: " + blog.date;
        return thumbNail;
    }

    private LocalDate dateFormat() {
        LocalDate localDate = LocalDate.now();
        return localDate;
    }
}
