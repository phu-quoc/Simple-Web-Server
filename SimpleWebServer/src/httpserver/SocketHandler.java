package httpserver;

import java.io.IOException;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import java.util.HashMap;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;

class SocketHandler implements Runnable  {
  private Socket socket;
  private Handler defaultHandler;
  private Map<String, Map<String, Handler>> handlers;
  private JTextArea txtLogs;
  private JTextArea txtClients;
  private JTextField tfQuantity;
  private String path;
  
  public SocketHandler(Socket socket, 
                       Map<String, Map<String, Handler>> handlers, JTextArea txtLogs, JTextArea txtClients, JTextField tfQuantity, String path)  {
    this.socket = socket;
    this.handlers = handlers;
    this.txtLogs = txtLogs;
    this.txtClients = txtClients;
    this.tfQuantity = tfQuantity;
    this.path = path;
  }

  /**
   * Simple responses like errors.  Normal reponses come from handlers.
   */
  private void respond(int statusCode, String msg, OutputStream out) throws IOException  {
    String responseLine = "HTTP/1.1 " + statusCode + " " + msg + "\r\n\r\n";
    log("Response: HTTP/1.1 " + statusCode + " " + msg);
    out.write(responseLine.getBytes());
  }

  public void run()  {
    BufferedReader in = null;
    OutputStream out = null;

    try  {
    	socket.setSoTimeout(1000);
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      out = socket.getOutputStream();
      boolean done = false;
      
      while(!done) {
    	Request request = new Request(in, txtLogs);
    	try {
    		if (!request.parse())  {
    			respond(500, "Unable to parse request", out);
    	        return;
    	    } 
		} catch (SocketTimeoutException e) {
			break;
		}
    	
    	if ("close".equalsIgnoreCase(request.getHeader("connection")))  {
            done = true;
        }
    	
    	boolean foundHandler = false;
        Response response = new Response(out, txtLogs);
        Map<String, Handler> methodHandlers = handlers.get(request.getMethod());
        if (methodHandlers == null)  {
          respond(405, "Method not supported", out);
          return;
        }

        for (String handlerPath : methodHandlers.keySet())  {
          if (handlerPath.equals(request.getPath()))  {
            methodHandlers.get(request.getPath()).handle(request, response, txtLogs, path);
            response.send();
            foundHandler = true;
            break;
          }
        }
        
        if (!foundHandler)  {
          if (methodHandlers.get("/*") != null)  {
            methodHandlers.get("/*").handle(request, response, txtLogs, path);
            response.send();
          } else  {
            respond(404, "Not Found", out);
          }
        }
        
        
      }
      

      // TODO most specific handler
      
    } catch (IOException e)  {
      try  {
//        e.printStackTrace();
        if (out != null)  {
          respond(500, e.toString(), out);
        }
      } catch (IOException e2)  {
//        e2.printStackTrace();
        // We tried
      }
    } finally  {
      try  {
        if (out != null)  {
          out.close();
        }
        if (in != null)  {
          in.close();
        }
        socket.close();
      } catch (IOException e)  {
//        e.printStackTrace();
      }
    }
  }

  private void log(String message)  {
	  txtLogs.append(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))+" [Java] "+message+"\n");
  }
}