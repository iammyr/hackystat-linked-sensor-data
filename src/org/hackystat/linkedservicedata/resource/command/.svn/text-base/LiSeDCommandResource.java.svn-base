package org.hackystat.linkedservicedata.resource.command;

import java.util.List;
import org.hackystat.linkedservicedata.externalDatasets.SindiceApiManager;
import org.hackystat.linkedservicedata.resource.linkedservicedata.LinkedServiceDataResource;
import org.hackystat.linkedservicedata.vocabularies.HackystatConstants;
import org.hackystat.linkedservicedata.vocabularies.HackystatVocab;
import org.hackystat.sensorbase.client.SensorBaseClient;
import org.hackystat.sensorbase.client.SensorBaseClientException;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorData;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorDataIndex;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorDataRef;
import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * Construct the command resource.
 *
 * @author Myriam Leggieri.
 *
 */
public class LiSeDCommandResource extends LinkedServiceDataResource {
  protected String commandName = null;
  protected String result = null;
  private String machine = null;
  private String os = null;

  public LiSeDCommandResource(Context context, Request request, Response response) {
    super(context, request, response);
    this.commandName = ((String) request.getAttributes().get("commandName"));
    this.result = ((String) request.getAttributes().get("result"));
  }

  @Override
  public Representation represent(Variant variant) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * Creates and returns a resource representing the requested command.
   *
   * @param m_returned the RDF model to which data have to be added
   * @return A resource added to the give model, which represents the requested command.
   * @throws Exception
   */
  protected Resource makeCommandData(Model m_returned) throws Exception {
    Resource resource = null;
    resource = createCommandResource(resource, m_returned, uriUser, commandName);
    // browse SD owned by the given user, to find any result that has been returned
    // and any parameter value passed to this command
    SensorBaseClient client = getSensorBaseClient();
    if (client == null) {
      resource = addCommandData(resource, client);
      resource = addLinkedData(resource);
    }
    resource.addProperty(m_returned
        .createProperty("http://dublincore.org/documents/dcmi-terms/#terms-isPartOf"),
        HackystatConstants.voIDURI);
    return resource;
  }

  private Resource addLinkedData(Resource resource) {
    // SINDICE
    String uri = SindiceApiManager.performTermSearch(new String[] { commandName, machine, os },
        "AND", null);
    if (uri != null) {
      resource.addProperty(RDFS.seeAlso, uri);
    }
    return resource;
  }

  private Resource addCommandData(Resource resource, SensorBaseClient client)
      throws SensorBaseClientException {
    SensorDataIndex sdind = client.getSensorDataIndex(uriUser, HackystatConstants.CLI);
    List<SensorDataRef> list = sdind.getSensorDataRef();
    if (list != null) {
      String parameters = null, result = null;
      SensorData sd = null;
      String[] elems = null;
      for (SensorDataRef sdref : list) {
        sd = client.getSensorData(sdref);
        parameters = getSensorDataProperty(HackystatConstants.COMMAND_ARGUMENTS, "", sd).getValue();
        result = getSensorDataProperty(HackystatConstants.COMMAND_RESULT, "", sd).getValue();
        machine = getSensorDataProperty(HackystatConstants.MACHINE, "", sd).getValue();
        os = getSensorDataProperty(HackystatConstants.OPERATING_SYSTEM, "", sd).getValue();
        if (parameters != null) {
          elems = parameters.split(HackystatConstants.SEPARATOR1_ID);
          for (int i = 0; i < elems.length; i++) {
            resource.addProperty(HackystatVocab.HAS_PARAMETER, elems[i]);
          }
        }
        if (result != null) {
          elems = result.split(HackystatConstants.SEPARATOR2_ID);
          for (int i = 0; i < elems.length; i++) {
            resource.addProperty(HackystatVocab.HAS_RESULT, elems[i]);
          }
        }
        if (machine != null) {
          resource.addProperty(HackystatVocab.ON_MACHINE, getMachineUri(machine));
        }
        if (os != null) {
          resource.addProperty(HackystatVocab.ON_OPERATING_SYSTEM, getOperatingSystemUri(os));
        }
      }
    }
    return resource;
  }
}
