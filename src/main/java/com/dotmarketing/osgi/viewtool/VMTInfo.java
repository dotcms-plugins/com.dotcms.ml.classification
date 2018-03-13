package com.dotmarketing.osgi.viewtool;

import org.apache.velocity.tools.view.context.ViewContext;
import org.apache.velocity.tools.view.servlet.ServletToolInfo;

public class VMTInfo extends ServletToolInfo {

    

    @Override
    public String getKey () {
        return "VisitorMetricTool";
    }

    @Override
    public String getScope () {
        return ViewContext.RESPONSE;
    }

    @Override
    public String getClassname () {
        return VMTTool.class.getName();
    }

    @Override
    public Object getInstance ( Object initData ) {

        VMTTool viewTool = new VMTTool();
        viewTool.init( initData );

        setScope( ViewContext.RESPONSE );

        return viewTool;
    }

}