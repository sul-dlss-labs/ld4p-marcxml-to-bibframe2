package main;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.*;

/* Saxonica */
import net.sf.saxon.s9api.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import net.sf.saxon.dom.NodeOverNodeInfo;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;
import java.util.*;
import java.io.*;

import java.text.SimpleDateFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class ConvertToBibframe {

    private static final Logger conversionLog = LogManager.getLogger();

    public static void main(String[] args) throws Exception  {

        Properties props = new Properties(PropGet.getProps("src/main/resources/conversion.conf"));
        String LOGDIR = props.getProperty("LOGDIR");
        String BASEURI = props.getProperty("BASEURI");
        String CREATE_BNODE = props.getProperty("CREATE_BNODE");
        //String CLEANUP = props.getProperty("CLEANUP");

        String savelog = LOGDIR + new SimpleDateFormat("yyyyMMdd'T'HHmmssmmmmmm").format(Calendar.getInstance().getTime()) + ".log.xml";
        String startDT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.mmmmmmZ").format(Calendar.getInstance().getTime());

        Processor proc = new Processor(false);
        DocumentBuilder builder = proc.newDocumentBuilder();
        builder.setLineNumbering(true);
        builder.setWhitespaceStrippingPolicy(WhitespaceStrippingPolicy.ALL);

        DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
        dfactory.setNamespaceAware(true);
        Document rdfxml;
        rdfxml = dfactory.newDocumentBuilder().newDocument();
        Element rdfrdf = rdfxml.createElementNS("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "rdf:RDF");
        //append root element to document
        rdfxml.appendChild(rdfrdf);

        Document log = dfactory.newDocumentBuilder().newDocument();
        Element logroot = log.createElementNS("info:lc/marc2bibframe/logging#", "log:log");
        log.appendChild(logroot);

        conversionLog.info("\nCONVERTING MARCXML TO BIBFRAME\n");
        XdmNode doc = builder.build(new File(args[0]));

        XPathCompiler xpath = proc.newXPathCompiler();
        xpath.declareNamespace("", "http://www.loc.gov/MARC21/slim");

        // find all the record elements
        XPathSelector selector = xpath.compile("//record").load();
        selector.setContextItem(doc);

        XQueryCompiler comp = proc.newXQueryCompiler();
        String query = "import module namespace marcbib2bibframe = \"info:lc/id-modules/marcbib2bibframe#\" at \"../modules/module.MARCXMLBIB-2-BIBFRAME.xqy\"; \n" + 
            "import module namespace RDFXMLnested2flat = \"info:lc/bf-modules/RDFXMLnested2flat#\" at \"../modules/module.RDFXMLnested-2-flat.xqy\"; \n" +
            "declare namespace marcxml       = \"http://www.loc.gov/MARC21/slim\"; \n" + 
            "declare namespace rdf           = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"; \n" + 
            "declare namespace rdfs          = \"http://www.w3.org/2000/01/rdf-schema#\"; \n" + 
            "declare variable $marcxml external; \n" +
            "declare variable $baseuri external; \n" +
            "let $resources := \n" +
            "for $r in $marcxml \n" +
            "let $controlnum := xs:string($r/marcxml:controlfield[@tag eq \"001\"][1]) \n" +
            "let $httpuri := fn:concat($baseuri , $controlnum) \n" +
            "let $bibframe :=  marcbib2bibframe:marcbib2bibframe($r,$httpuri) \n" +
            "let $rdf :=  RDFXMLnested2flat:RDFXMLnested2flat($bibframe,$httpuri,\"" + CREATE_BNODE + "\") \n" +
            "return $rdf \n" +
            "return $resources \n";

        XQueryExecutable exp = comp.compile( query );
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

                //Node tempnode = rdfxml.importNode(n.getDocumentElement(), true);
                //rdfrdf.appendChild(tempnode);

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

                String time = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.mmmmmmZ").format(Calendar.getInstance().getTime());

                Element logentry = log.createElementNS("info:lc/marc2bibframe/logging#", "log:success");
                logroot.appendChild(logentry);

                Attr attruri = log.createAttribute("uri");
                attruri.setValue(BASEURI + cf001);
                logentry.setAttributeNode(attruri);

                Attr attrdt = log.createAttribute("datetime");
                attrdt.setValue(time);
                logentry.setAttributeNode(attrdt);

            } catch (SaxonApiException exception) {
                errors++;

                if (!errorList.isEmpty()) {
                    TransformerException err = errorList.get(0);

                    String time = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.mmmmmmZ").format(Calendar.getInstance().getTime());

                    Element logentry = log.createElementNS("info:lc/marc2bibframe/logging#", "log:error");
                    logroot.appendChild(logentry);

                    Attr attruri = log.createAttribute("uri");
                    attruri.setValue(BASEURI + cf001);
                    logentry.setAttributeNode(attruri);

                    Attr attrdt = log.createAttribute("datetime");
                    attrdt.setValue(time);
                    logentry.setAttributeNode(attrdt);

                    Element errordetails = log.createElementNS("info:lc/marc2bibframe/logging#", "log:error-details");
                    logentry.appendChild(errordetails);

                    Element errorxcode = log.createElementNS("info:lc/marc2bibframe/logging#", "log:error-xcode");
                    errorxcode.appendChild(log.createTextNode(exception.getErrorCode().toString()));
                    errordetails.appendChild(errorxcode);

                    Element errormsg = log.createElementNS("info:lc/marc2bibframe/logging#", "log:error-msg");
                    errormsg.appendChild(log.createTextNode(err.getMessage()));
                    errordetails.appendChild(errormsg);

                    String[] locations = err.getLocationAsString().split(String.valueOf(';'));
                    for (String location : locations) {
                        String elname = "";
                        String[] parts = location.split(String.valueOf(':'));
                        //System.out.println(parts[0].trim());
                        if (parts[0].trim().equals("SystemID")) {
                            elname = "log:error-file";
                        } else if (parts[0].trim().equals("Line#")) {
                            elname = "log:error-line";
                        } else if (parts[0].trim().equals("Column#")) {
                            elname = "log:error-column";
                        }
                        if (!Objects.equals(elname, "")) {
                            String value = location.substring(location.indexOf(":") + 1);
                            //System.out.println(elname);
                            Element errordetail = log.createElementNS("info:lc/marc2bibframe/logging#", elname);
                            errordetail.appendChild(log.createTextNode(value.trim()));
                            errordetails.appendChild(errordetail);
                        }
                    }

                    Element offendingrecord = log.createElementNS("info:lc/marc2bibframe/logging#", "log:offending-record");
                    logentry.appendChild(offendingrecord);

                    XdmNode xn = (XdmNode) item;
                    NodeOverNodeInfo noni = NodeOverNodeInfo.wrap(xn.getUnderlyingNode());
                    Node tempnode = log.importNode(noni, true);
                    offendingrecord.appendChild(tempnode);

                }

            }

        }

        String endDT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.mmmmmmZ").format(Calendar.getInstance().getTime());

        Attr attrengine = log.createAttribute("engine");
        attrengine.setValue("Saxon9he");
        logroot.setAttributeNode(attrengine);

        Attr attrstart = log.createAttribute("start");
        attrstart.setValue(startDT);
        logroot.setAttributeNode(attrstart);

        Attr attrend = log.createAttribute("end");
        attrend.setValue(endDT);
        logroot.setAttributeNode(attrend);

        Attr attrsource = log.createAttribute("source");
        attrsource.setValue(args[0]);
        logroot.setAttributeNode(attrsource);

        Attr attrtotal = log.createAttribute("total-submitted");
        attrtotal.setValue(String.valueOf( successes + errors ));
        logroot.setAttributeNode(attrtotal);

        Attr attrsuccess = log.createAttribute("total-success");
        attrsuccess.setValue(String.valueOf(successes));
        logroot.setAttributeNode(attrsuccess);

        Attr attrerror = log.createAttribute("total-error");
        attrerror.setValue(String.valueOf(errors));
        logroot.setAttributeNode(attrerror);


        DOMSource source = new DOMSource(log.getDocumentElement());
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = tf.newTransformer();
        StreamResult streamResult = new StreamResult(new File(savelog));
        t.transform(source, streamResult);

        // Output XML
        //source = new DOMSource(rdfxml.getDocumentElement());
        Document modSource = ModUris.forBibframe(rdfxml);
        DOMSource output = new DOMSource(modSource);
        streamResult = new StreamResult(System.out);
        
        conversionLog.info("\nWRITING FILE...");
        t.transform(output, streamResult);
        
        conversionLog.info("\nDONE!");
    }

}
