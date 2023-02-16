package httpserver;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import javax.swing.JTextArea;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/** 
 * Encapsulate an HTTP Response.  Mostly just wrap an output stream and
 * provide some state.
 */
public class Response {
	  private OutputStream out;
	  private int statusCode;
	  private String statusMessage;
	  private Map<String, List<String>> headers = new HashMap<String, List<String>>();
	  private String body;
	  private byte[] file;

	  public Response(OutputStream out, JTextArea txtLogs) {
	    this.out = out;
	  }

	  public void setResponseCode(int statusCode, String statusMessage) {
	    this.statusCode = statusCode;
	    this.statusMessage = statusMessage;
	  }

	  public void addHeader(String headerName, String headerValue)  {
	    List<String> headerValues = this.headers.get(headerName);
	    if (headerValues == null) {
	      headerValues = new ArrayList<String>();
	      this.headers.put(headerName, headerValues);
	    }

	    headerValues.add(headerValue);
	  }

	  public void addBody(String body)  {
	    addHeader("Content-Length", Integer.toString(body.length()));
	    this.body = body;
	  }
	  
	  public void addCookie(Cookie cookie)  {
		  addHeader("Set-Cookie", cookie.toString());
	  }
	  
	  public void addFile(byte[] file) {
		  addHeader("Content-Length", Integer.toString(file.length));
		  this.file = file;
	  }

	  public void send() throws IOException {
	    out.write(("HTTP/1.1 " + statusCode + " " + statusMessage + "\r\n").getBytes());
	    for (String headerName : headers.keySet())  {
	      Iterator<String> headerValues = headers.get(headerName).iterator();
	      while (headerValues.hasNext())  {
	        out.write((headerName + ": " + headerValues.next() + "\r\n").getBytes());
	      }
	    }
	    out.write("\r\n".getBytes());
	    if (body != null) {
	      out.write(body.getBytes());
	    }
	    
	    if(file != null) {
	    	out.write(file);
	    }
	  }
	}