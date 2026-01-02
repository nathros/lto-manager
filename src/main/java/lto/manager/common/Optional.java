package lto.manager.common;

// This is wrapper of java.util.Optional which allows use in HtmlFlow
public class Optional<T> {
	private java.util.Optional<T> opt;

	public Optional(java.util.Optional<T> t) {
		opt = t;
	}

	public Optional(T t) {
		opt = java.util.Optional.of(t);
	}

	public void set(T t) {
		opt = java.util.Optional.of(t);
	}

	public T get() {
		return opt.get();
	}

	public boolean isEmpty() {
		return opt.isEmpty();
	}

	public boolean isPresent() {
		return opt.isPresent();
	}

	public static <T> Optional<T> empty() {
		Optional<T> t = new Optional<>(java.util.Optional.empty());
		return t;
	}
}
