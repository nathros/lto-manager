package lto.manager.web.handlers.http.partial.components;

import java.util.List;

import org.xmlet.htmlapifaster.Div;

import lto.manager.web.handlers.http.partial.components.ButtonExtended.ButtonExtendedOptions;
import lto.manager.web.resource.CSS;

public class ButtonExtendedGroup {
	public static Void content(Div<?> view, final ButtonExtendedGroupOptions options) {
		view
			.h2()
				.attrClass(CSS.BUTTON_IMAGE_EXT_GROUP + options.getIconClass() + CSS.BUTTON_IMAGE + CSS.BUTTON_IMAGE_EXT + CSS.BUTTON_IMAGE_W_TEXT)
				.text(options.getTitle())
			.__()
			.hr().__()
			.div()
				.of(div -> {
					for (ButtonExtendedOptions itemOption : options.getItemsList()) {
						ButtonExtended.content(div, itemOption);
					}
				})
			.__(); // div
		return null;
	}

	public static class ButtonExtendedGroupOptions {
		private String title;
		private String iconClass;
		private List<ButtonExtendedOptions> itemsList;

		public static ButtonExtendedGroupOptions of(final String text, final String iconClass, ButtonExtendedOptions... items) {
			return new ButtonExtendedGroupOptions().setTitle(text).setIconClass(iconClass).setItemsList(List.of(items));
		}

		public ButtonExtendedGroupOptions setTitle(final String title) {
			this.title = title;
			return this;
		}

		public String getTitle() {
			return title;
		}

		public ButtonExtendedGroupOptions setIconClass(final String iconClass) {
			this.iconClass = iconClass;
			return this;
		}

		public String getIconClass() {
			return iconClass;
		}

		public ButtonExtendedGroupOptions setItemsList(final List<ButtonExtendedOptions> itemsList) {
			this.itemsList = itemsList;
			return this;
		}

		public List<ButtonExtendedOptions> getItemsList() {
			return itemsList;
		}
	}
}
