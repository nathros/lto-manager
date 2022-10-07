package lto.manager.web.handlers.templates.models;

import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.web.handlers.BaseHandler;

public class BodyModel {
	private final HttpExchange he;
	private final Object model;
	private final Map<String, String> queries;

	public static final String QUERY_ON = "on";
	public static final String QUERY_OFF = "off";

	private BodyModel(HttpExchange he, Object model) {
		this.he = he;
		this.model = model;
		queries = new HashMap<String, String>();
		String query = he.getRequestURI().getRawQuery();
		BaseHandler.parseQuery(query, queries);
	}

	public static BodyModel of(HttpExchange he, Object model) {
		return new BodyModel(he, model);
	}

	public HttpExchange getHttpExchange() { return he; }
	public Object getModel() { return model; }
	public String getQuery(String key) { return queries.get(key); }

	public String getQueryNoNull(String key) {
		String q = queries.get(key);
		if (q == null) return "";
		else return q;
	}

	public boolean hasQuery() {
		String q = he.getRequestURI().getRawQuery();
		return q != null;
	}
}