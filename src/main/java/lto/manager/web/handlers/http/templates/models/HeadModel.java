package lto.manager.web.handlers.http.templates.models;

import java.util.ArrayList;
import java.util.List;


public class HeadModel {
	final String title;
	final List<String> extraStylesList = new ArrayList<String>();
	final List<String> extraScriptsList = new ArrayList<String>();
	final List<String> extraScriptsDeferList = new ArrayList<String>();

	private HeadModel(final String title) {
		this.title = title;
	}

	public static HeadModel of(final String title) {
		return new HeadModel(title);
	}

	public String getTitle() {
		return title;
	}

	public HeadModel addCSS(final String css) {
		extraStylesList.add(css);
		return this;
	}

	public HeadModel addScript(final String js) {
		extraScriptsList.add(js);
		return this;
	}

	// Defer script will only be run when DOM content is loaded
	public HeadModel addScriptDefer(final String js) {
		extraScriptsDeferList.add(js);
		return this;
	}

	public List<String> getExtraStylesList() {
		return extraStylesList;
	}

	public List<String> getExtraScriptsList() {
		return extraScriptsList;
	}

	public List<String> getExtraScriptsDeferList() {
		return extraScriptsDeferList;
	}
}
