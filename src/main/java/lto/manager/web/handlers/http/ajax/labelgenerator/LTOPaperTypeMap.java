package lto.manager.web.handlers.http.ajax.labelgenerator;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import lto.manager.common.IniFileProcessor;
import lto.manager.common.Util;
import lto.manager.web.resource.Asset;

public class LTOPaperTypeMap {
	private static LinkedHashMap<String, LTOPageType> pageMapContainer = get();

	public record LTOPageType(float pageWidthPT, float pageHeightPT, int columnCount, int labelsPerPage, float xStart,
			float xOffset, float yStart, float yOffset) {
	}

	private static LinkedHashMap<String, LTOPageType> get() {
		final String paperFile = Asset.IMG_LTO_LABEL + "paper.ini";
		LinkedHashMap<String, LTOPageType> pageMap = new LinkedHashMap<String, LTOPageType>();
		LinkedHashMap<String, HashMap<String, String>> iniMap = new LinkedHashMap<String, HashMap<String, String>>();
		try {
			iniMap = IniFileProcessor.asMultiMap(paperFile);
			iniMap.forEach((k, v) -> {
				final float pWidth = Float.parseFloat(v.get("page_width_pt"));
				final float pHeight = Float.parseFloat(v.get("page_height_pt"));
				final int cols = Integer.parseInt(v.get("columns"));
				final int mCount = Integer.parseInt(v.get("max_label_per_page"));
				final float xS = Float.parseFloat(v.get("x_start"));
				final float xO = Float.parseFloat(v.get("x_offset"));
				final float yS = Float.parseFloat(v.get("y_start"));
				final float yO = Float.parseFloat(v.get("y_offset"));
				pageMap.put(k, new LTOPageType(pWidth, pHeight, cols, mCount, xS, xO, yS, yO));
			});

		} catch (IOException e) {
			Util.logAndException(new Exception("Unable to find/process LTO paper file at: " + paperFile));
		}
		return pageMap;
	}

	public static LTOPageType getPaperType(final String paperTypeName) {
		return pageMapContainer.get(paperTypeName);
	}

	public static LTOPageType getDefaultPaperType() {
		return pageMapContainer.get("A4");
	}

	public static Set<String> getPaperTypeNames() {
		return pageMapContainer.keySet();
	}

}
