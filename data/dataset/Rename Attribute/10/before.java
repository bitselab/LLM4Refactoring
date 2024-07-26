package com.github.javaparser.printer;

import com.github.javaparser.ast.expr.Expression;
import org.junit.jupiter.api.Test;

import static com.github.javaparser.StaticJavaParser.parseExpression;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

class XmlPrinterTest {

    // Used for building XML documents
    private static DocumentBuilderFactory documentBuilderFactory;
    private static DocumentBuilder db;

    @BeforeAll
    public static void setupDocumentBuilder() {
        try {
            documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            documentBuilderFactory.setCoalescing(true);
            documentBuilderFactory.setIgnoringElementContentWhitespace(true);
            documentBuilderFactory.setIgnoringComments(true);
            db = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            throw new RuntimeException(ex);
        }
    }

    public Document getDocument(String xml) throws SAXException, IOException {
        InputStream inputStream = new ByteArrayInputStream(xml.getBytes());
        Document result = db.parse(inputStream);
        result.normalizeDocument();
        return result;
    }

    public void assertXMLEquals(String xml1, String xml2) throws SAXException, IOException {
        assertTrue(getDocument(xml1).isEqualNode(getDocument(xml2)));
    }

    @Test
    void testWithType() throws SAXException, IOException {
        Expression expression = parseExpression("1+1");
        XmlPrinter xmlOutput = new XmlPrinter(true);

        String output = xmlOutput.output(expression);

        assertXMLEquals("<root type='BinaryExpr' operator='PLUS'><left type='IntegerLiteralExpr' value='1'></left><right type='IntegerLiteralExpr' value='1'></right></root>", output);
    }

    @Test
    void testWithoutType() throws SAXException, IOException {
        Expression expression = parseExpression("1+1");

        XmlPrinter xmlOutput = new XmlPrinter(false);

        String output = xmlOutput.output(expression);

        assertXMLEquals("<root operator='PLUS'><left value='1'></left><right value='1'></right></root>", output);
    }

    @Test
    void testList() throws SAXException, IOException {
        Expression expression = parseExpression("a(1,2)");

        XmlPrinter xmlOutput = new XmlPrinter(true);

        String output = xmlOutput.output(expression);

        assertXMLEquals("<root type='MethodCallExpr'><name type='SimpleName' identifier='a'></name><arguments><argument type='IntegerLiteralExpr' value='1'></argument><argument type='IntegerLiteralExpr' value='2'></argument></arguments></root>", output);
    }
}