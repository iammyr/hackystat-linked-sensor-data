package org.hackystat.linkedservicedata.client;

import org.restlet.data.Status;

/**
 * Kind of Exception thrown by the LinkedServiceData client class.
 *
 * @author Myriam Leggieri.
 *
 */
public class LinkedServiceDataClientException extends Exception {

  /**
	 *
	 */
  private static final long serialVersionUID = 8314578353515166003L;

  /**
   * Thrown when an unsuccessful status code is returned from the Server.
   *
   * @param status The Status instance indicating the problem.
   */
  public LinkedServiceDataClientException(Status status) {
    super(status.getCode() + ": " + status.getDescription());
  }

  /**
   * Thrown when an unsuccessful status code is returned from the Server.
   *
   * @param status The status instance indicating the problem.
   * @param error The previous error.
   */
  public LinkedServiceDataClientException(Status status, Throwable error) {
    super(status.getCode() + ": " + status.getDescription(), error);
  }

  /**
   * Thrown when some problem occurs with Client not involving the server.
   *
   * @param description The problem description.
   * @param error The previous error.
   */
  public LinkedServiceDataClientException(String description, Throwable error) {
    super(description, error);
  }

  /**
   * Thrown when some problem occurs with Client not involving the server.
   *
   * @param description The problem description.
   */
  public LinkedServiceDataClientException(String description) {
    super(description);
  }
}
