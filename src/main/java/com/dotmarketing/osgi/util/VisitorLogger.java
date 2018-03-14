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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pmw.tinylog.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.Splitter;

public class VisitorLogger {
    
    static GeoIp2CityDbUtil geo = GeoIp2CityDbUtil.getInstance();
    static ObjectMapper mapper;
    private ObjectMapper mapper() {
        if(mapper==null) {
            ObjectMapper newMapper = new ObjectMapper();
            newMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            mapper=newMapper;
        }
        return mapper;
    }



    public void log(final HttpServletRequest request, final HttpServletResponse response) throws JsonProcessingException {


        if (!shouldLog(request, response)) {
            return;
        }

        final Map<String, Object> map = new HashMap<>();
        // file or page
        final Identifier asset = (request.getAttribute("idInode") != null) ? (Identifier) request.getAttribute("idInode")
                : (Identifier) request.getAttribute(Constants.CMS_FILTER_IDENTITY);

        final Optional<String> content = Optional.ofNullable((String) request.getAttribute(WebKeys.WIKI_CONTENTLET));

        final Language lang = WebAPILocator.getLanguageWebAPI().getLanguage(request);
        final Visitor visitor = new VisitorAPIImpl().getVisitor(request).get();
        final GeolocationProvider geo = new GeoIp2Geolocation(visitor);
        final IPersona persona = visitor.getPersona();
        final String dmid = (visitor.getDmid()==null) ? "ukn" : visitor.getDmid().toString();
        final String device = visitor.getDevice();
        final String agent = request.getHeader("user-agent");
        final List<AccruedTag> tags = visitor.getTags();
        final Map<String, String> params = (request.getQueryString()!=null) ?  Splitter.on('&').trimResults().withKeyValueSeparator("=").split(request.getQueryString()) : Collections.emptyMap();




        map.put("ts", System.currentTimeMillis());
        map.put("ip", request.getRemoteHost());
        map.put("request", request.getRequestURI());
        map.put("query", params);
        map.put("referer", request.getHeader("referer"));
        map.put("host", request.getHeader("host"));
        map.put("assetId", (asset != null) ? asset.getId() : "ukn");
        map.put("contentId", content.orElse("ukn"));
        map.put("device", device);
        map.put("agent", agent);
        map.put("persona", (persona != null) ? persona.getKeyTag() : "ukn");
        map.put("city", geo.getCity());
        map.put("country", geo.getCountryCode());
        map.put("lang", lang.toString());
        map.put("dmid", dmid);
        map.put("latLong", geo.getLatLong());
        map.put("tags", tags);
        map.put("params", params);
        map.put("pagesViewed", visitor.getNumberPagesViewed());


        doLog(mapper().writeValueAsString(map));
    }

    private void doLog(Object message) {
        Logger.info(message);
    }


    private boolean shouldLog(HttpServletRequest request, HttpServletResponse response) {
        return 500 != response.getStatus() && new VisitorAPIImpl().getVisitor(request, false).isPresent();
    }


}
