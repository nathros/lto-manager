package lto.manager.web.handlers.http.ajax.labelgenerator;

import lto.manager.web.handlers.http.templates.models.BodyModel;

public record LTOLabelOptions(String barcode) {
	public static final String QPREFIX = "prefix";
	public static final String QBARCODE = "barcode";

	public static LTOLabelOptions of(BodyModel model) {
		if (model == null) return new LTOLabelOptions("");
		return new LTOLabelOptions("*" + model.getQuery(QBARCODE) + "L5*");
	}
}
