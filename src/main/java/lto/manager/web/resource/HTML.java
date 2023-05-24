package lto.manager.web.resource;

import org.xmlet.htmlapifaster.Input;
import org.xmlet.htmlapifaster.Option;

public class HTML {
	public static Input<?> check(Input<?> input, boolean checked) {
		// input.attrChecked(true) sets checked="true"
		// input.attrChecked(false) sets checked="false"
		// when checked attribute exists browser sets to checked even when value is false
		if (checked) {
			return input.addAttr("checked", "");
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
}
