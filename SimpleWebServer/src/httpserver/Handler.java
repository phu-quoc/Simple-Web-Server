package httpserver;

import java.io.IOException;

import javax.swing.JTextArea;

/**
 * Handlers must be thread safe.
 */
public interface Handler  {
  public void handle(Request request, Response response, JTextArea txtLogs, String path) throws IOException;
}