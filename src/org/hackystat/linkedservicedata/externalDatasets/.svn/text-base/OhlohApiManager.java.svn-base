package org.hackystat.linkedservicedata.externalDatasets;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Handler of requests for the Ohloh API.
 *
 * @author Myriam Leggieri.
 *
 */
public class OhlohApiManager {
  private static final char[] hexChars = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a',
      'b', 'c', 'd', 'e', 'f' };
  public static final String OHLOH_API_KEY = "QvFada9hZWF7lyCDhLjg3w";

  // public static void main(String[] args) {
  // OhlohApiManager.getOhlohUserDataByEmail("myrpandemon@yahoo.it");
  // // OhlohApiManager.getOhlohUserDataByNickname(apiKey, "iammyr");
  // // OhlohApiManager.getOhlohProjectDataByKeyWords(apiKey, new String[]{"book","school"});
  //
  // }

  public OhlohApiManager() {
  }

  /**
   *
   * @param userEmail
   * @return
   */
  public static String[] getOhlohUserDataByEmail(String userEmail) {
    String ret[] = null;
    System.out.println("Initialising request.");

    // Calculate MD5 digest from the email address.
    String emailDigest = calculateDigest(userEmail);
    System.out.println(emailDigest);

    try {
      // Request XML file regarding a user.
      URL url = new URL("http://www.ohloh.net/accounts/" + emailDigest + ".xml?api_key="
          + OHLOH_API_KEY);
      System.out.println(url.toExternalForm());
      ret = getId(url, "account");
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return ret;
  }

  /**
   *
   * @param apiKey
   * @param nick
   * @return
   */
  public static String[] getOhlohUserDataByNickname(String apiKey, String nick) {
    String ret[] = null;
    System.out.println("Initialising request.");

    try {
      nick = java.net.URLEncoder.encode(nick, "UTF-8");
      // Request XML file regarding a user.
      URL url = new URL("http://www.ohloh.net/accounts.xml?api_key=" + apiKey + "&query=" + nick);
      ret = getId(url, "account");
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return ret;
  }

  /**
   *
   * @param keys
   * @return
   */
  public static String[] getOhlohProjectDataByKeyWords(String[] keys) {
    String ret[] = null;
    System.out.println("Initialising request.");
    String query = "", plus = "+";
    for (int j = 0; j < keys.length; j++) {
      if (!keys[j].equals(""))
        query += keys[j].replaceAll("\"", "");
      if ((j + 1) != keys.length)
        query += plus;
    }
    try {
      query = java.net.URLEncoder.encode(query, "UTF-8");
      // Request XML file regarding a user.
      URL url = new URL("http://www.ohloh.net/projects.xml?api_key=" + OHLOH_API_KEY + "&query="
          + query);
      System.out.println(url.toExternalForm());
      ret = getId(url, "project");
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return ret;
  }

  /**
   *
   * @param url
   * @param entity
   * @return
   * @throws IOException
   * @throws ParserConfigurationException
   * @throws SAXException
   */
  private static String[] getId(URL url, String entity) throws IOException,
      ParserConfigurationException, SAXException {
    String[] ret = null;
    URLConnection con = url.openConnection();
    if (con == null) {
      System.err.println("Connection not available: failed to contact Ohloh.");
      return null;
    }
    // Check for status OK.
    if (con.getHeaderField("Status") != null && con.getHeaderField("Status").startsWith("200")) {
      System.out.println("Request succeeded.");
    }
    else {
      System.out.println("Request failed.");
      return ret;
    }
    System.out.println("Looking up ID..");

    // Create a document from the URL's input stream, and parse.
    DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    Document doc = builder.parse(con.getInputStream());

    NodeList responseNodes = doc.getElementsByTagName("response");
    for (int i = 0; i < responseNodes.getLength(); i++) {
      Element element = (Element) responseNodes.item(i);

      // First check for the status code inside the XML file. It is
      // most likely, though, that if the request would have failed,
      // it is already returned earlier.
      NodeList statusList = element.getElementsByTagName("status");
      if (statusList.getLength() == 1) {
        Node statusNode = statusList.item(0);

        // Check if the text inidicates that the request was
        // successful.
        if (!statusNode.getTextContent().equals("success")) {
          System.out.println("Failed. " + statusNode.getTextContent());
          return ret;
        }
      }

      Element resultElement = (Element) element.getElementsByTagName("result").item(0);
      // We assume we only have one account result here.
      NodeList nl = resultElement.getElementsByTagName(entity), idnl = null;
      Element accountElement = null;
      String id = null;
      ret = new String[nl.getLength()];
      for (int j = 0; j < nl.getLength(); j++) {
        accountElement = (Element) nl.item(j);
        if (accountElement != null) {
          // Lookup id.
          idnl = accountElement.getElementsByTagName("id");
          if (idnl != null) {
            id = accountElement.getElementsByTagName("id").item(0).getTextContent();
            System.out.println("Located the id: " + id);
            ret[j] = id;
          }
        }
      }
    }
    return ret;
  }

  /**
   *
   * @param email
   * @return
   */
  private static String calculateDigest(String email) {
    return hexStringFromBytes(calculateHash(email.getBytes()));
  }

  /**
   *
   * @param dataToHash
   * @return
   */
  private static byte[] calculateHash(byte[] dataToHash) {
    try {
      // Calculate MD5 digest.
      MessageDigest md = MessageDigest.getInstance("MD5");
      md.update(dataToHash, 0, dataToHash.length);
      return md.digest();
    }
    catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   *
   * @param b
   * @return
   */
  private static String hexStringFromBytes(byte[] b) {
    // Conversion from bytes to String.
    String hex = "";

    int msb;

    int lsb = 0;
    int i;

    for (i = 0; i < b.length; i++) {
      msb = ((int) b[i] & 0x000000FF) / 16;

      lsb = ((int) b[i] & 0x000000FF) % 16;
      hex = hex + hexChars[msb] + hexChars[lsb];
    }
    return hex;
  }

}
