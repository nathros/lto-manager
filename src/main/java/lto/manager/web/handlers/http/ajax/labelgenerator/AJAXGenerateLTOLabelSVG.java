package lto.manager.web.handlers.http.ajax.labelgenerator;

import java.util.List;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.common.database.tables.records.RecordRole.Permission;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.resource.Asset;

public class AJAXGenerateLTOLabelSVG extends BaseHTTPHandler {
	public static final String PATH = Asset.PATH_AJAX_BASE + "generate/lto/label/svg/";

	@Override
	public void requestHandle(HttpExchange he, BodyModel bm) throws Exception {
		List<String> labelsSVGs = GenerateLTOLabelSVG.generate(LTOLabelOptions.of(bm));
		StringBuilder sb = new StringBuilder();

		final String firstSVG = labelsSVGs.get(0);
		int index = firstSVG.indexOf('>', 64);
		String wrapperSVG = firstSVG.substring(0, index + 1);

		String heightStr = GenerateLTOLabelSVG.getAttributeValue(wrapperSVG, "height");
		heightStr = heightStr.substring(0, heightStr.length() - 2); // Remove mm unit

		final float heightFloat = Float.parseFloat(heightStr);
		final float newHeightFloat = heightFloat * labelsSVGs.size();
		final String newHeightStr = String.valueOf(newHeightFloat);

		wrapperSVG = GenerateLTOLabelSVG.replaceAttributeValue(wrapperSVG, "height", newHeightStr + "mm");
		String viewBoxStr = GenerateLTOLabelSVG.getAttributeValue(wrapperSVG, "viewBox");
		viewBoxStr = viewBoxStr.replaceAll(heightStr, newHeightStr);
		wrapperSVG = GenerateLTOLabelSVG.replaceAttributeValue(wrapperSVG, "viewBox", viewBoxStr);
		wrapperSVG = GenerateLTOLabelSVG.replaceAttributeValue(wrapperSVG, "id", "wrapper");

		sb.append(wrapperSVG);
		int heightIndex = 0;
		for (String labelSVG : labelsSVGs) {
			final String currentHeight = String.valueOf(heightFloat * heightIndex);
			sb.append("<g transform=\"translate(0 " + currentHeight + ")\">");
			index = labelSVG.indexOf(">");
			labelSVG = labelSVG.substring(index + 1, labelSVG.length()); // Remove XML doctype
			labelSVG = GenerateLTOLabelSVG.replaceAttributeValue(labelSVG, "viewBox", ""); // Remove nested view box
			sb.append(labelSVG);
			sb.append("</g>");
			heightIndex++;
		}
		sb.append("</svg>"); // wrapperSVG

		requestHandleCompleteAPIText(he, sb.toString(), BaseHTTPHandler.CONTENT_TYPE_SVG);
	}

	@Override
	public Permission getHandlePermission() {
		return null;
	}
}
