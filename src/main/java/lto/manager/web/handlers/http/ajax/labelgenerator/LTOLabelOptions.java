package lto.manager.web.handlers.http.ajax.labelgenerator;

import lto.manager.web.handlers.http.templates.models.BodyModel;

public record LTOLabelOptions(String barcode, String borderRadiusLabel) {
	public static final String QPREFIX = "prefix";
	public static final String QBARCODE = "barcode";
	public static final String QBORDER_RADIUS_LABEL = "border-rad-lab";

	public static LTOLabelOptions of(BodyModel model) {
		final String borderRadiusLabelStr = model.getQueryNoNull(QBORDER_RADIUS_LABEL);
		//final float borderRadiusLabelFloat = Float.parseFloat(borderRadiusLabelStr);
		return new LTOLabelOptions("*" + model.getQuery(QBARCODE) + "L5*", borderRadiusLabelStr);
	}
}
