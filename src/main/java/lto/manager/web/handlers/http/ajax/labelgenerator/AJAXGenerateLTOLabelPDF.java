package lto.manager.web.handlers.http.ajax.labelgenerator;

import java.awt.Graphics2D;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.util.XMLResourceDescriptor;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.ImgTemplate;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import com.sun.net.httpserver.HttpExchange;

import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.resource.Asset;

public class AJAXGenerateLTOLabelPDF extends BaseHTTPHandler {
	public static final String PATH = Asset.PATH_AJAX_BASE + "generate/lto/label/pdf";

	@Override
	public void requestHandle(HttpExchange he) throws Exception { // FIXME finish PDF scale is wrong
		List<String> labelsSVGs = GenerateLTOLabelSVG.generate(LTOLabelOptions.of(BodyModel.of(he, null)));

		Document document = new Document();
		var stream = new ByteArrayOutputStream();
		try {
			var pdfwriter = PdfWriter.getInstance(document, stream);

			document.open();
			document.add(new Chunk("Test phase - scale is wrong"));

			SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(XMLResourceDescriptor.getXMLParserClassName());

			for (final String svg : labelsSVGs) {
				// final StringReader reader = new StringReader(
				// AssetHandler.getResourceAsString(Asset.IMG_LTO_LABEL + "test.svg"));
				final StringReader reader = new StringReader(svg);
				var SVGDocument = factory.createSVGDocument("", reader);

				UserAgent userAgent = new UserAgentAdapter();
				DocumentLoader loader = new DocumentLoader(userAgent);

				BridgeContext context = new BridgeContext(userAgent, loader);
				context.setDynamicState(BridgeContext.DYNAMIC);

				GVTBuilder builder = new GVTBuilder();
				GraphicsNode rootGraphicsNode = builder.build(context, SVGDocument);

				PdfTemplate template = PdfTemplate.createTemplate(pdfwriter, 400, 100);

				Graphics2D g2d = template.createGraphics(template.getWidth(), template.getHeight(), null);
				var a = new ImgTemplate(template);
				document.add(a);

				try {
					rootGraphicsNode.paint(g2d);
				} finally {
					g2d.dispose();
				}
			}

		} catch (DocumentException | IOException de) {
			System.err.println(de.getMessage());
		}

		document.close();

		requestHandleCompleteAPIBinary(he, stream, BaseHTTPHandler.CONTENT_TYPE_PDF);
	}
}
