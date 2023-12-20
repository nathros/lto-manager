package lto.manager.web.handlers.http.ajax.labelgenerator;

import lto.manager.common.Util;
import lto.manager.common.database.tables.TableTape;
import lto.manager.web.handlers.http.ajax.labelgenerator.LTOLabelEnum.LTOLabelColourSettings;
import lto.manager.web.handlers.http.ajax.labelgenerator.LTOLabelEnum.LTOLabelTypeSettings;
import lto.manager.web.handlers.http.templates.models.BodyModel;

public record LTOLabelOptions(String mediaID, String prefix, String postfix, String borderRadiusLabel,
		String borderStrokeLabel, String borderRadiusInner, String borderStrokeInner, String themeName,
		LTOLabelColourSettings colourSetting, int startIndex, int quantity, String previewScale, int previewCount) {

	public static final String QUERY_PREFIX = "prefix";
	public static final String QUERY_POSTFIX = "postfix";
	public static final String QUERY_MEDIA = "media";

	public static final String QUERY_BORDER_RADIUS_LABEL = "border-rad-lab";
	public static final String QUERY_BORDER_RADIUS_INNER = "border-rad-in";
	public static final float BORDER_RADIUS_LABEL_MAX = 8;
	public static final float BORDER_RADIUS_LABEL_MIN = 0;
	public static final float BORDER_RADIUS_LABEL_DEFAULT = 1;
	public static final float BORDER_RADIUS_INNER_DEFAULT = 0;

	public static final String QUERY_BORDER_STROKE_LABEL = "border-stroke-lab";
	public static final String QUERY_BORDER_STROKE_INNER = "border-stroke-in";
	public static final float BORDER_STROKE_LABEL_MAX = 2;
	public static final float BORDER_STROKE_LABEL_MIN = 0;
	public static final float BORDER_STROKE_LABEL_DEFAULT = (float) 0.1;

	public static final String QUERY_THEME = "theme";
	public static final String QUERY_COLOURS = "colours";

	public static final String QUERY_START_INDEX = "start-index";
	public static final int START_INDEX_MIN = 0;
	public static final int START_INDEX_MAX = Integer.MAX_VALUE;
	public static final int START_INDEX_DEFAULT = 1;

	public static final String QUERY_QUANTITY = "quantity";
	public static final int QUANTITY_MIN = 1;
	public static final int QUANTITY_MAX = Integer.MAX_VALUE;
	public static final int QUANTITY_DEFAULT = 32;

	public static final String QUERY_PREVIEW_SCALE = "preview-scale";
	public static final float PREVIEW_SCALE_MAX = 3;
	public static final float PREVIEW_SCALE_MIN = 1;
	public static final float PREVIEW_SCALE_DEFAULT = (float) 1.75;
	public static final String PREVIEW_SCALE_EMPTY = "1";

	public static final String QUERY_PREVIEW_COUNT = "preview-count";
	public static final int PREVIEW_COUNT_MAX = 100;
	public static final int PREVIEW_COUNT_MIN = -1;
	public static final int PREVIEW_COUNT_MIN_FORM = 0;
	public static final int PREVIEW_COUNT_DEFAULT = 10;

	public static LTOLabelOptions of(final BodyModel model) {
		final String prefixStr = model.getQueryNoNull(QUERY_PREFIX);
		final String postfixStr = model.getQueryNoNull(QUERY_POSTFIX);
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

		final String startIndexStr = model.getQueryNoNull(QUERY_START_INDEX);
		int startIndex = checkInt(startIndexStr, START_INDEX_MIN, START_INDEX_MAX, "Start index");

		final String quantityStr = model.getQueryNoNull(QUERY_QUANTITY);
		int quantity = checkInt(quantityStr, QUANTITY_MIN, QUANTITY_MAX, "Quantity");

		final String borderRadiusLabelStr = model.getQueryNoNull(QUERY_BORDER_RADIUS_LABEL);
		checkFloat(borderRadiusLabelStr, BORDER_RADIUS_LABEL_MIN, BORDER_RADIUS_LABEL_MAX, "Label border radius");

		final String borderStrokeLabelStr = model.getQueryNoNull(QUERY_BORDER_STROKE_LABEL);
		checkFloat(borderStrokeLabelStr, BORDER_STROKE_LABEL_MIN, BORDER_STROKE_LABEL_MAX, "Label border stroke");

		final String borderRadiusInnerStr = model.getQueryNoNull(QUERY_BORDER_RADIUS_INNER);
		checkFloat(borderRadiusInnerStr, BORDER_RADIUS_LABEL_MIN, BORDER_RADIUS_LABEL_MAX, "Inner border radius");

		final String borderStrokeInnerStr = model.getQueryNoNull(QUERY_BORDER_STROKE_INNER);
		checkFloat(borderStrokeInnerStr, BORDER_STROKE_LABEL_MIN, BORDER_STROKE_LABEL_MAX, "Inner border stroke");

		final String colourSettingsStr = model.getQueryNoNull(QUERY_COLOURS);
		LTOLabelColourSettings colourSetting = null;
		try {
			colourSetting = LTOLabelColourSettings.valueOf(colourSettingsStr);
		} catch (Exception e) {
			Util.logAndException(new Exception("Invalid colour setting: " + colourSetting));
		}

		final String themeNameStr = model.getQueryNoNull(QUERY_THEME);

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

		final String mediaStr = model.getQueryNoNull(QUERY_MEDIA);
		String media = null;
		try {
			LTOLabelTypeSettings type = LTOLabelTypeSettings.valueOf(mediaStr);
			media = LTOLabelEnum.getTypeDesignation(type);
		} catch (Exception e) {
			Util.logAndException(new Exception("Invalid tape type label: " + colourSetting));
		}

		return new LTOLabelOptions(media, prefixStr, postfixStr, borderRadiusLabelStr, borderStrokeLabelStr,
				borderRadiusInnerStr, borderStrokeInnerStr, themeNameStr, colourSetting, startIndex, quantity,
				previewScaleStr, previewInt);
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

	public String generateBarcode(int index) {
		final int remainingLength = TableTape.MAX_LEN_BARCODE_FORM - prefix.length() - postfix.length();
		String indexStr = String.valueOf(index);
		while (indexStr.length() < remainingLength) {
			indexStr = "0" + indexStr;
		}
		if (indexStr.length() > remainingLength) {
			indexStr = indexStr.substring(indexStr.length() - remainingLength);
		}
		final String result = "*" + prefix + indexStr + postfix + mediaID + "*";
		return result;
	}
}
