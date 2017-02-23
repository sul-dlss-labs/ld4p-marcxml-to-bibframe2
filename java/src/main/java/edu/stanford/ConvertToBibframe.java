package edu.stanford;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.*;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.*;

import net.sf.saxon.s9api.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.*;
import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class ConvertToBibframe {

    private static final Logger conversionLog = LogManager.getLogger();

    private static Properties props () throws IOException {
        return new Properties(PropGet.getProps(ConvertToBibframe.class.getResource("/conversion.conf").getFile()));
    }

    public static void main(String[] args) throws Exception  {

        String BASEURI = props().getProperty("BASEURI");
        String CREATE_BNODE = props().getProperty("CREATE_BNODE");
        //String CLEANUP = props().getProperty("CLEANUP");
        //String LOGDIR = props().getProperty("LOGDIR");

        Processor proc = new Processor(false);

        DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
        dfactory.setNamespaceAware(true);

        Document rdfxml = dfactory.newDocumentBuilder().newDocument();
        Element rdfrdf = rdfxml.createElementNS("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "rdf:RDF");
        rdfxml.appendChild(rdfrdf);

        DocumentBuilder builder = proc.newDocumentBuilder();
        builder.setLineNumbering(true);
        builder.setWhitespaceStrippingPolicy(WhitespaceStrippingPolicy.ALL);

        XdmNode doc = builder.build(new File(args[0]));

        XPathCompiler xpath = proc.newXPathCompiler();
        xpath.declareNamespace("", "http://www.loc.gov/MARC21/slim");

        XPathSelector selector = xpath.compile("//record").load();
        selector.setContextItem(doc);

        XQueryCompiler comp = proc.newXQueryCompiler();
        XQueryString xQuery = new XQueryString();
        String query = xQuery.query(CREATE_BNODE);
        XQueryExecutable exp = comp.compile(query);
        XQueryEvaluator qe = exp.load();

        final List<TransformerException> errorList = new ArrayList<>();

        ErrorListener listener = new ErrorListener() {
            public void error(TransformerException exception) throws TransformerException {
                errorList.add(exception);
            }

            public void fatalError(TransformerException exception) throws TransformerException {
                errorList.add(exception);
            }

            public void warning(TransformerException exception) throws TransformerException {
                // no action
            }
        };

        qe.setErrorListener(listener);

        Integer successes = 0;
        Integer errors = 0;

        conversionLog.info("\nCONVERTING MARCXML TO BIBFRAME\n");

        for (XdmItem item : selector) {
            XPathSelector cf001s = xpath.compile("controlfield[@tag='001']").load();
            cf001s.setContextItem(item);
            Iterator<XdmItem> itr001 = cf001s.iterator();
            String cf001 = itr001.next().getStringValue();

            try {
                qe.setExternalVariable(new QName("marcxml"), item);
                qe.setExternalVariable(new QName("baseuri"), new XdmAtomicValue(BASEURI));

                Document n;
                n = dfactory.newDocumentBuilder().newDocument();
                qe.run(new DOMDestination(n));

                NodeList nodeList = n.getChildNodes();
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node currentNode = nodeList.item(i);
                    if (currentNode.getNodeType() == Node.ELEMENT_NODE && currentNode.getLocalName().equals("RDF")) {
                        NodeList nl = currentNode.getChildNodes();
                        for (int j = 0; j < nl.getLength(); j++) {
                            Node cn = nl.item(j);
                            if (cn.getNodeType() == Node.ELEMENT_NODE) {
                                Node tempnode = rdfxml.importNode(cn, true);
                                rdfrdf.appendChild(tempnode);
                            }
                        }
                    }
                }
                successes++;

            } catch (SaxonApiException exception) {
                errors++;

                conversionLog.warn("Error for record " + cf001);

                if (!errorList.isEmpty()) {
                    TransformerException err = errorList.get(0);
                    String[] locations = err.getLocationAsString().split(String.valueOf(';'));
                    for (String location : locations) {
                        conversionLog.warn("Record error at location: " + location);
                    }
                }
            }
        }

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = tf.newTransformer();

        DOMSource output = new DOMSource(rdfxml.getDocumentElement());
        StreamResult streamResult = new StreamResult(System.out);

        conversionLog.info("\nOUTPUTTING FILE...");

        t.transform(output, streamResult);

        conversionLog.info("\nDONE!");

        conversionLog.info("Conversion successes: " + successes);
        conversionLog.warn("Conversion errors: " + errors);
    }
}
