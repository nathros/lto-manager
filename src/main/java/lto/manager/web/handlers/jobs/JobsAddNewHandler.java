package lto.manager.web.handlers.jobs;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import org.xmlet.htmlapifaster.EnumTypeButtonType;
import org.xmlet.htmlapifaster.EnumTypeInputType;

import com.sun.net.httpserver.HttpExchange;

import htmlflow.DynamicHtml;
import lto.manager.web.handlers.BaseHandler;
import lto.manager.web.handlers.templates.TemplatePage;
import lto.manager.web.handlers.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.templates.models.BodyModel;
import lto.manager.web.handlers.templates.models.HeadModel;
import lto.manager.web.resource.CSS;

public class JobsAddNewHandler extends BaseHandler {
	public static final String PATH = "/jobs/add";
	public static DynamicHtml<BodyModel> view = DynamicHtml.view(JobsAddNewHandler::body);

	public static final String NAME = "name";
	public static final String COMMENT = "comment";

	static void body(DynamicHtml<BodyModel> view, BodyModel model) {
		final String name = model.getQueryNoNull(NAME);
		final String comment = model.getQueryNoNull(COMMENT);

		// add files
		view
			.div()
				.form()
					.b().attrStyle("width:150px;display:inline-block").text("Job Name: ").__()
					.input().attrType(EnumTypeInputType.TEXT).attrName(NAME).dynamic(input -> input.attrValue(name)).__().br().__()

					.b().attrStyle("width:150px;display:inline-block").text("Comment: ").__()
					.textarea().attrName(COMMENT).text(comment).__()
					.button().attrClass(CSS.BUTTON).attrType(EnumTypeButtonType.SUBMIT).text("Submit").__()
				.__() // form
			.__(); // div
	}

	@Override
	public void requestHandle(HttpExchange he) throws IOException {
		try {
			HeadModel thm = HeadModel.of("Jobs");
			TemplatePageModel tepm = TemplatePageModel.of(view, thm, SelectedPage.Jobs, BodyModel.of(he, null));
			String response = TemplatePage.view.render(tepm);

			he.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length());
			OutputStream os = he.getResponseBody();
			os.write(response.getBytes());
			os.close();
		} catch (Exception e) {
			view = DynamicHtml.view(JobsAddNewHandler::body);
			throw e;
		}
	}

}
