package lto.manager.web.check.element.input;

import org.xmlet.htmlapifaster.Div;

import lto.manager.web.check.FormValidator.ValidatorStatus;

public class ElementInputPassword extends ElementInput {
	public ElementInputPassword() {
		super(FormElementType.INPUT_PASSWORD);
	}

	@Override
	public void validate() {
		validatorStatus = ValidatorStatus.emptyOK();
	}

	@Override
	public void render(Div<?> div) {
		// TODO Auto-generated method stub

	}
}
