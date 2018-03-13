
package com.dotmarketing.osgi.servlet;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Optional;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pmw.tinylog.Logger;

import com.dotcms.util.GeoIp2CityDbUtil;
import com.dotcms.visitor.business.VisitorAPIImpl;
import com.dotcms.visitor.domain.Visitor;
import com.dotmarketing.beans.Identifier;
import com.dotmarketing.business.web.WebAPILocator;
import com.dotmarketing.filters.Constants;
import com.dotmarketing.portlets.languagesmanager.model.Language;
import com.dotmarketing.util.WebKeys;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class VisitorFilter implements Filter {
  GeoIp2CityDbUtil geo = GeoIp2CityDbUtil.getInstance();

  private ObjectMapper  mapper() {
    
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    return mapper;
  }
  
  public void init(FilterConfig config) throws ServletException {
    
    try {
      System.out.println("visitor logger started:" + mapper().writeValueAsString(Logger.getConfiguration()));
    } catch (JsonProcessingException e) {

      e.printStackTrace();
    }

  }

  public void doFilter(final ServletRequest req, final ServletResponse res, final FilterChain chain) throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) req;

    final HttpServletResponse response = (HttpServletResponse) res;

    try {
      chain.doFilter(req, res);
    } finally {

      if (!shouldLog(request, response)) {
        return;
      }
      StringWriter sw = new StringWriter();

      // file or page 
      final Identifier assetIdentifier = (request.getAttribute("idInode") != null) ? (Identifier) request.getAttribute("idInode")
          : (Identifier) request.getAttribute(Constants.CMS_FILTER_IDENTITY);

      final Optional<String> contentIdentifier = Optional.ofNullable((String) request.getAttribute(WebKeys.WIKI_CONTENTLET));

      final Language lang = WebAPILocator.getLanguageWebAPI().getLanguage(request);
      Visitor visitor = new VisitorAPIImpl().getVisitor(request).get();

      
      
      
      String uri = String.valueOf(request.getAttribute(javax.servlet.RequestDispatcher.FORWARD_REQUEST_URI));

      sw.append(visitor.toString()).append('\t').append("uri:").append(request.getRequestURI()).append('\t').append("referer:")
          .append(request.getHeader("referer")).append('\t').append("host:").append(request.getHeader("host")).append('\t')
          .append("pageId:").append(String.valueOf(request.getAttribute(Constants.CMS_FILTER_URI_OVERRIDE))).append('\t')
          .append("contentId:").append((String) request.getAttribute(WebKeys.WIKI_CONTENTLET));
      doLog(sw.toString());


    }
  }

  public void destroy() {
    doLog("Destroyed filter");
  }

  private void doLog(String message) {
    Logger.info(message);
  }


  private boolean shouldLog(HttpServletRequest request, HttpServletResponse response) {

    return 500 != response.getStatus() && new VisitorAPIImpl().getVisitor(request, false).isPresent();


  }



}
