package lto.manager.web.check;

import lto.manager.web.check.element.FormElement.FormElementType;

public class FormValidator {
	private final ValidatorOptions options;

	private String messagePrefix;
	private String messagePostfix;

	public static class ValidatorStatus extends Exception {
		private static final long serialVersionUID = 1L;
		private final CheckStatusType status;
		private final String validatorMessage;

		public ValidatorStatus(CheckStatusType status, final String generatedMessage) {
			this.status = status;
			this.validatorMessage = generatedMessage;
		}

		public static ValidatorStatus emptyOK() {
			return new ValidatorStatus(CheckStatusType.OK, "");
		}

		public boolean statusOK() {
			return status == CheckStatusType.OK;
		}

		public CheckStatusType getStatus() {
			return status;
		}

		public String getValidatorMessage() {
			return validatorMessage;
		}
	}

	public static class ValidatorOptions {
		public static final int UNSET = -1;

		public boolean valueNotEmpty;
		public boolean valueNotNull;
		public long valueMaxLength = UNSET;
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

		public ValidatorOptions valueMaxLength(long valueMaxLength) {
			this.valueMaxLength = valueMaxLength;
			return this;
		}

		public ValidatorOptions valueExpectedLength(int valueExtactLength) {
			this.valueExpectedLength = valueExtactLength;
			return this;
		}
	}

	private String genMgs(String baseMsg) {
		if (messagePrefix == null) {
			baseMsg = baseMsg.toLowerCase();
		}
		return messagePrefix + baseMsg + messagePostfix;
	}

	private void validateText(String value) throws Exception {
		if (options.valueNotNull && value == null) {
			throw new Exception(genMgs("Value cannot be null"));
		}
		if (options.valueNotEmpty) {
			if (value == null) {
				throw new Exception(genMgs("Value cannot be null"));
			} else if (value.equals("")) {
				throw new Exception(genMgs("Value cannot be empty"));
			}
		}
		if (options.valueMaxLength != ValidatorOptions.UNSET) {
			if (value.length() > options.valueMaxLength) {
				throw new Exception(genMgs("value is too long, max length " + options.valueMaxLength));
			}
		}
		if (options.valueExpectedLength != ValidatorOptions.UNSET) {
			if (value.length() != options.valueExpectedLength) {
				throw new Exception(genMgs("value is too short, length must be " + options.valueExpectedLength + " characters"));
			}
		}
	}

	public static FormValidator of(ValidatorOptions options, String message) {
		return new FormValidator(options, message);
	}

	public static FormValidator ofDefault() {
		return new FormValidator(ValidatorOptions.of(), "");
	}

	public FormValidator(ValidatorOptions options, String message) {
		this.options = options;
	}

	public ValidatorOptions getOptions() {
		return options;
	}

	public void setMessage(final String prefix, final String postfix) {
		this.messagePrefix = prefix;
		this.messagePostfix = postfix;
	}

	public ValidatorStatus validateText(final String value, boolean enabled) {
		if (enabled) {
			try {
				validateText(value);
			} catch (Exception e) {
				return new ValidatorStatus(CheckStatusType.ERROR, e.getMessage());
			}
		}
		return ValidatorStatus.emptyOK();
	}

	public String validateThrow(FormElementType type, String value, boolean enabled) throws ValidatorStatus {
		if (enabled) {
			try {
				switch (type) {
				case INPUT_TEXT:
					validateText(value);
					break;
				case INPUT_CHECKBOX: {
					break;
				}
				case INPUT_PASSWORD: {
					break;
				}
				default:
					throw new IllegalArgumentException("Unexpected value: " + type);
				}
			} catch (Exception e) {
				 throw new ValidatorStatus(CheckStatusType.ERROR, e.getMessage());
			}
		}
		return value;
	}

	public ValidatorStatus validatePassword(FormElementType type, final String password, final String passwordConfirm, boolean enabled) {
		if (password != null) {
			if (password.equals(passwordConfirm)) {
				return validateText(password, enabled);
			} else {
				return new ValidatorStatus(CheckStatusType.ERROR, "Passwords do no match");
			}
		} else if (passwordConfirm != null) {
			if (passwordConfirm.equals(password)) {
				return validateText(passwordConfirm, enabled);
			} else {
				return new ValidatorStatus(CheckStatusType.ERROR, "Passwords do not match");
			}
		}

		return ValidatorStatus.emptyOK(); // Password empty ignore
	}

}
