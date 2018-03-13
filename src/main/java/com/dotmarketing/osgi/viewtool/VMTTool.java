package com.dotmarketing.osgi.viewtool;

import com.dotmarketing.osgi.util.VisitorLogger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.tools.view.context.ViewContext;
import org.apache.velocity.tools.view.tools.ViewTool;

import com.fasterxml.jackson.core.JsonProcessingException;

public class VMTTool implements ViewTool {
    HttpServletRequest request;
    HttpServletResponse response;

    static VisitorLogger logger=new VisitorLogger();
    @Override
    public void init(Object initData) {


        this.request = ((ViewContext) initData).getRequest();
        this.response = ((ViewContext) initData).getResponse();
        
        try {
            logger.log(request, response);
        } catch (JsonProcessingException e) {
            System.err.println("VMTTool:" + e);
        }
    }



}
