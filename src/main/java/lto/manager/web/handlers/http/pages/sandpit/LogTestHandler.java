package lto.manager.web.handlers.http.pages.sandpit;

import java.util.logging.Level;

import org.xmlet.htmlapifaster.Div;
import org.xmlet.htmlapifaster.EnumTypeButtonType;
import org.xmlet.htmlapifaster.EnumTypeInputType;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.common.log.Log;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.templates.TemplatePage.BreadCrumbs;
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;
import lto.manager.web.resource.Asset;
import lto.manager.web.resource.CSS;
import lto.manager.web.resource.HTML;

public class LogTestHandler extends BaseHTTPHandler {
	public static final String PATH = Asset.PATH_SANDPIT_BASE + "log";
	public static final String NAME = "Logging Tester";

	static Void content(Div<?> view, BodyModel model) {
		final String message = model.getQueryNoNull("message");
		final String level = model.getQueryNoNull("level");

		view
			.form()
				.attrStyle("display:flex;flex-direction:column;")
				.b().text("Level:").__()
				.select()
					.attrName("level")
					.option()
						.attrValue(Level.FINEST.toString())
						.of(o -> HTML.option(o, level.equals(Level.FINEST.toString())))
						.text(Level.FINEST.toString())
					.__()
					.option()
						.attrValue(Level.FINER.toString())
						.of(o -> HTML.option(o, level.equals(Level.FINER.toString())))
						.text(Level.FINER.toString())
					.__()
					.option()
						.attrValue(Level.FINE.toString())
						.of(o -> HTML.option(o, level.equals(Level.FINE.toString())))
						.text(Level.FINE.toString())
					.__()
					.option()
						.attrValue(Level.INFO.toString())
						.of(o -> HTML.option(o, level.equals(Level.INFO.toString())))
						.text(Level.INFO.toString())
					.__()
					.option()
						.attrValue(Level.WARNING.toString())
						.of(o -> HTML.option(o, level.equals(Level.WARNING.toString())))
						.text(Level.WARNING.toString())
					.__()
					.option()
						.attrValue(Level.SEVERE.toString())
						.of(o -> HTML.option(o, level.equals(Level.SEVERE.toString()) || level.equals("")))
						.text(Level.SEVERE.toString())
					.__()
				.__()
				.br().__()
				.b().text("Message:").__()
				.input()
					.attrType(EnumTypeInputType.TEXT)
					.attrName("message")
					.attrValue(message.equals("") ? "test message" : message)
				.__()
				.br().__()
				.button()
					.attrType(EnumTypeButtonType.SUBMIT)
					.attrClass(CSS.BUTTON)
					.text("Generate Log Message")
				.__()
				.of(f -> {
					if (!message.equals("")) {
						try {
							final Level logLevel = Level.parse(level);
							Log.log(logLevel, message, null);
							f.p()
								.attrStyle("color:green")
								.text("Successfully created log message")
							.__();
						} catch (IllegalArgumentException e) {
							f.p()
								.attrStyle("color:red")
								.text("Failed to parse log level: " + e.getMessage())
							.__();
						} catch (Exception e) {
							f.p()
								.attrStyle("color:red")
								.text("Other exception: " + e.getMessage())
							.__();
						}
					}
					final var logHandlers = Log.getHandlers();
					f.br().__().b().text("Log Handlers").__();
					for (final var handler : logHandlers) {
						f.span()
							.span()
								.attrStyle("display:inline-block;width:16rem")
								.text(handler.getClass().getSimpleName())
							.__()
							.text("[Level = " + handler.getLevel().toString() + "]")
						.__();
					}
				})
			.__();
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he) throws Exception {
		HeadModel thm = HeadModel.of(NAME);
		BreadCrumbs crumbs = new BreadCrumbs().add(SandpitHandler.NAME, SandpitHandler.PATH).add(NAME, PATH);
		TemplatePageModel tpm = TemplatePageModel.of(LogTestHandler::content, null, thm, SelectedPage.Sandpit, BodyModel.of(he, null), crumbs);
		requestHandleCompletePage(he, tpm);
	}

}
