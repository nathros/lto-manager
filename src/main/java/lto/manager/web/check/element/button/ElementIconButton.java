package lto.manager.web.check.element.button;

import org.xmlet.htmlapifaster.Div;
import org.xmlet.htmlapifaster.EnumTypeButtonType;

import lto.manager.web.resource.CSS;
import lto.manager.web.resource.Localisation;
import lto.manager.web.resource.Localisation.LOC;

public class ElementIconButton extends ElementButton {
	final String iconClass;

	public ElementIconButton(final String iconClass) {
		super(FormElementType.BUTTON_ICON);
		this.iconClass = iconClass;
	}

	public static ElementIconButton of(final String iconClass) {
		return new ElementIconButton(iconClass);
	}

	public static ElementIconButton ofTypeAdd() {
		var btn = new ElementIconButton(CSS.ICON_PLUS_SQUARE);
		btn.withText(Localisation.get(LOC.FORM_ADD));
		return btn;
	}

	public static ElementIconButton ofTypeCancel() {
		var btn = new ElementIconButton(CSS.ICON_CROSS);
		btn.withText(Localisation.get(LOC.FORM_CANCEL));
		return btn;
	}

	@Override
	public void render(Div<?> div) {
		// @formatter:off
		div
			.button()
				.attrClass(CSS.BUTTON + CSS.BUTTON_IMAGE + CSS.BUTTON_IMAGE_W_TEXT + iconClass)
				.attrType(isSubmit ? EnumTypeButtonType.SUBMIT : EnumTypeButtonType.BUTTON)
				.of(s -> {
					for (final var op: getOperations()) {
						op.getValue().accept(s);
					}
					if (text != null) {
						s.text(text);
					}
				})
			.__(); // button
		// @formatter:on
	}
}
