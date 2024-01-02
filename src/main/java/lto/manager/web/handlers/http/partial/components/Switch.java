package lto.manager.web.handlers.http.partial.components;

import java.util.Random;

import org.xmlet.htmlapifaster.Div;
import org.xmlet.htmlapifaster.EnumTypeInputType;

import lto.manager.web.resource.HTML;

public class Switch {
	public static Void content(Div<?> view, final SwitchOptions options) {
		view
			.label()
				.attrClass("switch-container")
				.attrFor(options.getID())
				.input()
					.attrType(EnumTypeInputType.CHECKBOX)
					.attrId(options.getID())
					.of(i -> HTML.check(i, options.getChecked()))
					.of(i -> HTML.disabled(i, options.getDisabled()))
				.__()
				.span().__()
			.__(); // label
		return null;
	}

	public static class SwitchOptions {
		private String ID;
		private String text;
		private boolean isChecked = false;
		private boolean isDisabled = false;

		public static SwitchOptions of() {
			return new SwitchOptions();
		}

		public static SwitchOptions of(final String text) {
			return new SwitchOptions().setText(text);
		}

		public SwitchOptions setText(final String text) {
			this.text = text;
			return this;
		}

		public String getText() {
			return text;
		}

		public SwitchOptions setID(final String id) {
			this.ID = id;
			return this;
		}

		public String getID() {
			if (ID == null) {
				ID = String.valueOf(new Random().nextInt());
			}
			return ID;
		}

		public SwitchOptions setChecked(boolean checked) {
			this.isChecked = checked;
			return this;
		}

		public SwitchOptions setChecked() {
			this.isChecked = true;
			return this;
		}

		public boolean getChecked() {
			return isChecked;
		}

		public SwitchOptions setDisabled(boolean disabled) {
			this.isDisabled = disabled;
			return this;
		}

		public boolean getDisabled() {
			return isDisabled;
		}
	}
}
