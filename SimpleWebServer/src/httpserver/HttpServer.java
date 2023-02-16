package httpserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Provider;
import java.security.Security;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.SSLServerSocketFactory;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class HttpServer implements Runnable {
	  private int port;
	  private int count = 0;
	  private long time = 0;
	  private ServerSocket socket;
	  private Socket client;
	  private Handler defaultHandler = null;
	  private boolean stop = false;
	  private String message = "";
	  private JTextArea txtLogs;
	  private JTextArea txtClients;
	  private JTextField tfQuantity;
	  private String path;
	  // Two level map: first level is HTTP Method (GET, POST, OPTION, etc.), second level is the
	  // request paths.
	  private Map<String, Map<String, Handler>> handlers = new HashMap<String, Map<String, Handler>>();

	  // TODO SSL support
	  public HttpServer(int port, JTextArea txtLogs, JTextField tfQuantity, JTextArea txtClients, String path) throws IOException  {
	    this.port = port;
	    this.txtLogs = txtLogs;
	    this.tfQuantity = tfQuantity;
	    this.txtClients = txtClients;
	    this.path = path;
	  }

	  /**
	   * @param path if this is the special string "/*", this is the default handler if
	   *   no other handler matches.
	   */
	  public void addHandler(String method, String path, Handler handler)  {
	    Map<String, Handler> methodHandlers = handlers.get(method);
	    if (methodHandlers == null)  {
	      methodHandlers = new HashMap<String, Handler>();
	      handlers.put(method, methodHandlers);
	    }
	    methodHandlers.put(path, handler);
	  }

	  public void start() throws IOException  {
		System.setProperty("javax.net.ssl.keyStore", path+"httpserver.jks");
		System.setProperty("javax.net.ssl.keyStorePassword", "password");
		SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
		socket = factory.createServerSocket(port);
	    while ((client = socket.accept()) != null)  {
	    	long currentTime = System.currentTimeMillis();
	    	if(currentTime-time > 1000) {
	    		count = 0;
	    		txtClients.setText("");
	    	}
	    	tfQuantity.setText(++count+"");
	    	txtClients.append(count+". Received connection from " + client.getRemoteSocketAddress().toString()+"\n");
    		time = currentTime;
	      SocketHandler handler = new SocketHandler(client, handlers, txtLogs, txtClients, tfQuantity, path);
	      Thread t = new Thread(handler);
	      t.start();
	    }
	  }

	  public void initialize()  {
	    addHandler("GET", "/picture", new FileHandler());
	    addHandler("GET", "/profile", new FileHandler());
	    addHandler("GET", "/*", new Handler() {

			@Override
			public void handle(Request request, Response response, JTextArea txtLogs, String path) throws IOException {
				try {
				      FileInputStream file = new FileInputStream(path+request.getPath().substring(1));
				      response.setResponseCode(200, "OK");
				      response.addHeader("Connection", request.getHeader("Connection"));
				      response.addFile(file.readAllBytes());
				      txtLogs.append(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))+" [Java] Response: HTTP/1.1 200 OK "+request.getPath().substring(1)+"\n");
				 } catch (FileNotFoundException e) {
					 txtLogs.append(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))+" [Java] Response: HTTP/1.1 404 Not Found "+request.getPath().substring(1)+"\n");
				      response.setResponseCode(404, "Not Found");
				 }
			}
	    	
	    });
	    addHandler("POST", "/profile", new Handler() {
	    	  public void handle(Request request, Response response, JTextArea txtLogs, String path) throws IOException {
	    	     StringBuffer buf = new StringBuffer();
	    	     InputStream in = request.getBody();
	    	     int c;
	    	     while ((c = in.read()) != -1) {
	    	       buf.append((char) c);
	    	     }
	    	     String[] components = buf.toString().split("&");
	    	     Map<String, String> urlParameters = new HashMap<String, String>();
	    	     for (String component : components) {
	    	       String[] pieces = component.split("=");
	    	       urlParameters.put(pieces[0], pieces[1]);
	    	     }
	    	     String name = ""+urlParameters.get("username");

	    	     FileInputStream file = new FileInputStream(path+request.getPath().substring(1)+".html");
	    	     response.addCookie(new Cookie("username", name, null, null, "localhost", "/", true, true, null));
			      response.setResponseCode(200, "OK");
			      response.addHeader("Content-Type", "text/html");
			      response.addHeader("Connection", request.getHeader("Connection"));
			      StringBuffer buff = new StringBuffer();
			      // TODO this is slow
			      int ch;
			      char t1, t2;
			      while ((ch = file.read()) != -1) {
			    	  if((char) ch == '$') {
			    		  t1 = (char) file.read();
			    		  if(t1 == '{') {
			    			  t2 = (char) file.read();
			    			  if(t2 == '}') {
			    				  buff.append(name);
			    			  } else {
			    				  buff.append((char) ch); 
				    			  buff.append(t1);
				    			  buff.append(t2);
			    			  }
			    		  } else {
			    			  buff.append((char) ch); 
			    			  buff.append(t1);
			    		  }
			    	  } else {
			    		  buff.append((char) ch); 
			    	  }
			      }
			      response.addBody(buff.toString());
			      txtLogs.append(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))+" [Java] Response: HTTP/1.1 200 OK "+request.getPath().substring(1)+"\n");
	    	  }
	    	});
	    try {
			start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//
			e.printStackTrace();
		}
	  }
	  
	  private void log(String msg)  {
		    message = msg;
		}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(!stop) {
			initialize();
		}
		
		
		
	}
	
	public void stopThread() {
		stop = true;
		try {
			socket.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			//
			e1.printStackTrace();
		}
	}
	}