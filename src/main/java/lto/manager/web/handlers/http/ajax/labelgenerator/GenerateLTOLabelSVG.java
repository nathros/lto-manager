package lto.manager.web.handlers.http.ajax.labelgenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lto.manager.common.Util;
import lto.manager.common.database.tables.TableTape;
import lto.manager.web.handlers.http.pages.AssetHandler;
import lto.manager.web.resource.Asset;

public class GenerateLTOLabelSVG {
	public static List<String> generate(final LTOLabelOptions options) {
		List<String> results = new ArrayList<String>();

		String labelSVG = null;
		final String labelResource = Asset.IMG_LTO_LABEL + "label.svg";
		try {
			labelSVG = AssetHandler.getResourceAsString(labelResource);
		} catch (Exception e) {
			Util.logAndException(new Exception("Main LTO label resource " + labelResource + " not found"));
		}

		final String[] labelSVGLines = labelSVG.split("\n");

		final HashMap<Integer, String> barcodeSVG = new HashMap<Integer, String>();
		for (int i = 0; i < TableTape.BARCODE_ALL_CHARS.length(); i++) {
			final int charInt = TableTape.BARCODE_ALL_CHARS.charAt(i);
			String svg = null;
			try {
				svg = AssetHandler.getResourceAsString(Asset.IMG_LTO_LABEL + charInt + ".svg");
			} catch (Exception e) {
				Util.logAndException(new Exception(
						"Barcode segment resource letter [" + ((char) charInt) + "] " + charInt + ".svg not found"));
			}
			svg = svg.replaceFirst(" viewBox=\"(.*?)\"", ""); // Remove viewBox as will be child of other SVG
			barcodeSVG.put(charInt, svg);
		}

		StringBuilder sb = new StringBuilder();
		int i = 0;
		while (i < labelSVGLines.length) {
			final String line = labelSVGLines[i];
			SVGID elementID = SVGID.of(line);
			if (elementID != null) {
				switch (elementID.name) {
				case "br": { // This is a barcode section SVG element
					sb.append(line);
					final int barcodeCharacter = options.barcode().charAt(elementID.index);
					final String barcode = barcodeSVG.get(barcodeCharacter);
					if (barcode == null) {
						Util.logAndException(new Exception("Barcode contains invalid character ["
								+ ((char) barcodeCharacter) + "], valid are: " + TableTape.BARCODE_VALID_CHARS));
					}
					sb.append(barcode);
					i += 2;
					sb.append(labelSVGLines[i]);
					i++;
					continue;
				}

				case "t": { // This is label text SVG element
					sb.append(line);
					i++;
					String tLine = labelSVGLines[i];
					sb.append(tLine);
					i++;
					tLine = labelSVGLines[i];
					String labelCharacter = String.valueOf(options.barcode().charAt(elementID.index));
					if (elementID.index == 7) {
						labelCharacter = labelCharacter // Last section has 2 characters for tape type eg: L7
								.concat(String.valueOf(options.barcode().charAt(elementID.index + 1)));
					}
					tLine = tLine.replaceFirst("#", Matcher.quoteReplacement(labelCharacter)); // Matcher needed for $
					sb.append(tLine);
					i++;
					tLine = labelSVGLines[i];
					sb.append(tLine);
					i++;
					continue;
				}

				case "body": {// Main LTO label
					sb.append(line);
					i++;
					String tLine = labelSVGLines[i];
					tLine = replaceAttributeValue(tLine, "rx", options.borderRadiusLabel());
					tLine = replaceAttributeValue(tLine, "ry", options.borderRadiusLabel());
					tLine = replaceAttributeValue(tLine, "stroke-width", options.borderStrokeLabel());
					sb.append(tLine);
					i++;
					continue;
				}
				case "root": {
					String tLine = labelSVGLines[i];
					String widthStr = getAttributeValue(tLine, "width");
					widthStr = widthStr.substring(0, widthStr.length() - 2);
					float width = Float.parseFloat(widthStr);
					String heightStr = getAttributeValue(tLine, "height");
					heightStr = heightStr.substring(0, heightStr.length() - 2);
					float height = Float.parseFloat(heightStr);

					final float scaleMultiplier = Float.parseFloat(options.previewScale());
					width = width * scaleMultiplier;
					height = height * scaleMultiplier;
					widthStr = String.valueOf(width);
					heightStr = String.valueOf(height);
					tLine = replaceAttributeValue(tLine, "width",  widthStr + "mm");
					tLine = replaceAttributeValue(tLine, "height", heightStr + "mm");
					sb.append(tLine);
					i++;
					continue;
				}

				default:
					throw new IllegalArgumentException(
							Asset.IMG_LTO_LABEL + "label.svg unexpected id: " + elementID.name);
				}
			} else {
				sb.append(line);
			}
			i++;
		}

		final String result = sb.toString();
		results.add(result);
		return results;
	}

	private static String replaceAttributeValue(final String input, final String attributeKey,
			final String newAttributeValue) {
		final String search = " " + attributeKey + "=\"(.*?)\"";
		final String replace = " " + attributeKey + "=\"" + newAttributeValue + "\"";
		return input.replaceFirst(search, replace);
	}

	private static String getAttributeValue(final String input, final String attributeKey) {
		final String regex = attributeKey + "=\"([^\"]*)\"";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(input);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return null;
	}

	// id inside label.svg in this format key-value eg: br-3
	// This record will find id if exists and extract key value pair
	private static record SVGID(String name, Integer index) {
		public static SVGID of(String svgStrLine) { // Return null if not found
			int startIndex = svgStrLine.indexOf("id=\"");
			if (startIndex > 0) {
				startIndex += 4;
				int endIndex = svgStrLine.indexOf('"', startIndex);
				if (endIndex > 0) {
					final String keyIndex = svgStrLine.substring(startIndex, endIndex);
					final String[] split = keyIndex.split("-");
					if (split.length == 2) {
						return new SVGID(split[0], Integer.parseInt(split[1]));
					}
				}
			}
			return null;
		}
	}
}
