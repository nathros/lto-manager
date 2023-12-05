package lto.manager.web.handlers.http.templates.models;

import java.util.ArrayList;
import java.util.List;


public class HeadModel {
	final String title;
	final List<String> extraStylesList = new ArrayList<String>();
	final List<String> extraScriptsList = new ArrayList<String>();

	private HeadModel(final String title) {
		this.title = title;
	}

	public static HeadModel of(final String title) {
		return new HeadModel(title);
	}

	public String getTitle() {
		return title;
	}

	public HeadModel AddCSS(final String css) {
		extraStylesList.add(css);
		return this;
	}

	public HeadModel AddScript(final String js) {
		extraScriptsList.add(js);
		return this;
	}

	public List<String> getExtraStylesList() {
		return extraStylesList;
	}

	public List<String> getExtraScriptsList() {
		return extraScriptsList;
	}
}
