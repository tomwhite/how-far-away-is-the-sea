package org.geotools.tutorial.quickstart;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.InputStreamInStream;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKBReader;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.operation.distance.DistanceOp;
import com.vividsolutions.jts.operation.distance.GeometryLocation;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class CoasterJts {

  private final URL landGeometry;
  private final URL oceanGeometry;

  public CoasterJts(URL landGeometry, URL oceanGeometry) {
    this.landGeometry = landGeometry;
    this.oceanGeometry = oceanGeometry;
  }

  public DistanceToCoast distanceFromCoast(double lat, double lng) throws IOException {
    GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
    WKTReader reader = new WKTReader(geometryFactory);
    Point point = null;
    try {
      point = (Point) reader.read("POINT (" + lng + " " + lat + ")");
    } catch (ParseException e) {
      throw new IOException(e);
    }

    MultiPolygon landMultiPolygon = extractMultiPolygon(landGeometry);

//    if (landMultiPolygon.contains(point)) {
//      MultiPolygon oceanMultiPolygon = extractMultiPolygon(oceanShapeFile);
//      return distanceToCoast(true, oceanMultiPolygon, point);
//    } else {
//      return distanceToCoast(false, landMultiPolygon, point);
//    }
    return distanceToCoast(true, landMultiPolygon, point);
  }

  private DistanceToCoast distanceToCoast(boolean onLand, MultiPolygon mp, Point point) throws IOException {
    DistanceOp d = new DistanceOp(point, mp);
    GeometryLocation[] locs = d.nearestLocations();
    System.out.println(locs[0].getCoordinate().y + "," + locs[0].getCoordinate().x);
    System.out.println(locs[1].getCoordinate().y + "," + locs[1].getCoordinate().x);
    CoordinateReferenceSystem crs = null;
    return new DistanceToCoast(locs[0].getCoordinate().y, locs[0].getCoordinate().x,
        locs[1].getCoordinate().y, locs[1].getCoordinate().x,
        onLand, 0);
  }

  private MultiPolygon extractMultiPolygon(URL shapeFile) throws IOException {
    try {
      WKBReader wkbReader = new WKBReader();
      return (MultiPolygon) wkbReader.read(new InputStreamInStream(shapeFile.openStream()));
    } catch (ParseException e) {
      throw new IOException(e);
    }
  }

  public static void main(String[] args) throws Exception {
    if (args.length != 4) {
      System.err.println("Usage: " + CoasterJts.class.getSimpleName() +
          " <land shapefile> <ocean shapefile> <lat> <long>");
      System.exit(1);
    }

    File landShapeFile = new File(args[0]);
    File oceanShapeFile = new File(args[1]);
    String lat = args[2];
    String lng = args[3];

    CoasterJts coaster = new CoasterJts(landShapeFile.toURL(), oceanShapeFile.toURL());
    DistanceToCoast distanceToCoast = coaster.distanceFromCoast(Double.parseDouble(lat), Double.parseDouble(lng));
    if (distanceToCoast.isOnLand()) {
      System.out.println("You are on land!");
    } else {
      System.out.println("You are at sea!");
    }
    System.out.printf("Distance from the coast: %.1f km\n", distanceToCoast.getDistanceToCoast() / 1000);
  }
}
