package lto.manager.web.handlers.http.pages.admin;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.xmlet.htmlapifaster.Div;
import org.xmlet.htmlapifaster.EnumTypeButtonType;
import org.xmlet.htmlapifaster.EnumTypeInputType;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.common.database.Database;
import lto.manager.common.database.Options;
import lto.manager.common.database.tables.records.RecordOptions;
import lto.manager.common.database.tables.records.RecordOptions.OptionsSetting;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.templates.TemplatePage.BreadCrumbs;
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;
import lto.manager.web.resource.CSS;
import lto.manager.web.resource.HTML;

public class UpdateOptionsHandler extends BaseHTTPHandler {
	public static final String PATH = "/admin/settings";
	public static final String NAME = "Settings";

	public static final String OPTIONS_INDEX = "options-index";
	public static final String OPTIONS_RESET = "reset";

	private static List<RecordOptions> getOptions() {
		try {
			return Database.getAllOptions();
		} catch (SQLException e) {
			return null;
		}
	}

	static Void content(Div<?> view, BodyModel model) {
		final boolean reset = model.getQuery(OPTIONS_RESET) != null;
		if (reset) {
			Options.resetToDefault();
		}
		final List<String> queryList = reset ? new ArrayList<String>() : model.getQueryArrayNotNull(OPTIONS_INDEX);
		if (queryList.size() > 0) {
			List<RecordOptions> updated = RecordOptions.ofBatch(queryList);
			Options.setBatch(updated);
		}

		final List<RecordOptions> options = getOptions();

		view.form()
			.of(f -> {
				if (queryList.size() > 0) f.i().attrStyle("color:green").text("Updated successful").__().br().__();
				if (reset == Boolean.TRUE) f.i().attrStyle("color:green").text("Settings have been reset").__().br().__();
				for (RecordOptions opt: options) {
					final String optionDescription = OptionsSetting.valueOf(opt.getIndex()).toString();
					final String optionStyle = optionDescription.contains("placeholder") ? "display:none" : "";
					switch (opt.getDataType()) {
					case Boolean:
						final boolean value = opt.getValueAsBool();
						final String trueStr = Boolean.toString(true);
						final String falseStr = Boolean.toString(false);
						f
							.b().attrStyle("width:350px;display:inline-block;" + optionStyle).text(optionDescription).__()
							.select().attrName(OPTIONS_INDEX).attrStyle(optionStyle)
								.option().attrValue(trueStr).of(o -> HTML.option(o, value)).text(trueStr) .__()
								.option().attrValue(falseStr).of(o -> HTML.option(o, !value)).text(falseStr).__()
							.__().br().attrStyle(optionStyle).__();
						break;
					case Integer:
						f
							.b().attrStyle("width:350px;display:inline-block;" + optionStyle).text(optionDescription).__()
							.input().attrStyle(optionStyle).attrType(EnumTypeInputType.NUMBER).attrName(OPTIONS_INDEX).attrValue(opt.getValueAsString()).__()
							.br().attrStyle(optionStyle).__();
						break;
					case String:
						f
							.b().attrStyle("width:350px;display:inline-block;" + optionStyle).text(optionDescription).__()
							.input().attrStyle(optionStyle).attrType(EnumTypeInputType.TEXT).attrName(OPTIONS_INDEX).attrValue(opt.getValueAsString()).__()
							.br().attrStyle(optionStyle).__();
						break;
					default:
						f
							.b().attrStyle("width:350px;display:inline-block;").text("Unknown option: " + opt.getIndex()).__()
							.i().text("[" + opt.getDataType().name() + "] value: " + opt.getValueAsString()).__()
							.br().__();
						break;
					}
				}
			})
			.button().attrClass(CSS.BUTTON).attrType(EnumTypeButtonType.SUBMIT).text("Update").__()
			.button()
				.attrClass(CSS.BUTTON + CSS.BACKGROUND_CAUTION)
				.attrType(EnumTypeButtonType.SUBMIT)
				.attrName(OPTIONS_RESET)
				.attrValue(OPTIONS_RESET)
				.text("Reset to Default")
			.__()
		.__();
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he) throws IOException, InterruptedException, ExecutionException {
		HeadModel thm = HeadModel.of(NAME);
		BreadCrumbs crumbs = new BreadCrumbs().add(AdminHandler.NAME, AdminHandler.PATH).add(NAME, PATH);
		TemplatePageModel tpm = TemplatePageModel.of(UpdateOptionsHandler::content, null, thm, SelectedPage.Admin, BodyModel.of(he, null), crumbs);
		requestHandleCompletePage(he, tpm);
	}

}
