package lto.manager.web.handlers.templates.models;

import com.sun.net.httpserver.HttpExchange;

public class BodyModel {
	private final HttpExchange he;
	private final Object model;

	private BodyModel(HttpExchange he, Object model) {
		this.he = he;
		this.model = model;
	}

	public static BodyModel of(HttpExchange he, Object model) {
		return new BodyModel(he, model);
	}

	public HttpExchange getHttpExchange() { return he; }
	public Object getModel() { return model; }
}