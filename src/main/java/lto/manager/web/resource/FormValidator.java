package lto.manager.web.resource;

public class FormValidator {
	private final ValidatorType type;
	private final String message;
	private final ValidatorOptions options;

	public static enum ValidatorType {
		INPUT_TEXT, INPUT_CHECKBOX
	}

	public static enum ValidatorStatusType {
		OK, ERROR
	}

	public static record ValidatorStatus(String input, ValidatorStatusType status, String userMessage) {
		public boolean statusOK() {
			return status == ValidatorStatusType.OK;
		}
	}

	public static class ValidatorOptions {
		public static final int UNSET = -1;

		public boolean valueNotEmpty;
		public boolean valueNotNull;
		public int valueMaxLength = UNSET;
		public int valueExpectedLength = UNSET;

		public static ValidatorOptions of() {
			return new ValidatorOptions();
		}

		public ValidatorOptions valueNotEmpty() {
			this.valueNotEmpty = true;
			return this;
		}

		public ValidatorOptions valueNotNull() {
			this.valueNotNull = true;
			return this;
		}

		public ValidatorOptions valueMaxLength(int valueMaxLength) {
			this.valueMaxLength = valueMaxLength;
			return this;
		}

		public ValidatorOptions valueExpectedLength(int valueExtactLength) {
			this.valueExpectedLength = valueExtactLength;
			return this;
		}
	}

	private void validateText(String value) throws Exception {
		if (options.valueNotNull && value == null) {
			throw new Exception(" has null value");
		}
		if (options.valueNotEmpty) {
			if (value == null) {
				throw new Exception(" has null value");
			} else if (value.equals("")) {
				throw new Exception(" has empty value");
			}
		}
		if (options.valueMaxLength != ValidatorOptions.UNSET) {
			if (value.length() > options.valueMaxLength) {
				throw new Exception(" is too long, max length " + options.valueMaxLength);
			}
		}
		if (options.valueExpectedLength != ValidatorOptions.UNSET) {
			if (value.length() != options.valueExpectedLength) {
				throw new Exception(" length must be " + options.valueExpectedLength + " characters");
			}
		}
	}

	public static FormValidator of(ValidatorType type, ValidatorOptions options, String message) {
		return new FormValidator(type, options, message);
	}

	public FormValidator(ValidatorType type, ValidatorOptions options, String message) {
		this.type = type;
		this.options = options;
		this.message = message;
	}

	public ValidatorStatus validate(String value, boolean enabled) {
		if (enabled) {
			try {
				switch (type) {
				case INPUT_TEXT:
					validateText(value);
					break;
				case INPUT_CHECKBOX: {

				}
				default:
					throw new IllegalArgumentException("Unexpected value: " + type);
				}
			} catch (Exception e) {
				return new ValidatorStatus(value, ValidatorStatusType.ERROR, message + e.getMessage());
			}
		}
		return new ValidatorStatus(value, ValidatorStatusType.OK, null);
	}

	public FormValidator(ValidatorType type, String name, String message, ValidatorOptions options) {
		this.type = type;
		this.message = message;
		this.options = options;
	}

}
