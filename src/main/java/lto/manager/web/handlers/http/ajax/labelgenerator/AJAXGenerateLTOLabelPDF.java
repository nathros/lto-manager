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

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.ImgTemplate;
import com.lowagie.text.RectangleReadOnly;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import com.sun.net.httpserver.HttpExchange;

import lto.manager.common.database.tables.records.RecordRole.Permission;
import lto.manager.web.handlers.http.BaseHTTPHandler;
import lto.manager.web.handlers.http.ajax.labelgenerator.LTOPaperTypeMap.LTOPageType;
import lto.manager.web.handlers.http.templates.models.BodyModel;
import lto.manager.web.resource.Asset;

public class AJAXGenerateLTOLabelPDF extends BaseHTTPHandler {
	public static final String PATH = Asset.PATH_AJAX_BASE + "generate/lto/label/pdf/";
	private static final float L_WIDTH = (float) 222.52; // Label width in PT
	private static final float L_HEIGHT = (float) 46.77; // Label height in PT

	@Override
	public void requestHandle(HttpExchange he, BodyModel bm) throws Exception {
		List<String> labelsSVGs = GenerateLTOLabelSVG.generate(LTOLabelOptions.of(bm));
		final String paperKey = bm.getQueryNoNull(LTOLabelOptions.QUERY_PAPER);
		LTOPageType paperType = LTOPaperTypeMap.getPaperType(paperKey);
		if (paperType == null) {
			paperType = LTOPaperTypeMap.getDefaultPaperType();
		}

		Document document = new Document(new RectangleReadOnly(paperType.pageWidthPT(), paperType.pageHeightPT()));
		var stream = new ByteArrayOutputStream();
		try {
			var pdfwriter = PdfWriter.getInstance(document, stream);
			document.open();
			SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(XMLResourceDescriptor.getXMLParserClassName());
			int count = 0;
			float xPos = paperType.xStart();
			float yPos = paperType.yStart(); // y is flipped

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

				PdfTemplate template = PdfTemplate.createTemplate(pdfwriter, 310, 100);

				Graphics2D g2d = template.createGraphics(template.getWidth(), template.getHeight(), null);
				var newBarcode = new ImgTemplate(template);
				// Not sure why the size is wrong, the SVG size is correct
				// width="80.5mm" height="18.5mm"
				newBarcode.scalePercent(75);

				if (count % paperType.labelsPerPage() == 0) {
					// Filled this page, create new
					document.newPage();
					count = 0;
					xPos = paperType.xStart();
					yPos = paperType.yStart();
					newBarcode.setAbsolutePosition(xPos, yPos);
				} else if (count % paperType.columnCount() == 0) {
					// Filled row move to next
					xPos = paperType.xStart();
					yPos = yPos - L_HEIGHT - paperType.yOffset();
					newBarcode.setAbsolutePosition(xPos, yPos);
				} else {
					// Move to next column
					xPos = xPos + L_WIDTH + paperType.xOffset();
					newBarcode.setAbsolutePosition(xPos, yPos);
				}

				document.add(newBarcode);

				try {
					rootGraphicsNode.paint(g2d);
				} finally {
					g2d.dispose();
				}

				count++;
			}

		} catch (DocumentException | IOException de) {
			System.err.println(de.getMessage());
		}

		document.close();

		requestHandleCompleteAPIBinary(he, stream, BaseHTTPHandler.CONTENT_TYPE_PDF);
	}

	@Override
	public Permission getHandlePermission() {
		return null;
	}
}
