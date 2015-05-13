package org.hackystat.linkedservicedata.externalDatasets;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.hackystat.linkedservicedata.vocabularies.FoafVocab;
import org.hackystat.linkedservicedata.vocabularies.HackystatConstants;
import org.restlet.Client;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Preference;
import org.restlet.data.Protocol;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;

/**
 * Handler of requests for the Sindice API.
 *
 * @author Myriam Leggieri.
 *
 */
public class SindiceApiManager {

  /**
   *
   * @param email
   * @param homepage
   * @param weblog
   * @param name
   * @param surname
   * @return
   */
  public static String findFoafOnSindice(String email, String homepage, String weblog, String name,
      String surname) {
    String ret = null, appendice = "&qt=advanced", foafName = null, foafSurname = null, foafHomepage = null, foafWeblog = null, foafEmail = null, query = "", wildcard = "*", space = " ", ang1 = "<", ang2 = ">", virg = "\"", nl = "\n", and = "AND", par1 = "(", par2 = ")", or = "OR";
    if (name != null || surname != null) {
      if (name != null) {
        foafName = FoafVocab.FIRST_NAME.getURI();
        query += par1 + wildcard + space + ang1 + foafName + ang2 + space + virg + name + virg + nl
            + or + nl;
        foafName = FoafVocab.NAME.getURI();
        query += wildcard + space + ang1 + foafName + ang2 + space + virg + name + virg + par2;
      }
      if (surname != null) {
        if (!query.equals("")) {
          query += nl + and + nl;
        }
        foafSurname = FoafVocab.SURNAME.getURI();
        query += par1 + wildcard + space + ang1 + foafSurname + ang2 + space + virg + surname
            + virg + nl + or + nl;
        foafSurname = FoafVocab.FAMILY_NAME.getURI();
        query += wildcard + space + ang1 + foafSurname + ang2 + space + virg + surname + virg
            + par2;
      }
      if (name != null && surname != null) {
        if (!query.equals("")) {
          query += nl + or + nl;
        }
        foafName = FoafVocab.NAME.getURI();
        query += wildcard + space + ang1 + foafName + ang2 + space + virg + name + space + surname
            + virg;
      }
    }
    else {
      /**
       * Sindice rarely finds useful results when a foaf profile is searched by mean of a homepage
       * or weblog or email. That's why if a name and surname are available here, a query that
       * involves only them is preferred.
       **/
      if (email != null) {
        if (!query.equals("")) {
          query += nl + and + nl;
        }
        foafEmail = FoafVocab.MBOX.getURI();
        query += wildcard + space + ang1 + foafEmail + ang2 + space + virg + email + virg;
      }
      if (homepage != null) {
        if (!query.equals("")) {
          query += nl + and + nl;
        }
        foafHomepage = FoafVocab.HOMEPAGE.getURI();
        query += wildcard + space + ang1 + foafHomepage + ang2 + space + virg + homepage + virg;
      }
      if (weblog != null) {
        if (!query.equals("")) {
          query += nl + and + nl;
        }
        foafWeblog = FoafVocab.WEBLOG.getURI();
        query += wildcard + space + ang1 + foafWeblog + ang2 + space;
      }
    }
    ret = makeSindiceRequest(query, appendice);
    return ret;
  }

  /**
   *
   * @param query
   * @param appendice
   * @return
   */
  private static String makeSindiceRequest(String query, String appendice) {
    String ret = null;
    try {
      query = java.net.URLEncoder.encode(query, "UTF-8");
      String urlStr = HackystatConstants.SINDICE_PATTERN + query + appendice;
      Reference reference = new Reference(urlStr);
      Request request = null;
      request = new Request(Method.GET, reference);
      System.out.println("LinkedServiceData Tracing: " + Method.GET + " " + reference);
      request.getClientInfo().getAcceptedMediaTypes().clear();
      request.getClientInfo().getAcceptedMediaTypes().add(
          new Preference<MediaType>(MediaType.APPLICATION_RDF_XML));
      Response response = new Client(Protocol.HTTP).handle(request);
      if (response != null) {
        String resp = response.getEntity().getText();
        Status status = response.getStatus();
        System.out.println("  => " + status.getCode() + " " + status.getDescription());
        if (status.getCode() == 200) {
          if (resp != null) {
            Model rdfModel = ModelFactory.createDefaultModel();
            InputStream is = new ByteArrayInputStream(resp.getBytes("UTF-8"));
            rdfModel.read(is, HackystatConstants.SINDICE_BASE_URI,
                HackystatConstants.LANG_RDFXML_ABBREV);
            // rdfModel= LinkedServiceDataResource.deserializeRDFModel(resp,
            // HackystatConstants.SINDICE_BASE_URI, HackystatConstants.RDFXML_ABBREV_LANG);

            Resource subj = rdfModel.createResource("http://sindice.com/vocab/fields#result1");
            Property pred = rdfModel.createProperty("http://sindice.com/vocab/search#link");
            StmtIterator it = rdfModel.listStatements(subj, pred, (RDFNode) null);
            RDFNode nod = null;
            if (it.hasNext()) {
              nod = it.next().getObject();
              if (nod.isLiteral()) {
                ret = ((Literal) nod).getLexicalForm();
              }
              else if (nod.isResource()) {
                ret = ((Resource) nod).getURI();
              }
            }
          }
        }
        else {
          System.err.println("Request failed.");
        }
      }
    }
    catch (Exception e) {
      System.err.println("Unable to perform search on Sindice");
      e.printStackTrace();
    }
    return ret;
  }

  /**
   *
   * @param elem
   * @param andorOperator
   * @param className
   * @return
   */
  public static String performTermSearch(String[] elem, String andorOperator, String className) {
    String ret = null, uri = "(", appendice = "&qt=term", empty = "";
    boolean valid = false;
    for (int i = 0; i < elem.length; i++) {
      if (elem[i] != null && !elem[i].trim().equals(empty)) {
        valid = true;
        uri += elem[i] + " " + andorOperator + " ";
      }
    }
    if (valid) {
      uri += ")";
      if (className != null) {
        uri += " class:" + className;
      }
      ret = makeSindiceRequest(uri, appendice);
    }
    return ret;
  }

}
