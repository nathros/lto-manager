package lto.manager.web.handlers.http.partial;

import java.util.ArrayList;
import java.util.List;

import org.xmlet.htmlapifaster.Select;

import lto.manager.common.database.Database;
import lto.manager.common.database.tables.records.RecordTapeType;
import lto.manager.web.resource.HTML;

public class LTOTapeTypeSelect {
	public static final String DATA_AJAX = "data-ajax";

	public static Void content(Select<?> view, final String ID, final String name, final int selectedIndex) {
		final List<RecordTapeType> tapeTypes = new ArrayList<RecordTapeType>();
		try {
			tapeTypes.addAll(Database.getAllTapeTypes());
		} catch (Exception e) {
		}
		view
			.attrId(ID).attrName(name).of(select -> { //make generic
				select.attrOnchange("onSelectType()")
					.option().of(o -> HTML.option(o, selectedIndex == -1, true)).text("Select").__();
				int index = 0;
				for (RecordTapeType item: tapeTypes) {
					final int indexCopy = index;
					select.option()
						.of(sel -> {
							if (indexCopy == selectedIndex) {
								sel.of(o -> HTML.option(o, true));
							}
						})
						.addAttr("data-des", item.getDesignation())
						.addAttr("data-worm", item.getDesignationWORM())
						.attrValue(String.valueOf(index))
						.text(item.getType())
					.__();
					index++;
				}
		});
		return null;
	}
}
