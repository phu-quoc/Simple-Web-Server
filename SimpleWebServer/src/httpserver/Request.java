package httpserver;

import java.util.Map;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.StringTokenizer;

import javax.swing.JTextArea;

public class Request  {
  private String method;
  private String path;
  private String fullUrl;
  private Map<String, String> headers = new HashMap<String, String>();
  private Map<String, String> queryParameters = new HashMap<String, String>();
  private Map<String, String> cookies = new HashMap<String, String>();
  private BufferedReader in; 
  private JTextArea txtLogs;

  public Request(BufferedReader in, JTextArea txtLogs)  {
    this.in = in;
    this.txtLogs = txtLogs;
  }
  
  public InputStream getBody() throws IOException {
	    return new HttpInputStream(in, headers);
	  }

  public String getMethod()  {
    return method;
  }

  public String getPath()  {
    return path;
  }

  public String getFullUrl()  {
    return fullUrl;
  }

  // TODO support mutli-value headers
  public String getHeader(String headerName)  {
    return headers.get(headerName);
  }

  public String getParameter(String paramName)  {
    return queryParameters.get(paramName);
  }

  private void parseQueryParameters(String queryString)  {
    for (String parameter : queryString.split("&"))  {
      int separator = parameter.indexOf('=');
      if (separator > -1)  {
        queryParameters.put(parameter.substring(0, separator),
          parameter.substring(separator + 1));
      } else  {
        queryParameters.put(parameter, null);
      }
    }
  }
  
  private void parseCookies(String cookieString)  {
	  String[] cookiePairs = cookieString.split("; ");
	  for (int i = 0; i < cookiePairs.length; i++)  {
	      String[] cookieValue = cookiePairs[i].split("=");
	      cookies.put(cookieValue[0], cookieValue[1]);
	  }
  }

  public String getCookie(String cookieName)  {
	  return cookies.get(cookieName);
  }

  public boolean parse() throws IOException  {
	String initialLine = "";
	if((initialLine = in.readLine()) == null) return false;
//    initialLine = in.readLine();
    log(initialLine);
    StringTokenizer tok = new StringTokenizer(initialLine);
    String[] components = new String[3];
    for (int i = 0; i < components.length; i++)  {
      // TODO support HTTP/1.0?
      if (tok.hasMoreTokens())  {
        components[i] = tok.nextToken();
      } else  {
        return false;
      }
    }

    method = components[0];
    fullUrl = components[1];

    // Consume headers
    while (true)  {
      String headerLine = in.readLine();
//      log(headerLine);
      if (headerLine.length() == 0)  {
        break;
      }

      int separator = headerLine.indexOf(":");
      if (separator == -1)  {
        return false;
      }
      
      String name = headerLine.substring(0, separator);
      String value = headerLine.substring(separator + 2);
      headers.put(name, value);

      if ("Cookie".equals(name))  {
        parseCookies(value);
      }
    }

    // TODO should look for host header, Connection: Keep-Alive header, 
    // Content-Transfer-Encoding: chunked

    if (components[1].indexOf("?") == -1)  {
      path = components[1];
    } else  {
      path = components[1].substring(0, components[1].indexOf("?"));
      parseQueryParameters(components[1].substring(
        components[1].indexOf("?") + 1));
    }

    if ("/".equals(path))  {
      path = "/index";
    }

    return true;
  }

  private void log(String message)  {
    txtLogs.append(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))+" [Java] Request: "+message+"\n");
  }

  public String toString()  {
    return method  + " " + path + " " + headers.toString();
  }
}

class HttpInputStream extends InputStream  {
	  private Reader source;
	  private int bytesRemaining;
	  private boolean chunked = false;

	  public HttpInputStream(Reader source, Map<String, String> headers) throws IOException  {
	    this.source = source;

	    String declaredContentLength = headers.get("Content-Length");
	    if (declaredContentLength != null)  {
	      try  {
	        bytesRemaining = Integer.parseInt(declaredContentLength);
	      } catch (NumberFormatException e)  {
	        throw new IOException("Malformed or missing Content-Length header");
	      }
	    }  else if ("chunked".equals(headers.get("Transfer-Encoding")))  {
	      chunked = true;
	      bytesRemaining = parseChunkSize();
	    }
	  }

	  private int parseChunkSize() throws IOException {
	    int b;
	    int chunkSize = 0;

	    while ((b = source.read()) != '\r') {
	      chunkSize = (chunkSize << 4) |
	        ((b > '9') ?
	          (b > 'F') ?
	            (b - 'a' + 10) :
	            (b - 'A' + 10) :
	          (b - '0'));
	    }
	    // Consume the trailing '\n'
	    if (source.read() != '\n')  {
	      throw new IOException("Malformed chunked encoding");
	    }

	    return chunkSize;
	  }

	  public int read() throws IOException  {
	    if (bytesRemaining == 0)  {
	      if (!chunked) {
	        return -1;
	      } else  {
	        // Read next chunk size; return -1 if 0 indicating end of stream
	        // Read and discard extraneous \r\n
	        if (source.read() != '\r')  {
	          throw new IOException("Malformed chunked encoding");
	        }
	        if (source.read() != '\n')  {
	          throw new IOException("Malformed chunked encoding");
	        }
	        bytesRemaining = parseChunkSize();

	        if (bytesRemaining == 0)  {
	          return -1;
	        }
	      } 
	    }

	    bytesRemaining -= 1;
	    return source.read();
	  }
	}