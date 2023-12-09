package lto.manager.web.handlers.http.ajax.labelgenerator;

import java.awt.Graphics2D;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.util.XMLResourceDescriptor;
import org.xmlet.htmlapifaster.Div;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.ImgTemplate;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import com.sun.net.httpserver.HttpExchange;

import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.templates.TemplateAJAX.TemplateFetcherModel;
import lto.manager.web.handlers.http.templates.models.BodyModel;

public class AJAXGenerateLTOLabelPDF extends BaseHTTPHandler {
	public static final String PATH = "/ajax/generate/lto/label/pdf";

	public static Void content(Div<?> view, BodyModel model) {

		return null;
	}

	@Override
	public void requestHandle(HttpExchange he) throws Exception {
		BodyModel bm = BodyModel.of(he, null);
		requestHandleCompleteFetcher(he, new TemplateFetcherModel(AJAXGenerateLTOLabelPDF::content, bm));

		Document document = new Document();

		try {
			var pdfwritet = PdfWriter.getInstance(document, new FileOutputStream("Images.pdf"));

			document.open();

			SAXSVGDocumentFactory factory = new org.apache.batik.anim.dom.SAXSVGDocumentFactory(
					XMLResourceDescriptor.getXMLParserClassName());
			final StringReader reader = new StringReader(Files.readString(Path.of(
					"/home/calrec/eclipse-workspace-java/lto-manager/src/main/resources/lto/manager/web/assets/img/barcode-segments/label.svg"),
					StandardCharsets.UTF_8));
			var SVGDocument = factory.createSVGDocument("", reader);

			UserAgent userAgent = new UserAgentAdapter();
			DocumentLoader loader = new DocumentLoader(userAgent);
			// Notice, that you should use
			// org.apache.batik.bridge.svg12.SVG12BridgeContext.SVG12BridgeContext for the
			// svg version 1.2

			BridgeContext context = new BridgeContext(userAgent, loader);
			context.setDynamicState(BridgeContext.DYNAMIC);

			GVTBuilder builder = new GVTBuilder();
			GraphicsNode rootGraphicsNode = builder.build(context, SVGDocument);

			PdfTemplate template = PdfTemplate.createTemplate(pdfwritet, 400, 100);

			Graphics2D g2d = template.createGraphics(template.getWidth(), template.getHeight(), null);
			var a = new ImgTemplate(template);
			document.add(a);
			document.add(a);
			try {
				rootGraphicsNode.paint(g2d);
			} finally {
				g2d.dispose();
			}

		} catch (DocumentException | IOException de) {
			System.err.println(de.getMessage());
		}

		document.close();

	}
}
