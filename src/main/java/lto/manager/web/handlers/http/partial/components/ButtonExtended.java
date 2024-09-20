package lto.manager.web.handlers.http.partial.components;

import org.xmlet.htmlapifaster.Div;

import lto.manager.common.database.tables.records.RecordRole.Permission;
import lto.manager.web.resource.CSS;

public class ButtonExtended {
	public static Void content(Div<?> view, final ButtonExtendedOptions options) {
		view
			.a()
				.attrClass(CSS.BUTTON + options.getIconClass() + CSS.BUTTON_IMAGE + CSS.BUTTON_IMAGE_EXT + CSS.BUTTON_IMAGE_W_TEXT)
				.attrHref(options.getHref())
				.div()
					.div()
						.b().text(options.getTitle()).__()
						.text(options.getSubText())
					.__()
					.span().__()
				.__()
				//.text(options.getText())
			.__(); // a
		return null;
	}

	public static class ButtonExtendedOptions {
		private String title;
		private String subText;
		private Permission permission;
		private String href;
		private String iconClass;

		public static ButtonExtendedOptions of(final String text, final String subText, final Permission permission, final String href, final String iconClass) {
			return new ButtonExtendedOptions().setTitle(text).setSubText(subText).setPermission(permission).setHref(href).setIconClass(iconClass);
		}

		public ButtonExtendedOptions setTitle(final String title) {
			this.title = title;
			return this;
		}

		public String getTitle() {
			return title;
		}

		public ButtonExtendedOptions setPermission(final Permission permission) {
			this.permission = permission;
			return this;
		}

		public Permission getPermission() {
			return permission;
		}

		public ButtonExtendedOptions setSubText(final String subText) {
			this.subText = subText;
			return this;
		}

		public String getSubText() {
			return subText;
		}

		public ButtonExtendedOptions setHref(final String href) {
			this.href = href;
			return this;
		}

		public String getHref() {
			return href;
		}

		public ButtonExtendedOptions setIconClass(final String iconClass) {
			this.iconClass = iconClass;
			return this;
		}

		public String getIconClass() {
			return iconClass;
		}
	}
}
