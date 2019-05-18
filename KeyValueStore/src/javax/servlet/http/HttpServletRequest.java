package javax.servlet.http;

import java.security.Principal;
import java.util.Enumeration;

import javax.servlet.ServletRequest;

public abstract interface HttpServletRequest
  extends ServletRequest
{
  public static final String BASIC_AUTH = "BASIC";
  public static final String FORM_AUTH = "FORM";
  public static final String CLIENT_CERT_AUTH = "CLIENT_CERT";
  public static final String DIGEST_AUTH = "DIGEST";
  
  public abstract String getAuthType();
  
  public abstract long getDateHeader(String paramString);
  
  public abstract String getHeader(String paramString);
  
  public abstract Enumeration<String> getHeaders(String paramString);
  
  public abstract Enumeration<String> getHeaderNames();
  
  public abstract int getIntHeader(String paramString);
  
  public abstract String getMethod();
  
  public abstract String getPathInfo();
  
  public abstract String getPathTranslated();
  
  public abstract String getContextPath();
  
  public abstract String getQueryString();
  
  public abstract String getRemoteUser();
  
  public abstract boolean isUserInRole(String paramString);
  
  public abstract Principal getUserPrincipal();
  
  public abstract String getRequestedSessionId();
  
  public abstract String getRequestURI();
  
  public abstract StringBuffer getRequestURL();
  
  public abstract String getServletPath();
  
}

