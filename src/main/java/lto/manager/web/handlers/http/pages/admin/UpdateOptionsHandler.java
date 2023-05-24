package lto.manager.web.handlers.http.pages.admin;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

import org.xmlet.htmlapifaster.Div;
import org.xmlet.htmlapifaster.EnumTypeButtonType;

import com.sun.net.httpserver.HttpExchange;

import lto.manager.common.database.Database;
import lto.manager.common.database.Options;
import lto.manager.common.database.tables.records.RecordOptions;
import lto.manager.common.database.tables.records.RecordOptions.OptionsSetting;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.templates.TemplatePage.SelectedPage;
import lto.manager.web.handlers.http.templates.TemplatePage.TemplatePageModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.handlers.http.templates.models.HeadModel;
import lto.manager.web.resource.CSS;
import lto.manager.web.resource.HTML;

public class UpdateOptionsHandler extends BaseHTTPHandler {
	public static final String PATH = "/admin/options";

	public static final String OPTIONS_INDEX = "options-index";

	private static Supplier<List<RecordOptions>> getOptions = () -> {
		try {
			return Database.getAllOptions();
		} catch (SQLException e) {
			return null;
		}
	};

	static Void content(Div<?> view, BodyModel model) {
		final List<String> queryList = model.getQueryArrayNotNull(OPTIONS_INDEX);
		if (queryList.size() > 0) {
			List<RecordOptions> updated = RecordOptions.ofBatch(queryList);
			for (int i = 0; i < updated.size(); i++) {
				OptionsSetting setting = OptionsSetting.valueOf(i);
				Options.setStr(setting, updated.get(i).getValueAsString());
			}
		}

		final List<RecordOptions> options = getOptions.get();

		view.form()
		.of(f -> {
			for (RecordOptions opt: options) {
				switch (opt.getDataType()) {
				case Boolean:
					final boolean value = opt.getValueAsBool();
					final String trueStr = Boolean.toString(true);
					final String falseStr = Boolean.toString(false);
					f
						.b().attrStyle("width:350px;display: inline-block;").text(OptionsSetting.valueOf(opt.getIndex()).toString()).__()
						.select().attrName(OPTIONS_INDEX)
							.option().attrValue(trueStr).of(o -> HTML.option(o, value)).text(trueStr) .__()
							.option().attrValue(falseStr).of(o -> HTML.option(o, !value)).text(falseStr).__()
						.__().br().__();
					break;
				case Integer:
					break;
				case String:
					break;
				default:
					break;
				}
			}
		})
		.button().attrClass(CSS.BUTTON).attrType(EnumTypeButtonType.SUBMIT).text("Update").__()
		.__();
		return null;
	}

	@Override
	public void requestHandle(HttpExchange he) throws IOException, InterruptedException, ExecutionException {
		HeadModel thm = HeadModel.of("Admin Options");
		TemplatePageModel tpm = TemplatePageModel.of(UpdateOptionsHandler::content, thm, SelectedPage.Admin, BodyModel.of(he, null));
		requestHandleCompletePage(he, tpm);
	}

}
