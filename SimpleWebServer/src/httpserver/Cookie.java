package httpserver;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Cookie     {
	  private String name;
	  private String value;
	  private Date expires;
	  private Integer maxAge;
	  private String domain;
	  private String path;
	  private boolean secure;
	  private boolean httpOnly;
	  private String sameSite;

	  public Cookie(String name,
	                String value,
	                Date expires,
	                Integer maxAge,
	                String domain,
	                String path,
	                boolean secure,
	                boolean httpOnly,
	                String sameSite)        {
	    this.name = name;
	    this.value = value;
	    this.expires = expires;
	    this.maxAge = maxAge;
	    this.domain = domain;
	    this.path = path;
	    this.secure = secure;
	    this.httpOnly = httpOnly;
	    this.sameSite = sameSite;
	  }

	  public String toString()        {
	    StringBuffer s = new StringBuffer();

	    s.append(name + "=" + value);

	    if (expires != null)    {
	      SimpleDateFormat fmt = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss");
	      s.append("; Expires=" + fmt.format(expires) + " GMT");
	    }

	    if (maxAge != null)     {
	      s.append("; Max-Age=" + maxAge);
	    }

	    if (domain != null)     {
	      s.append("; Domain=" + domain);
	    }

	    if (path != null)       {
	      s.append("; Path=" + path);
	    }

	    if (secure)     {
	      s.append("; Secure");
	    }

	    if (httpOnly)   {
	      s.append("; HttpOnly");
	    }

	    if (sameSite != null)   {
	      s.append("; SameSite=" + sameSite);
	    }

	    return s.toString();
	  }
	}