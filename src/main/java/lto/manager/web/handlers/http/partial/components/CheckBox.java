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
				.of( label -> {
					String style = "";
					if (options.getHasPadding()) {
						style += "padding:var(--padding);";
					}
					if (options.getFillContainer()) {
						style += "width:100%;";
					}
					if (style.length() > 0) {
						label.attrStyle(style);
					}
				})
				.attrFor(options.getID())
				.input()
					.of(i -> {
						if (options.getKeepBoarderChecked()) {
							i.attrClass("keep-boarder");
						}
					})
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
		private boolean keepBoarderChecked = false;
		private boolean hasPadding = false;
		private boolean fillContainer = false;

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

		public CheckBoxOptions setChecked() {
			this.isChecked = true;
			return this;
		}

		public boolean getChecked() {
			return isChecked;
		}

		public CheckBoxOptions setDisabled(boolean disabled) {
			this.isDisabled = disabled;
			return this;
		}

		public CheckBoxOptions setDisabled() {
			this.isDisabled = true;
			return this;
		}

		public boolean getDisabled() {
			return isDisabled;
		}

		public CheckBoxOptions setKeepBoarderChecked(boolean keepBoarderChecked) {
			this.keepBoarderChecked = keepBoarderChecked;
			return this;
		}

		public CheckBoxOptions setKeepBoarderChecked() {
			this.keepBoarderChecked = true;
			return this;
		}

		public boolean getKeepBoarderChecked() {
			return keepBoarderChecked;
		}

		public CheckBoxOptions setHasPadding(boolean hasPadding) {
			this.hasPadding = hasPadding;
			return this;
		}

		public CheckBoxOptions setHasPadding() {
			this.hasPadding = true;
			return this;
		}

		public boolean getHasPadding() {
			return hasPadding;
		}

		public CheckBoxOptions setFillContainer(boolean fillContainer) {
			this.fillContainer = fillContainer;
			return this;
		}

		public CheckBoxOptions setFillContainer() {
			this.fillContainer = true;
			return this;
		}

		public boolean getFillContainer() {
			return fillContainer;
		}
	}
}
