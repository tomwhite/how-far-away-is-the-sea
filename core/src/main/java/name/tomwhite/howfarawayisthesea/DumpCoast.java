package name.tomwhite.howfarawayisthesea;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.InputStreamInStream;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKBReader;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.simplify.DouglasPeuckerSimplifier;
import com.vividsolutions.jts.simplify.VWSimplifier;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

public class DumpCoast {

  public static void main(String[] args) throws Exception {
    if (args.length == 0) {
      System.err.println("Usage: " + DumpCoast.class.getSimpleName() +
          " <land shapefile> [meta]");
      System.exit(1);
    }

    File landShapeFile = new File(args[0]);

    boolean meta = false;
    if (args.length == 2 && "meta".equals(args[1])) {
      meta = true;
    }

    DumpCoast.dump(landShapeFile.toURL(), meta);
  }

  private static void dump(URL landGeometry, boolean meta) throws Exception {
    GeometryFactory geometryFactory =  new GeometryFactory();
    WKTReader reader = new WKTReader(geometryFactory);

    Point point = null;
    try {
      point = (Point) reader.read("POINT (-3.1333 51.85)"); // where I live
    } catch (ParseException e) {
      throw new IOException(e);
    }

    MultiPolygon landMultiPolygon = extractMultiPolygon(landGeometry);
    Geometry britain = null;
    for (int i = 0; i < landMultiPolygon.getNumGeometries(); i++) {
      Geometry geometryN = landMultiPolygon.getGeometryN(i);
      if (geometryN.contains(point)) {
        britain = geometryN;
      }
    }
    if (britain == null) {
      System.err.println("Could not find Britain!");
      System.exit(1);
    }

    //britain = DouglasPeuckerSimplifier.simplify(britain, 1.0); // vary from 0.1 to 4.0
    britain = VWSimplifier.simplify(britain, 1.0); // vary from 0.1 to 4.0

    if (meta) {
      System.out.println("#points\tmean side (s)\t\t1/s\t\t\tlength");
      for (int i = 0; i < 9; i++) {
        int stepFactor = (int) Math.pow(2, i);
        Coordinate[] coordinates = britain.getCoordinates();
        int points = 0;
        double length = 0;
        Coordinate prev = null;
        for (int j = 0; j < coordinates.length; j += stepFactor) {
          points++;
          if (prev != null) {
            double side = prev.distance(coordinates[j]);
            length += side;
          }
          prev = coordinates[j];
        }
        double averageSide = length / points;
        System.out.println(points + "\t" + averageSide + "\t" + 1/averageSide + "\t" +
            length);
      }
      return;
    }

    CoordinateReferenceSystem sourceCRS = DefaultGeographicCRS.WGS84;
    CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:3857"); // Spherical Mercator projection coordinate system

    MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);
    Geometry b2 = JTS.transform(britain, transform);

    System.out.println("var poly = " + emitJavascriptArray(b2.getCoordinates()) + ";");
    System.out.println("var bounds = " + emitJavascriptArray(b2.getEnvelope().getCoordinates()) + ";");
  }

  private static String emitJavascriptArray(Coordinate[] coords) {
    StringBuilder sb = new StringBuilder();
    sb.append("[\n");
    for (Coordinate coord : coords) {
      sb.append(coord.x).append(", ").append(coord.y).append(", ").append("\n");
    }
    sb.append("]");
    return sb.toString();
  }

  private static MultiPolygon extractMultiPolygon(URL shapeFile) throws IOException {
    try {
      WKBReader wkbReader = new WKBReader();
      return (MultiPolygon) wkbReader.read(new InputStreamInStream(shapeFile.openStream()));
    } catch (ParseException e) {
      throw new IOException(e);
    }
  }
}
