package lto.manager.web.check.element.button;

import org.xmlet.htmlapifaster.Div;

import lto.manager.common.Util;
import lto.manager.web.check.element.FormElement;

public class ElementButton extends FormElement {

	public ElementButton(FormElementType type) {
		super(type);
	}

	public static ElementButton of() {
		return new ElementButton(FormElementType.BUTTON);
	}

	@Override
	public void render(Div<?> div) {
		Util.throwException(new Exception("Not implemented"));
	}

}
