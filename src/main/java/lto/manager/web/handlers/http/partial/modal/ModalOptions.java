package lto.manager.web.handlers.http.partial.modal;

public record ModalOptions(String id, boolean enableCross) {

	public static ModalOptions of(String id, boolean enableCross) {
		return new ModalOptions(id, enableCross);
	}
}
