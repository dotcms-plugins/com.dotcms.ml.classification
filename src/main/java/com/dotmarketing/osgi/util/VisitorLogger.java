package com.dotmarketing.osgi.util;

import com.dotcms.util.GeoIp2CityDbUtil;
import com.dotcms.visitor.business.VisitorAPIImpl;
import com.dotcms.visitor.domain.Visitor;
import com.dotcms.visitor.domain.Visitor.AccruedTag;

import com.dotmarketing.beans.Identifier;
import com.dotmarketing.business.web.WebAPILocator;
import com.dotmarketing.filters.Constants;
import com.dotmarketing.portlets.languagesmanager.model.Language;
import com.dotmarketing.portlets.personas.model.IPersona;
import com.dotmarketing.util.WebKeys;

import java.io.StringWriter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pmw.tinylog.Logger;

public class VisitorLogger {
    GeoIp2CityDbUtil geo = GeoIp2CityDbUtil.getInstance();


    public void log(final HttpServletRequest request, final HttpServletResponse response)  {


        if (!shouldLog(request, response)) {
            return;
        }
        StringWriter sw = new StringWriter();

        // file or page
        final Identifier asset = (request.getAttribute("idInode") != null) ? (Identifier) request.getAttribute("idInode")
                : (Identifier) request.getAttribute(Constants.CMS_FILTER_IDENTITY);

        final Optional<String> content = Optional.ofNullable((String) request.getAttribute(WebKeys.WIKI_CONTENTLET));

        final Language lang = WebAPILocator.getLanguageWebAPI().getLanguage(request);
        Visitor visitor = new VisitorAPIImpl().getVisitor(request).get();
        GeolocationProvider geo = new GeoIp2Geolocation(visitor);
        IPersona persona = visitor.getPersona();
        UUID dmid = visitor.getDmid();
        String device = visitor.getDevice();
        List<AccruedTag> tags = visitor.getTags();
        String agent = request.getHeader("user-agent");

        sw.append("ts:")
            .append(String.valueOf(System.currentTimeMillis()))

            .append('\t')
            .append("request:")
            .append(request.getRequestURI())

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
            .append("assetId:")
            .append((asset != null) ? asset.getId() : "ukn")

            .append('\t')
            .append("contentId:")
            .append(content.orElse("ukn"))

            .append('\t')
            .append("device:")
            .append(device)

            .append('\t')
            .append("agent:")
            .append(agent)

            .append('\t')
            .append("persona:")
            .append((persona != null) ? persona.getKeyTag() : "ukn")

            .append('\t')
            .append("city:")
            .append(geo.getCity())

            .append('\t')
            .append("lang:")
            .append(lang.toString())

            .append('\t')
            .append("dmid:")
            .append(dmid.toString())

            .append('\t')
            .append("latLong:")
            .append(geo.getLatLong())

            .append('\t')
            .append("tags:")
            .append(tags.toString());
        doLog(sw);



    }

    public void destroy() {
        System.out.println("visitor logger stopped");
    }

    private void doLog(Object message) {
        Logger.info(message);
    }


    private boolean shouldLog(HttpServletRequest request, HttpServletResponse response) {

        return 500 != response.getStatus() && new VisitorAPIImpl().getVisitor(request, false).isPresent();


    }



}
