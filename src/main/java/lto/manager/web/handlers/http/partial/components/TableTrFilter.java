package lto.manager.web.handlers.http.partial.components;

import org.xmlet.htmlapifaster.Div;
import org.xmlet.htmlapifaster.EnumTypeInputType;
import org.xmlet.htmlapifaster.Th;

import lto.manager.web.resource.CSS;
import lto.manager.web.resource.JS;

public class TableTrFilter {

	public static Void content(Th<?> view, final String text) {
		view
			.div()
				.text(text)
				.span()
					.attrClass(CSS.TABLE_SORT)
					.attrOnclick(JS.tableSort())
				.__()
			.__()
			.input()
				.attrSize((long) 1)
				.attrType(EnumTypeInputType.SEARCH)
				.attrOninput(JS.tableFilter())
			.__();
		return null;
	}

	public static Void header(Div<?> view, final String tableID) {
		view
			.div()
				.attrClass(CSS.HEADER_ITEM + CSS.ICON_FUNNEL)
				.ul()
					.attrClass(CSS.MENU_LIST)
					.li()
						.attrClass(CSS.HEADER_LABEL_TOP)
						.text("Filter")
					.__()
					.li()
						.a()
							.attrStyle("user-select:none")
							.attrOnclick(JS.tableFilterShow(tableID))
							.text("Show")
						.__()
					.__()
				.__()
			.__();
		return null;
	}

	// Use this if <th> is detached from table, or to filter other table
	public static Void content(Th<?> view, final String text, final String tableID) {
		view
			.div()
				.text(text)
				.span()
					.attrClass(CSS.TABLE_SORT)
					.attrOnclick(JS.tableSort(tableID))
				.__()
			.__()
			.input()
				.attrSize((long) 1)
				.attrType(EnumTypeInputType.SEARCH)
				.attrOninput(JS.tableFilter(tableID))
			.__();
		return null;
	}

}
