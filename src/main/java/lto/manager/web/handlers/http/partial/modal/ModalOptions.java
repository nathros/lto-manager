package lto.manager.web.handlers.http.partial.modal;

public record ModalOptions(String id, String title, boolean enableCross) {

	public static ModalOptions of(String id, String title, boolean enableCross) {
		return new ModalOptions(id, title, enableCross);
	}
}
