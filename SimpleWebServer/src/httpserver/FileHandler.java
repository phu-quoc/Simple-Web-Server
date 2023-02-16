package httpserver;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javax.swing.JTextArea;

public class FileHandler implements Handler {
	@Override
	public void handle(Request request, Response response, JTextArea txtLogs, String path) throws IOException {
		try {
		      FileInputStream file = new FileInputStream(path+request.getPath().substring(1)+".html");
		      response.setResponseCode(200, "OK");
		      response.addHeader("Content-Type", "text/html");
		      response.addHeader("Connection", request.getHeader("Connection"));
		      StringBuffer buf = new StringBuffer();
		      // TODO this is slow
		      int c;
		      while ((c = file.read()) != -1) {
		        buf.append((char) c);
		      }
		      txtLogs.append(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))+" [Java] Response: HTTP/1.1 200 OK "+request.getPath().substring(1)+"\n");
		      response.addBody(buf.toString());
		      
		 } catch (FileNotFoundException e) {
			 txtLogs.append(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))+" [Java] Response: HTTP/1.1 404 Not Found "+request.getPath().substring(1)+"\n");
		      response.setResponseCode(404, "Not Found");
		      
		 }
	}
	
	private void log(String msg)  {
	    System.out.println(msg);
	}
}