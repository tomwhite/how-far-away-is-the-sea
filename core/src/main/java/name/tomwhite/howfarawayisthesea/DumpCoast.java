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
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class DumpCoast {

  public static void main(String[] args) throws Exception {
    if (args.length != 1) {
      System.err.println("Usage: " + DumpCoast.class.getSimpleName() +
          " <land shapefile>");
      System.exit(1);
    }

    File landShapeFile = new File(args[0]);

    DumpCoast.dump(landShapeFile.toURL());
  }

  private static void dump(URL landGeometry) throws IOException {
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
    }
    System.out.println("var poly = [");
    for (Coordinate coord : britain.getCoordinates()) {
      System.out.println(coord.x + ", " + coord.y + ", ");
    }
    System.out.println("];");
    System.out.println("var bounds = [");
    for (Coordinate coord : britain.getEnvelope().getCoordinates()) {
      System.out.println(coord.x + ", " + coord.y + ", ");
    }
    System.out.println("];");
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
