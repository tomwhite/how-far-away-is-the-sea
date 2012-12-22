package org.geotools.tutorial.quickstart;

import java.io.File;
import java.io.IOException;
import javax.servlet.http.*;

public class CoasterServlet extends HttpServlet {
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    String lat = req.getParameter("lat");
    String lng = req.getParameter("long");
    resp.setContentType("text/plain");
    if (lat == null || lat.length() == 0 || lng == null || lng.length() == 0) {
      resp.sendError(404, "Please specify lat/long parameters.");
    } else {
      Coaster coaster = new Coaster(new File("/Users/tom/Downloads/ne_10m_land/ne_10m_land.shp"), new File("/Users/tom/Downloads/ne_10m_ocean/ne_10m_ocean.shp"));
      DistanceToCoast d = coaster.distanceFromCoast(Double.parseDouble(lat), Double.parseDouble(lng));
      if (d.isOnLand()) {
        resp.getWriter().println("You are on land!");
      } else {
        resp.getWriter().println("You are at sea!");
      }
      // original lat/long
      // coastal lat/long
      // on land (true/false)
      // distance to coast
    }
  }
}
