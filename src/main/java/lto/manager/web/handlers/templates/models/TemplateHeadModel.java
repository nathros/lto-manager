package lto.manager.web.handlers.templates.models;

import java.util.ArrayList;
import java.util.List;


public class TemplateHeadModel {
	final String title;
	final List<String> extraStylesList = new ArrayList<String>();
	final List<String> extraScriptsList = new ArrayList<String>();

	private TemplateHeadModel(final String title) {
		this.title = title;
	}

	public static TemplateHeadModel of(final String title) {
		return new TemplateHeadModel(title);
	}

	public String getTitle() {
		return title;
	}

	public void AddCSS(final String css) {
		extraStylesList.add(css);
	}

	public void AddScript(final String js) {
		extraScriptsList.add(js);
	}

	public List<String> getExtraStylesList() {
		return extraStylesList;
	}

	public List<String> getExtraScriptsList() {
		return extraScriptsList;
	}
}
