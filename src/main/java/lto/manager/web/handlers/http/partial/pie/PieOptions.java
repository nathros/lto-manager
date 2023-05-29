package lto.manager.web.handlers.http.partial.pie;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

public record PieOptions(
		int percent,
		String title,
		String ringColour,
		String toolTipWidth,
		String toolTipTop,
		String toolTipLeft,
		String toolTipText,
		List<Entry<String,String>> details){

	public static Entry<String,String> getDetailsPair(String first, String second) {
		return new SimpleImmutableEntry<String,String>(first, second);
	}

	public PieOptions(int percent,
			String title,
			String ringColour,
			String toolTipWidth,
			String toolTipTop,
			String toolTipLeft,
			String toolTipText) {
		this(percent, title, ringColour, toolTipWidth, toolTipTop, toolTipLeft, toolTipText, new ArrayList<Entry<String,String>>());
	}

}
