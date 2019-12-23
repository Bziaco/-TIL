package EnumGrouping;

public class BlogPost {
	private String title;
	private String auth;
	private BlogPostType type;
	private int likes;
	
	public BlogPost(String title,  String auth, BlogPostType type, int likes) {
		this.title = title;
		this.auth = auth;
		this.type = type;
		this.likes = likes;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuth() {
		return auth;
	}

	public void setAuth(String auth) {
		this.auth = auth;
	}

	public BlogPostType getType() {
		return type;
	}

	public void setType(BlogPostType type) {
		this.type = type;
	}

	public int getLikes() {
		return likes;
	}

	public void setLikes(int likes) {
		this.likes = likes;
	}
	
	@Override
	public String toString() {
		return this.getTitle() + " - " + this.getAuth();
	}
}
