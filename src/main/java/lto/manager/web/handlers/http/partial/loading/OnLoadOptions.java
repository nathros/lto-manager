package lto.manager.web.handlers.http.partial.loading;

public record OnLoadOptions(String pathAJAX) {

	public static OnLoadOptions of(String pathAJAX) {
		return new OnLoadOptions(pathAJAX);
	}
}
