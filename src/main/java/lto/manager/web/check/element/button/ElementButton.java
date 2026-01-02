package lto.manager.web.check.element.button;

import org.xmlet.htmlapifaster.Div;
import lto.manager.common.Util;
import lto.manager.web.check.element.FormElement;

public class ElementButton extends FormElement {
	protected boolean isSubmit;

	public ElementButton(FormElementType type) {
		super(type);
	}

	public static ElementButton of() {
		return new ElementButton(FormElementType.BUTTON);
	}

	public ElementButton asSubmit() {
		isSubmit = true;
		return this;
	}

	@Override
	public void validate() {
		validatorStatus = null;
	}

	@Override
	public void render(Div<?> div) {
		Util.throwException(new Exception("Not implemented"));
	}

}
