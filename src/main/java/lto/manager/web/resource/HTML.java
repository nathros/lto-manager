package lto.manager.web.resource;

import org.xmlet.htmlapifaster.Input;
import org.xmlet.htmlapifaster.Option;
import org.xmlet.htmlapifaster.Select;
import org.xmlet.htmlapifaster.Textarea;

public class HTML {
	public static final String TARGET_BLANK = "_blank";
	public static final String DISABLE_AUTOCOMPLETE = "new-password";
	public static final String AUTOFOCUS = "autofocus";

	public static final String MODEL_ERR_POSTIX = "-error";

	public static final String DATA_FORM_VALIDATE = "data-form-validate";

	public static Input<?> check(Input<?> input, boolean checked) {
		// input.attrChecked(true) sets checked="true"
		// input.attrChecked(false) sets checked="false"
		// when checked attribute exists browser sets to checked even when value is
		// false
		if (checked) {
			return input.addAttr("checked", "");
		}
		return input;
	}

	public static Input<?> disabled(Input<?> input, boolean disabled) {
		if (disabled) {
			return input.addAttr("disabled", "");
		}
		return input;
	}

	public static Option<?> option(Option<?> option, boolean selected, boolean disabled) {
		if (selected) {
			option = option.addAttr("selected", "");
		}
		if (disabled) {
			option = option.addAttr("disabled", "");
		}
		return option;
	}

	public static Option<?> option(Option<?> option, boolean selected) {
		if (selected) {
			option = option.addAttr("selected", "");
		}
		return option;
	}

	public static Select<?> selectId(Select<?> select, final String id) {
		if (id != null) {
			select = select.attrId(id);
		}
		return select;
	}

	public static Select<?> selectName(Select<?> select, final String name) {
		if (name != null) {
			select = select.attrName(name);
		}
		return select;
	}

	public static Input<?> inputId(Input<?> input, final String id) {
		if (id != null) {
			input = input.attrId(id);
		}
		return input;
	}

	public static Input<?> inputName(Input<?> input, final String name) {
		if (name != null) {
			input = input.attrName(name);
		}
		return input;
	}

	public static Input<?> inputMaxLen(Input<?> input, final Long len) {
		if (len != null) {
			input = input.attrMaxlength(len);
		}
		return input;
	}

	public static Input<?> inputMinLen(Input<?> input, final Long len) {
		if (len != null) {
			input = input.attrMinlength(len);
		}
		return input;
	}

	public static Textarea<?> textArea(Textarea<?> textArea, boolean readOnly) {
		if (readOnly) {
			textArea = textArea.addAttr("readonly", "");
		}
		return textArea;
	}

}
