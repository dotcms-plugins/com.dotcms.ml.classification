package com.dotmarketing.osgi.viewtool;

import com.dotmarketing.business.web.WebAPILocator;
import com.dotmarketing.osgi.util.VisitorLogger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.tools.view.context.ViewContext;
import org.apache.velocity.tools.view.tools.ViewTool;

public class VMTTool implements ViewTool {
    HttpServletRequest request;
    HttpServletResponse response;

    static VisitorLogger logger=new VisitorLogger();
    @Override
    public void init(Object initData) {


        this.request = ((ViewContext) initData).getRequest();
        this.response = ((ViewContext) initData).getResponse();
        
        try {
            if(!WebAPILocator.getUserWebAPI().isLoggedToBackend(request)) {
                logger.log(request, response);
            }
        } catch (Exception e) {
            System.err.println("VMTTool:" + e);
            System.err.println("VMTTool:" + e.getStackTrace()[0]);
            System.err.println("VMTTool:" + e.getStackTrace()[1]);
            System.err.println("VMTTool:" + e.getStackTrace()[2]);
        }
    }



}
