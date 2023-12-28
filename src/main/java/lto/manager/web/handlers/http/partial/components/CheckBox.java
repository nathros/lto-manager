package lto.manager.web.handlers.http.partial.components;

import java.util.Random;

import org.xmlet.htmlapifaster.Div;
import org.xmlet.htmlapifaster.EnumTypeInputType;

import lto.manager.web.resource.HTML;

public class CheckBox {
	public static Void content(Div<?> view, final CheckBoxOptions options) {
		view
			.label()
				.attrClass("checkbox-container")
				.attrFor(options.getID())
				.input()
					.attrType(EnumTypeInputType.CHECKBOX)
					.attrId(options.getID())
					.of(i -> HTML.check(i, options.getChecked()))
					.of(i -> HTML.disabled(i, options.getDisabled()))
				.__()
				.of(l -> {
					if (options.getText() != null) {
						l.text(options.getText() );
					}
				})
			.__(); // label
		return null;
	}

	public static class CheckBoxOptions {
		private String ID;
		private String text;
		private boolean isChecked = false;
		private boolean isDisabled = false;

		public static CheckBoxOptions of() {
			return new CheckBoxOptions();
		}

		public static CheckBoxOptions of(final String text) {
			return new CheckBoxOptions().setText(text);
		}

		public CheckBoxOptions setText(final String text) {
			this.text = text;
			return this;
		}

		public String getText() {
			return text;
		}

		public CheckBoxOptions setID(final String id) {
			this.ID = id;
			return this;
		}

		public String getID() {
			if (ID == null) {
				ID = String.valueOf(new Random().nextInt());
			}
			return ID;
		}

		public CheckBoxOptions setChecked(boolean checked) {
			this.isChecked = checked;
			return this;
		}

		public boolean getChecked() {
			return isChecked;
		}

		public CheckBoxOptions setDisabled(boolean disabled) {
			this.isDisabled = disabled;
			return this;
		}

		public boolean getDisabled() {
			return isDisabled;
		}
	}
}
