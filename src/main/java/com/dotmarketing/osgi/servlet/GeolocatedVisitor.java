package com.dotmarketing.osgi.servlet;



import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;

import com.dotcms.repackage.com.maxmind.geoip2.exception.GeoIp2Exception;
import com.dotcms.util.GeoIp2CityDbUtil;
import com.dotcms.visitor.domain.Visitor;
import com.dotmarketing.portlets.rules.conditionlet.Location;



public class GeolocatedVisitor implements Serializable {

  public Location getLocation() {
    return location;
  }
  public String getLatLong() {
    return location.toString();
  }


  public String getCountryCode() {
    return countryCode;
  }


  public String getCity() {
    return city;
  }
  static Field latitudeFld=Location.class.getDeclaredField("latitude");
  static Field longitudeFld=Location.class.getDeclaredField("longitude");

  private static final long serialVersionUID = 1L;
  private final String ipAddress;
  private static GeoIp2CityDbUtil geo;
  private final double latitude, longitude;
  private final Location location;
  private final String countryCode, city;
  private final static String unknown = "ukn";

  public GeolocatedVisitor(Visitor visitor) {

    this.ipAddress = visitor.getIpAddress().getHostAddress();
    this.city = city();
    this.countryCode = country();
    this.location = location();
    this.latitude = latitude();
    this.longitude = (Double) latitudeFld.get(this.longitude);
    
  }


  private GeoIp2CityDbUtil geo() {
    if ((geo == null)) {
      geo = GeoIp2CityDbUtil.getInstance();
    }
    return geo;
  }


  private String city() {
    try {
      return geo.getCityName(ipAddress);
    } catch (IOException | GeoIp2Exception e) {
      return unknown;
    }
  }
  private double latitude() {
    try {
      return (Double) latitudeFld.get(this.location);
    } catch (IOException | GeoIp2Exception e) {
      return unknown;
    }
  }
  private String country() {
    try {
      return geo.getCountryIsoCode(ipAddress);
    } catch (IOException | GeoIp2Exception e) {
      return unknown;
    }
  }


  private String latLong() {
    return latitude + "," + longitude;
  }
  private Location location() {
    try {
      
      return geo.getLocationByIp(ipAddress);
    } catch (IOException | GeoIp2Exception e) {
      return new Location(0d, 0d);
    }

  }


}
