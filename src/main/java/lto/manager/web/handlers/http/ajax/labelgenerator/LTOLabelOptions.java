package lto.manager.web.handlers.http.ajax.labelgenerator;

import lto.manager.common.Util;
import lto.manager.common.database.tables.TableTape;
import lto.manager.web.handlers.http.templates.models.BodyModel;

public record LTOLabelOptions(String barcode, String prefix, String postfix, String borderRadiusLabel,
		String borderStrokeLabel, String previewScale, int previewCount) {

	public static final String QPREFIX = "prefix";
	public static final String QPOSTFIX = "postfix";
	public static final String QBARCODE = "barcode";

	public static final String QUERY_BORDER_RADIUS_LABEL = "border-rad-lab";
	public static final float BORDER_RADIUS_LABEL_MAX = 8;
	public static final float BORDER_RADIUS_LABEL_MIN = 0;
	public static final float BORDER_RADIUS_LABEL_DEFAULT = 1;

	public static final String QUERY_BORDER_STROKE_LABEL = "border-rad-storke";
	public static final float BORDER_STROKE_LABEL_MAX = 2;
	public static final float BORDER_STROKE_LABEL_MIN = 0;
	public static final float BORDER_STROKE_LABEL_DEFAULT = (float) 0.035;

	public static final String QUERY_PREVIEW_SCALE = "preview-scale";
	public static final float PREVIEW_SCALE_MAX = 3;
	public static final float PREVIEW_SCALE_MIN = 1;
	public static final float PREVIEW_SCALE_DEFAULT = (float) 1.5;
	public static final String PREVIEW_SCALE_EMPTY = "1";

	public static final String QUERY_PREVIEW_COUNT = "preview-count";
	public static final int PREVIEW_COUNT_MAX = 100;
	public static final int PREVIEW_COUNT_MIN = -1;
	public static final int PREVIEW_COUNT_DEFAULT = 4;

	public static LTOLabelOptions of(final BodyModel model) {
		final String prefixStr = model.getQueryNoNull(QPREFIX);
		final String postfixStr = model.getQueryNoNull(QPOSTFIX);
		final int fixLenCombined = prefixStr.length() + postfixStr.length();
		if (fixLenCombined > TableTape.MAX_LEN_BARCODE_FORM) {
			Util.logAndException(new Exception(
					"Prefix and postfix combined length should not be bigger than " + TableTape.MAX_LEN_BARCODE_FORM));
		}
		for (int i = 0; i < prefixStr.length(); i++) {
			final String c = String.valueOf(prefixStr.charAt(i));
			if (!TableTape.BARCODE_VALID_CHARS.contains(c)) {
				Util.logAndException(new Exception(
						"Prefix contains invalid character [" + c + "], valid are: " + TableTape.BARCODE_VALID_CHARS));
			}
		}
		for (int i = 0; i < postfixStr.length(); i++) {
			final String c = String.valueOf(postfixStr.charAt(i));
			if (!TableTape.BARCODE_VALID_CHARS.contains(c)) {
				Util.logAndException(new Exception(
						"Postfix contains invalid character [" + c + "], valid are: " + TableTape.BARCODE_VALID_CHARS));
			}
		}

		final String borderRadiusLabelStr = model.getQueryNoNull(QUERY_BORDER_RADIUS_LABEL);
		checkFloat(borderRadiusLabelStr, BORDER_RADIUS_LABEL_MIN, BORDER_RADIUS_LABEL_MAX, "Label border radius");

		final String borderStrokeLabelStr = model.getQueryNoNull(QUERY_BORDER_STROKE_LABEL);
		checkFloat(borderStrokeLabelStr, BORDER_STROKE_LABEL_MIN, BORDER_STROKE_LABEL_MAX, "Label border stroke");

		// These are for preview only
		String previewScaleStr = model.getQueryNoNull(QUERY_PREVIEW_SCALE); // Can be missing
		if (previewScaleStr.equals("")) {
			previewScaleStr = PREVIEW_SCALE_EMPTY;
		} else {
			checkFloat(previewScaleStr, PREVIEW_SCALE_MIN, PREVIEW_SCALE_MAX, "Preview scale");
		}
		String previewCountStr = model.getQueryNoNull(QUERY_PREVIEW_COUNT); // // Can be missing
		int previewInt;
		if (previewCountStr.equals("")) {
			previewInt = PREVIEW_COUNT_MIN;
		} else {
			previewInt = checkInt(previewCountStr, PREVIEW_COUNT_MIN, PREVIEW_COUNT_MAX, "Preview count");
		}

		return new LTOLabelOptions("*" + model.getQuery(QBARCODE) + "L5*", prefixStr, postfixStr, borderRadiusLabelStr,
				borderStrokeLabelStr, previewScaleStr, previewInt);
	}

	private static void checkFloat(final String queryStr, final float minValue, final float maxValue,
			final String verboseName) {
		try {
			final float asFloat = Float.parseFloat(queryStr);
			if (asFloat < minValue || asFloat > maxValue) {
				throw new IllegalArgumentException(verboseName + " not between " + minValue + " and " + maxValue);
			}
		} catch (NumberFormatException e) {
			Util.logAndException(new Exception(verboseName + " not a valid number"));
		} catch (IllegalArgumentException e) {
			Util.logAndException(e);
		}
	}

	private static int checkInt(final String queryStr, final int minValue, final int maxValue,
			final String verboseName) {
		try {
			final int asInt = Integer.parseInt(queryStr);
			if (asInt < minValue || asInt > maxValue) {
				throw new IllegalArgumentException(verboseName + " not between " + minValue + " and " + maxValue);
			}
			return asInt;
		} catch (NumberFormatException e) {
			Util.logAndException(new Exception(verboseName + " not a valid integer (whole number)"));
		} catch (IllegalArgumentException e) {
			Util.logAndException(e);
		}
		return -1; // Should not reach here
	}
}
