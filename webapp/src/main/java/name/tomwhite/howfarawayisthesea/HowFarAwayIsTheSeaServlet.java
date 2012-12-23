package name.tomwhite.howfarawayisthesea;

import com.google.gson.Gson;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HowFarAwayIsTheSeaServlet extends HttpServlet {

  private HowFarAwayIsTheSea howfar;

  @Override
  public void init(ServletConfig config) throws ServletException {
    ServletContext context = config.getServletContext();
    try {
      URL landShapeFile = context.getResource("/WEB-INF/data/land.bin");
      URL oceanShapeFile = context.getResource("/WEB-INF/data/ocean.bin");
      howfar = new HowFarAwayIsTheSea(landShapeFile, oceanShapeFile);
    } catch (MalformedURLException e) {
      throw new ServletException(e);
    }
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
      DistanceToCoast d = howfar.distanceFromCoast(Double.parseDouble(lat), Double.parseDouble(lng));
      resp.getWriter().println(new Gson().toJson(d));
    }
  }
}
