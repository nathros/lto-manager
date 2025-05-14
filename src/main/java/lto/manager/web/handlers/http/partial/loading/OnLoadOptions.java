package lto.manager.web.handlers.http.partial.loading;

import java.util.Objects;

public record OnLoadOptions(String pathAJAX, String successCallJS, String errorCallJS) {

	public OnLoadOptions {
		Objects.requireNonNull(pathAJAX);
		Objects.requireNonNull(successCallJS);
		Objects.requireNonNull(errorCallJS);
	}

	public static OnLoadOptions of(String pathAJAX, String successCallJS, String errorCallJS) {
		return new OnLoadOptions(pathAJAX, successCallJS, errorCallJS);
	}
}
