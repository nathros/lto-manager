package lto.manager.web.handlers.http.partial.pie;

import java.util.Map.Entry;

import org.xmlet.htmlapifaster.Div;

import lto.manager.web.resource.CSS;

public class PieItem {
	public static Void content(Div<?> view, PieOptions settings) {
		view
			.div().attrClass(CSS.PIE_ITEM)
				.div()
					.div()
						.attrClass(CSS.PIE_CIRCLE)
						.attrStyle("--p:" + settings.percent() +  ";--c:" + settings.ringColour() + ";")
						.text(settings.percent() + "%")
					.__()
					.i().attrClass(CSS.PIE_INFO)
						.em()
							.attrClass(CSS.PIE_TOOLTIP)
							.attrStyle("top:" + settings.toolTipTop() +
									";left:" + settings.toolTipLeft() +
									";width:" + settings.toolTipWidth())
							.text(settings.toolTipText())
						.__()
					.__()
				.__()
				.div()
					.i().text(settings.title()).__()
					.table()
						.of(t -> {
							for (Entry<String,String> entry : settings.details()) {
								t.tr()
									.td().b().text(entry.getKey()).__().__()
									.td().text(entry.getValue()).__()
								.__();
							}
						})
					.__()
				.__()
			.__();
		return null;
	}
}
