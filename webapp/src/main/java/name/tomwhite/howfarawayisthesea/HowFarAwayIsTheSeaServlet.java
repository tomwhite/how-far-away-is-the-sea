package name.tomwhite.howfarawayisthesea;

import com.google.gson.Gson;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
    String lng = req.getParameter("lng");
    resp.setContentType("text/plain");
    if (lat == null || lat.length() == 0 || lng == null || lng.length() == 0) {
      resp.sendError(404, "Please specify lat/long parameters.");
    } else {
      DistanceToCoast d = howfar.distanceFromCoast(Double.parseDouble(lat), Double.parseDouble(lng));
      resp.getWriter().println(new Gson().toJson(d));
    }
  }
}
