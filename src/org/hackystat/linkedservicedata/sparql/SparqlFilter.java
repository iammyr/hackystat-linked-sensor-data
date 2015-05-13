package org.hackystat.linkedservicedata.sparql;

import org.hackystat.linkedservicedata.vocabularies.BaetleVocab;
import org.hackystat.linkedservicedata.vocabularies.DoapVocab;
import org.hackystat.linkedservicedata.vocabularies.FoafVocab;
import org.hackystat.linkedservicedata.vocabularies.HackystatVocab;
import org.hackystat.linkedservicedata.vocabularies.Helios_btVocab;
import org.hackystat.linkedservicedata.vocabularies.IswcVocab;
import org.hackystat.linkedservicedata.vocabularies.ProcessVocab;
import org.hackystat.linkedservicedata.vocabularies.SecVocab;
import org.hackystat.linkedservicedata.vocabularies.SiocVocab;
import org.hackystat.linkedservicedata.vocabularies.VomVocab;
import com.hp.hpl.jena.ontology.ObjectProperty;

/**
 * Utility class to group all the features as filters for quality measures associated with users or
 * projects.
 *
 * @author Myriam Leggieri.
 *
 */
public class SparqlFilter {
  public static final String OP_LESS_THAN = "<";
  public static final String OP_GREATER_THAN = ">";
  public static final String OP_EQUALS_TO = "=";
  private ObjectProperty predicate = null;
  private String operator = null;
  private String value = null;
  private Double numericValue = null;
  private String prefix = null;
  private String predicateLocalName = null;
  private String predicateNamespace = null;

  public SparqlFilter() {

  }

  public SparqlFilter(ObjectProperty predicate, String operator, Object value) throws Exception {
    setPredicate(predicate);
    if (!setOperator(operator)) {
      throw new Exception("Invalid operator for Sparql filter." + "Allowed operators are only "
          + OP_LESS_THAN + ", " + OP_GREATER_THAN + ", " + OP_EQUALS_TO + ".");
    }
    if (value instanceof String) {
      setValue(String.valueOf(value));
    }
    else if (value instanceof Double) {
      setValue(Double.valueOf(String.valueOf(value)));
    }
    else {
      throw new Exception("Invalid value for Sparql filter."
          + "Allowed values are only String and Double.");
    }
  }

  public void setValue(String value) {
    value = "\"" + value + "\"";
  }

  public Object getValue() {
    if (value != null)
      return value;
    return numericValue;
  }

  public void setValue(Double value) {
    numericValue = value;
  }

  public boolean setOperator(String operator) {
    boolean ret = false;
    if (operator.equals(OP_LESS_THAN) || operator.equals(OP_GREATER_THAN)
        || operator.equals(OP_EQUALS_TO)) {
      this.operator = operator;
      ret = true;
    }
    return ret;
  }

  public String getOperator() {
    return operator;
  }

  public void setPredicate(ObjectProperty predicate) {
    this.predicate = predicate;
    String uripred = predicate.getURI();
    setOntoFeatures(uripred);
    this.predicateLocalName = this.predicate.getLocalName();
  }

  private void setOntoFeatures(String uripred) {
    if (uripred.startsWith(BaetleVocab.NS)) {
      this.prefix = BaetleVocab.PREFIX;
      this.predicateNamespace = BaetleVocab.NS;
    }
    else if (uripred.startsWith(DoapVocab.NS)) {
      this.prefix = DoapVocab.PREFIX;
      this.predicateNamespace = DoapVocab.NS;
    }
    else if (uripred.startsWith(FoafVocab.NS)) {
      this.prefix = FoafVocab.PREFIX;
      this.predicateNamespace = FoafVocab.NS;
    }
    else if (uripred.startsWith(HackystatVocab.NS)) {
      this.prefix = HackystatVocab.PREFIX;
      this.predicateNamespace = HackystatVocab.NS;
    }
    else if (uripred.startsWith(Helios_btVocab.NS)) {
      this.prefix = Helios_btVocab.PREFIX;
      this.predicateNamespace = Helios_btVocab.NS;
    }
    else if (uripred.startsWith(IswcVocab.NS)) {
      this.prefix = IswcVocab.PREFIX;
      this.predicateNamespace = IswcVocab.NS;
    }
    else if (uripred.startsWith(ProcessVocab.NS)) {
      this.prefix = ProcessVocab.PREFIX;
      this.predicateNamespace = ProcessVocab.NS;
    }
    // else if (uripred.startsWith(ResVocab.NS)){
    // this.prefix=ResVocab.PREFIX;
    // this.predicateNamespace=ResVocab.NS;
    // }
    else if (uripred.startsWith(SecVocab.NS)) {
      this.prefix = SecVocab.PREFIX;
      this.predicateNamespace = SecVocab.NS;
    }
    else if (uripred.startsWith(SiocVocab.NS)) {
      this.prefix = SiocVocab.PREFIX;
      this.predicateNamespace = SiocVocab.NS;
    }
    else if (uripred.startsWith(VomVocab.NS)) {
      this.prefix = VomVocab.PREFIX;
      this.predicateNamespace = VomVocab.NS;
    }
  }

  public String getPredicateNamespace() {
    return this.predicateNamespace;
  }

  public String getPredicateLocalName() {
    return this.predicateLocalName;
  }

  public String getPrefix() {
    return this.prefix;
  }

}
