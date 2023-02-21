package lto.manager.web.handlers.http.templates.models;

public class EmptyModel {
	private EmptyModel() {}

	public static EmptyModel of() {
		return new EmptyModel();
	}
}