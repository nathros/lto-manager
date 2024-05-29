package lto.manager.web.check;

public class OperationStatus {
	private String message;
	private CheckStatusType status;
	private Object dynamic;

	private OperationStatus(CheckStatusType status) {
		this.status = status;
	}

	public static OperationStatus undefined() {
		return new OperationStatus(CheckStatusType.UNDEFINED);
	}

	public OperationStatus setMessage(final String message) {
		this.message = message;
		return this;
	}

	public String getMessage() {
		return message;
	}

	public OperationStatus setStatus(final CheckStatusType status) {
		this.status = status;
		return this;
	}

	public CheckStatusType getStatus() {
		return status;
	}

	public boolean statusOK() {
		return status == CheckStatusType.OK;
	}

	public OperationStatus setObject(Object o) {
		this.dynamic = o;
		return this;
	}

	public Object getObject() {
		return dynamic;
	}

	public OperationStatus update(final CheckStatusType status, final String message) {
		this.status = status;
		this.message = message;
		return this;
	}

}
