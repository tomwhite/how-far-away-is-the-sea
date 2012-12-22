package org.geotools.tutorial.quickstart;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.*;

public class CoasterServlet extends HttpServlet {

  private ServletConfig config;

  @Override
  public void init(ServletConfig config) {
    this.config = config;
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    String lat = req.getParameter("lat");
    String lng = req.getParameter("long");
    resp.setContentType("text/plain");
    if (lat == null || lat.length() == 0 || lng == null || lng.length() == 0) {
      resp.sendError(404, "Please specify lat/long parameters.");
    } else {
      ServletContext context = config.getServletContext();
      URL landShapeFile = context.getResource("/WEB-INF/data/land.bin");
      URL oceanShapeFile = context.getResource("/WEB-INF/data/ocean.bin");

      CoasterJts coaster = new CoasterJts(landShapeFile, oceanShapeFile);
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
