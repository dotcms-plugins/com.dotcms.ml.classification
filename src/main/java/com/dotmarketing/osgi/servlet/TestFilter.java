
package com.dotmarketing.osgi.servlet;

import com.dotcms.visitor.business.VisitorAPIImpl;
import com.dotcms.visitor.domain.Visitor;

import com.dotmarketing.filters.Constants;
import com.dotmarketing.util.WebKeys;

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
import javax.servlet.http.HttpSession;

import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.Logger;
import org.pmw.tinylog.writers.ConsoleWriter;
import org.pmw.tinylog.writers.FileWriter;

public class TestFilter implements Filter {
    private final String name;

    public TestFilter(String name) {
        this.name = name;
    }

    public TestFilter() {
        this.name = this.getClass().getName();
    }

    public void init(FilterConfig config) throws ServletException {

       // FileWriter fw = new FileWriter("analyticlogger.log", true, true);
        ConsoleWriter fw = new ConsoleWriter();
        Configurator.currentConfig()
        .removeAllWriters()
        .addWriter(fw)
        .formatPattern("{message}")
        .writingThread(true)
        .activate();
        Logger.info("DotCMSAccessLogger started");

    }

    public void doFilter(final ServletRequest req, final ServletResponse res, final FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;

        final HttpServletResponse response = (HttpServletResponse)res;
        doLog("1." + response.getStatus()+"");
        try {
            chain.doFilter(req, res);
        }
        finally {
            
            
            new Thread(new Runnable() {
                public void run() {
                    for(int i=0;i<10;i++) {
                        
                        if(res.isCommitted()) {
                            doLog("Response commited");
                            break;
                        } else {
                            
                            doLog("Response still pending!");
                            doLog("2." + response.getStatus()+"");

                        }
                        
                        
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {

                        }
                        
                    }
                    
                    
                    
                    
                    
                    
                }
            }).start();
            
            
            
            
            StringWriter sw = new StringWriter();


            Optional<Visitor> visitor = new VisitorAPIImpl().getVisitor(request, false);

            if(!visitor.isPresent()) {
                return;
            }
            

            String uri = String.valueOf(request.getAttribute(javax.servlet.RequestDispatcher.FORWARD_REQUEST_URI));

            sw.append(visitor.get().toString())
            .append('\t')
            .append("uri:")
            .append(request.getRequestURI())
            .append('\t')
            .append("referer:")
            .append(request.getHeader("referer"))
            .append('\t')
            .append("host:")
            .append(request.getHeader("host"))
            .append('\t')
            .append("pageId:")
            .append(String.valueOf(request.getAttribute(Constants.CMS_FILTER_URI_OVERRIDE)))
            .append('\t')
            .append("contentId:")
            .append((String) request.getAttribute(WebKeys.WIKI_CONTENTLET));
            doLog(sw.toString());
            
            
        }
    }

    public void destroy() {
        doLog("Destroyed filter");
    }

    private void doLog(String message) {
        Logger.info(message);

    }
}
