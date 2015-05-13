package org.hackystat.linkedservicedata.resource.network;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import org.hackystat.linkedservicedata.resource.linkedservicedata.LinkedServiceDataResource;
import org.hackystat.linkedservicedata.vocabularies.HackystatConstants;
import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

/**
 * Constrcut a network resource.
 *
 * @author Myriam Leggieri.
 *
 */
public class LiSeDNetworkResource extends LinkedServiceDataResource {
  protected String uri = null;
  protected String admin = null;
  protected String password = null;

  public LiSeDNetworkResource(Context context, Request request, Response response) {
    super(context, request, response);
    try {
      this.uri = (String) request.getAttributes().get("uri");
      if (this.uri != null) {
        this.uri = java.net.URLDecoder.decode(this.uri, "UTF-8");
      }
    }
    catch (Exception e) {
      setStatusError("Error creating LiSeD. "
          + "The URI has been encoded using an unsupported encoding scheme.", e);
      e.printStackTrace();
      return;
    }
  }

  @Override
  public Representation represent(Variant variant) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   *
   * @param uri
   * @param user
   * @param passw
   * @throws IOException
   */
  public static final synchronized void addExternalHackystatLiSeDServer(String uri, String user,
      String passw) throws IOException {
    if (!uri.endsWith("/"))
      uri += "/";
    if (HackystatConstants.hackystatLisedServers_list.containsKey(uri)) {
      String[] elem = HackystatConstants.hackystatLisedServers_list.get(uri);
      elem[0] = user;
      elem[1] = passw;
    }
    else {
      HackystatConstants.hackystatLisedServers_list.put(uri, new String[] { user, passw });
    }
    serializeChanges();

  }

  /**
   *
   * @throws IOException
   */
  private static void serializeChanges() throws IOException {
    // serialize the changed list
    try {
      // Serialize to a file
      ObjectOutput out = new ObjectOutputStream(new FileOutputStream(
          HackystatConstants.hackystatLisedServers_serializedObj));
      out.writeObject(HackystatConstants.hackystatLisedServers_list);
      out.close();
    }
    catch (IOException e) {
      e.printStackTrace();
      throw new IOException(e.getMessage());
    }
  }

  /**
   *
   * @throws FileNotFoundException
   * @throws IOException
   * @throws ClassNotFoundException
   */
  @SuppressWarnings("unchecked")
  public static final synchronized void loadList() throws FileNotFoundException, IOException,
      ClassNotFoundException {
    // deserialize the list of servers
    // Deserialize from a file
    File file = new File(HackystatConstants.hackystatLisedServers_serializedObj);
    if (file.exists()) {
      ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
      // Deserialize the object
      HackystatConstants.hackystatLisedServers_list = (java.util.HashMap<String, String[]>) in
          .readObject();
      in.close();
    }
    else {
      HackystatConstants.hackystatLisedServers_list = new HashMap<String, String[]>();
    }
  }

  /**
   *
   * @param uri
   * @throws IOException
   */
  public static final synchronized void removeExternalHackystatLiSeDServer(String uri)
      throws IOException {
    if (HackystatConstants.hackystatLisedServers_list.containsKey(uri)) {
      HackystatConstants.hackystatLisedServers_list.remove(uri);
    }
    serializeChanges();
  }

}
