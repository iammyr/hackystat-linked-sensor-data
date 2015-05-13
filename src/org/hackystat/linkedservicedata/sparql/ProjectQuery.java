package org.hackystat.linkedservicedata.sparql;

import java.util.HashMap;
import java.util.LinkedList;
import com.hp.hpl.jena.ontology.OntProperty;

/**
 * Utility class to group all the project features as filters for Sparql query on Projects.
 *
 * @author Myriam Leggieri.
 *
 */
public class ProjectQuery {
  public String projectname = null;
  public String owner = null;
  public String start = null;
  public String end = null;
  public String modified = null;
  public String description = null;
  public String[] progrlangs = null;
  public String[] osystems = null;
  public String[] bugdatabases = null;
  public String[] mirrors = null;
  public String repwebinterface = null;
  public String repanon = null;
  public String rep = null;
  public String[] wikis = null;
  public String[] tags = null;
  public String[] tools = null;
  public String[] devPhases = null;
  public String[] invitations = null;
  public String[] spectators = null;
  public String[] members = null;
  public String[] seeAlso = null;
  public String[] sameAs = null;
  public HashMap<OntProperty, String[]> usersRoles = null;
  public LinkedList<SparqlFilter> filtersList = null;
}
