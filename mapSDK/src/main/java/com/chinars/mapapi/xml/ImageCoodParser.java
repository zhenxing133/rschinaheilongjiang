package com.chinars.mapapi.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ImageCoodParser extends DefaultHandler {
	private ImageCoordinate mCoordinate;
	private StringBuilder builder;
	
	public ImageCoodParser(ImageCoordinate coordinate){
		this.mCoordinate=coordinate;
		builder = new StringBuilder();
	}
	
	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
	}
	
	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
	}
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		builder. append(ch, start, length);
		super.characters(ch, start, length);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		builder.delete(0, builder.length());
		super.startElement(uri, localName, qName, attributes);
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if(localName.equalsIgnoreCase("X")){
			mCoordinate.x=Double.valueOf(builder.toString());
		}else if(localName.equalsIgnoreCase("Y")){
			mCoordinate.y=Double.valueOf(builder.toString());
		}else if(localName.equalsIgnoreCase("Z")){
			mCoordinate.z=Double.valueOf(builder.toString());
		}else if(localName.equalsIgnoreCase("X1")){
			mCoordinate.x1=Double.valueOf(builder.toString());
		}else if(localName.equalsIgnoreCase("X2")){
			mCoordinate.x2=Double.valueOf(builder.toString());
		}else if(localName.equalsIgnoreCase("X3")){
			mCoordinate.x3=Double.valueOf(builder.toString());
		}else if(localName.equalsIgnoreCase("X4")){
			mCoordinate.x4=Double.valueOf(builder.toString());
		}else if(localName.equalsIgnoreCase("Y1")){
			mCoordinate.y1=Double.valueOf(builder.toString());
		}else if(localName.equalsIgnoreCase("Y2")){
			mCoordinate.y2=Double.valueOf(builder.toString());
		}else if(localName.equalsIgnoreCase("Y3")){
			mCoordinate.y3=Double.valueOf(builder.toString());
		}else if(localName.equalsIgnoreCase("Y4")){
			mCoordinate.y4=Double.valueOf(builder.toString());
		}
		super.endElement(uri, localName, qName);
	}
	
}
