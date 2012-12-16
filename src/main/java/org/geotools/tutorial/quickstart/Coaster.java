package org.geotools.tutorial.quickstart;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.operation.distance.DistanceOp;
import com.vividsolutions.jts.operation.distance.GeometryLocation;
import java.io.File;
import java.io.IOException;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.simple.SimpleFeatureImpl;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.opengis.feature.Feature;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

public class Coaster {

  public static void main(String[] args) throws Exception {
    if (args.length != 4) {
      System.err.println("Usage: " + Coaster.class.getSimpleName() +
          " <land shapefile> <ocean shapefile> <lat> <long>");
      System.exit(1);
    }

    File landShapeFile = new File(args[0]);
    File oceanShapeFile = new File(args[1]);
    String lat = args[2];
    String lng = args[3];

    GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
    WKTReader reader = new WKTReader(geometryFactory);
    Point point = (Point) reader.read("POINT (" + lng + " " + lat + ")");

    MultiPolygon landMultiPolygon = extractMultiPolygon(landShapeFile);

    FileDataStore store = FileDataStoreFinder.getDataStore(landShapeFile);
    SimpleFeatureSource featureSource = store.getFeatureSource();
    CoordinateReferenceSystem crs = featureSource.getSchema().getCoordinateReferenceSystem();

    if (landMultiPolygon.contains(point)) {
      MultiPolygon oceanMultiPolygon = extractMultiPolygon(oceanShapeFile);
      System.out.println("You are on land!");
      System.out.printf("Distance from the coast: %.1f km\n", distanceToCoast(oceanMultiPolygon, point, crs));
    } else {
      System.out.println("You are at sea!");
      System.out.printf("Distance from the coast: %.1f km\n", distanceToCoast(landMultiPolygon, point, crs));
    }

  }

  private static double distanceToCoast(MultiPolygon mp, Point point, CoordinateReferenceSystem crs) throws TransformException {
    DistanceOp d = new DistanceOp(point, mp);
    GeometryLocation[] locs = d.nearestLocations();
    System.out.println(locs[0].getCoordinate().y + "," + locs[0].getCoordinate().x);
    System.out.println(locs[1].getCoordinate().y + "," + locs[1].getCoordinate().x);
    return JTS.orthodromicDistance(locs[0].getCoordinate(), locs[1].getCoordinate(), crs) / 1000;
  }

  private static MultiPolygon extractMultiPolygon(File shapeFile) throws IOException {
    FileDataStore store = FileDataStoreFinder.getDataStore(shapeFile);
    SimpleFeatureSource featureSource = store.getFeatureSource();
    FeatureCollection collection = featureSource.getFeatures();
    FeatureIterator iterator = collection.features();
    if (iterator.hasNext()) {
      Feature feature = iterator.next();
      SimpleFeatureImpl sf = (SimpleFeatureImpl) feature;
      Object geo = sf.getDefaultGeometry();
      if (geo instanceof MultiPolygon) {
        return (MultiPolygon) geo;
      }
    }
    return null;
  }
}
