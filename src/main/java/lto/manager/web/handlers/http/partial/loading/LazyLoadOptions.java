package lto.manager.web.handlers.http.partial.loading;

import java.util.Objects;

public record LazyLoadOptions(String pathAJAX, String successCallJS, String errorCallJS) {

	public LazyLoadOptions {
		Objects.requireNonNull(pathAJAX);
		Objects.requireNonNull(successCallJS);
		Objects.requireNonNull(errorCallJS);
	}

	public static LazyLoadOptions of(String pathAJAX, String successCallJS, String errorCallJS) {
		return new LazyLoadOptions(pathAJAX, successCallJS, errorCallJS);
	}
}
