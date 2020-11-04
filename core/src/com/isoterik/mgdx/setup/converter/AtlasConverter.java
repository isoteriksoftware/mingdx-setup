package com.isoterik.mgdx.setup.converter;

import com.badlogic.gdx.files.FileHandle;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;

public class AtlasConverter {
    private InputStream in;
    private XmlParams xml;
    private AtlasParams atlas;
    private FileHandle atlasFile;
    private StringBuffer buffer;

    public AtlasConverter(XmlParams xml, 
						  AtlasParams atlas, FileHandle xmlFile,
						  FileHandle atlasFile) {
        this.xml = xml;
        this.atlas = atlas;
        this.atlasFile = atlasFile;

        in = xmlFile.read();
    }

    public void convert() throws Exception {
        Document doc = null;

	    DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	    doc = docBuilder.parse(in);

	    Element root = doc.getDocumentElement();
	    if( !root.getTagName().equals(xml.atlasTag)) {
	        System.err.println("This is not a valid " + xml.atlasTag + " xml File");
	        throw new IllegalArgumentException("Atlas Tag name does not correspond to the parameter passed!");
	    }

	    buffer = new StringBuffer();
	    writeAtlasHeader(root);

	    NodeList nodes = root.getChildNodes();
	    for (int i=0; i<nodes.getLength(); i++) {
	        Node node = nodes.item(i);
	        if (node instanceof Element) {
	            Element el = (Element)node;
	            String eln = el.getTagName();
	            if(eln.equals(xml.textureTag))
					addRegion(el);
	        }
	    }

	    finishConversion();
    }

    private void append(String s) {
        buffer.append(s).append("\n");
    }

    private String attr(Element el, String attribute) throws IllegalStateException {
        if (!el.hasAttribute(attribute))
			throw new IllegalStateException("Attribute *" + attribute + "* does not exist!");

		return (el.getAttribute(attribute));
    }

    private int toInt( String string) {
        return ( Integer.parseInt(string));
    }

    private void writeAtlasHeader(Element root) {
        String path = attr(root, xml.pathAttr);
        String size = atlas.atlasWidth + "," +
			atlas.atlasHeight;
        String format = atlas.format;
        String filter = atlas.minFilter + "," +
			atlas.magFilter;
        String repeat = atlas.repeat;

        append("");
        append(path);
        append("size: " + size);
        append("format: " + format);
        append("filter: " + filter);
        append("repeat: " + repeat);
    }

    private void addRegion(Element el) {
		String name = attr(el, xml.nameAttr);
		int x = toInt(attr(el, xml.xAttr));
		int y = toInt(attr(el, xml.yAttr));
		int w = toInt(attr(el, xml.wAttr));
		int h = toInt(attr(el, xml.hAttr));

		FileHandle t = new FileHandle(name);
		name = t.nameWithoutExtension();
		x += atlas.xTuning;
		y += atlas.yTuning;
		w += atlas.wTuning;
		h += atlas.hTuning;

		append(name);
		append("  rotate: false");
		append("  xy: " + x + "," + y);
		append("  size: " + w + "," + h);
		append("  orig: " + w + "," + h);
		append("  offset: 0,0");
		append("  index: -1");
    }

    private void finishConversion() {
		atlasFile.writeString(buffer.toString(),
							  false);
    }
}
