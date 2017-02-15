package org.stanford;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Joshua Greben jgreben on 1/13/17.
 * Stanford University Libraries, DLSS
 */
public class XQueryStringTest {
    @Test
    public void query() throws Exception {
        String CREATE_BNODE = "true";
        String expected = "import module namespace marcbib2bibframe = \"info:lc/id-modules/marcbib2bibframe#\" at \"../modules/module.MARCXMLBIB-2-BIBFRAME.xqy\"; \n" +
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

        XQueryString xQuery = new XQueryString();
        Assert.assertEquals(expected, xQuery.query(CREATE_BNODE));
    }
}