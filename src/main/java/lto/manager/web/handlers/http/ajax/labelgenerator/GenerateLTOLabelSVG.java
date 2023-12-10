package lto.manager.web.handlers.http.ajax.labelgenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lto.manager.common.database.tables.TableTape;
import lto.manager.common.log.Log;
import lto.manager.web.handlers.http.pages.AssetHandler;
import lto.manager.web.resource.Asset;

public class GenerateLTOLabelSVG {
	public static List<String> generate(final LTOLabelOptions options) {
		try {
			List<String> results = new ArrayList<String>();

			final String labelSVG = AssetHandler.getResourceAsString(Asset.IMG_LTO_LABEL + "label.svg");
			final String[] labelSVGLines = labelSVG.split("\n");

			final HashMap<Integer, String> barcodeSVG = new HashMap<Integer, String>();
			for (int i = 0; i < TableTape.BARCODE_ALL_CHARS.length(); i++) {
				final int charInt = TableTape.BARCODE_ALL_CHARS.charAt(i);
				String svg = AssetHandler.getResourceAsString(Asset.IMG_LTO_LABEL + charInt + ".svg");
				// Remove viewBox as will be child of other SVG
				svg = svg.replaceFirst(" viewBox=\"0 0 6.588 11.7\"", "");
				barcodeSVG.put(charInt, svg);
			}

			StringBuilder sb = new StringBuilder();
			int i = 0;
			while (i < labelSVGLines.length) {
				final String line = labelSVGLines[i];
				sb.append(line);
				SVGID elementID = SVGID.of(line);
				if (elementID != null) {
					switch (elementID.name) {
					case "br": { // This is a barcode SVG element
						final Integer barcodeCharacter = (int) options.barcode().charAt(elementID.index);
						final String barcode = barcodeSVG.get(barcodeCharacter);
						if (barcode == null) {
							throw new IllegalArgumentException(barcodeCharacter + " not found inside barcodeSVG");
						}
						sb.append(barcode);
						i += 2;
						sb.append(labelSVGLines[i]);
						i++;
						continue;
					}

					case "t": { // This is label SVG element
						i++;
						String tLine = labelSVGLines[i];
						sb.append(tLine);
						i++;
						tLine = labelSVGLines[i];
						String labelCharacter = String.valueOf(options.barcode().charAt(elementID.index));
						if (elementID.index == 7) {
							labelCharacter = labelCharacter.concat(String.valueOf(options.barcode().charAt(elementID.index + 1)));
						}
						tLine = tLine.replaceFirst("#", labelCharacter);
						sb.append(tLine);
						i++;
						tLine = labelSVGLines[i];
						sb.append(tLine);
						i++;
						continue;
					}

					case "body": // do nothing with body
						break;
					default:
						throw new IllegalArgumentException(
								Asset.IMG_LTO_LABEL + "label.svg unexpected id: " + elementID.name);
					}
				}
				i++;
			}

			final String result = sb.toString();
			results.add(result);
			return results;
		} catch (Exception e) {
			Log.l.severe("Failed to generate LTO label SVG: " + e.getMessage());
			return null;
		}

	}

	private String replaceAttributeValue(String input, final String attributeKey, final String newAttributeValue) {
		final String search = " " + attributeKey  + "=\".\"";
		final String replace = " " + attributeKey  + "=\"" + newAttributeValue + "\"";
		return input.replaceFirst(search, replace);
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
