package com.talient.util;

import java.io.File;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;

import org.jdom.transform.JDOMSource;
import org.jdom.transform.JDOMResult;
import org.jdom.input.SAXBuilder;
import org.jdom.Document;
import org.jdom.JDOMException;

public class XsltProcess {
    JDOMSource xmlsource;
    JDOMSource xslsource;
    Transformer transformer;

    public XsltProcess(File xslFile) {
        SAXBuilder builder = new SAXBuilder();
        try {
            Document xsldoc = builder.build(xslFile);
            xslsource = new JDOMSource(xsldoc);
        }
        catch (JDOMException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        TransformerFactory transfact = TransformerFactory.newInstance();
        try {
            transformer = transfact.newTransformer(xslsource);
        }
        catch (TransformerConfigurationException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    public XsltProcess(URL xslUrl) {
        SAXBuilder builder = new SAXBuilder();
        try {
            Document xsldoc = builder.build(xslUrl);
            xslsource = new JDOMSource(xsldoc);
        }
        catch (JDOMException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        TransformerFactory transfact = TransformerFactory.newInstance();
        try {
            transformer = transfact.newTransformer(xslsource);
        }
        catch (TransformerConfigurationException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    public void transform(Document xmldoc, File outfile) {
        JDOMResult result = new JDOMResult();
        xmlsource = new JDOMSource(xmldoc);
        try {
            transformer.transform(xmlsource, new StreamResult(outfile));
        }
        catch (TransformerException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    public String transform(Document xmldoc) {
        JDOMResult result = new JDOMResult();
        xmlsource = new JDOMSource(xmldoc);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            transformer.transform(xmlsource, new StreamResult(os));
        }
        catch (TransformerException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        return os.toString();
    }

    public void transform(Document xmldoc, OutputStream os) {
        JDOMResult result = new JDOMResult();
        xmlsource = new JDOMSource(xmldoc);
        try {
            transformer.transform(xmlsource, new StreamResult(os));
        }
        catch (TransformerException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    static public void main(String argv[]) {
        com.talient.football.jdbc.JDBCHomes.setHomes();
        com.talient.football.reports.Crosstable ct =
            com.talient.football.reports.Crosstable.getHome().findByYearWeek(1997, 1);
        URL xsl = ct.getClass().getResource("/com/talient/football/view/html/xslt/Crosstable.xslt");
        if (xsl == null) {
            System.out.println("Could not find resource.\n");
            System.exit(1);
        }
        XsltProcess process = new XsltProcess(xsl);
        try {
            String str = process.transform(ct.getJDOM());
            System.out.println(str);
        }
        catch (MissingPropertyException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        System.exit(0);
                                        
    }
}
