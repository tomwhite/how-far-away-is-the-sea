package org.geotools.tutorial.quickstart;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.ParseException;
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

  private final File landShapeFile;
  private final File oceanShapeFile;

  public Coaster(File landShapeFile, File oceanShapeFile) {
    this.landShapeFile = landShapeFile;
    this.oceanShapeFile = oceanShapeFile;
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

    MultiPolygon landMultiPolygon = extractMultiPolygon(landShapeFile);

    FileDataStore store = FileDataStoreFinder.getDataStore(landShapeFile);
    SimpleFeatureSource featureSource = store.getFeatureSource();
    CoordinateReferenceSystem crs = featureSource.getSchema().getCoordinateReferenceSystem();
    if (landMultiPolygon.contains(point)) {
      MultiPolygon oceanMultiPolygon = extractMultiPolygon(oceanShapeFile);
      return distanceToCoast(true, oceanMultiPolygon, point, crs);
    } else {
      return distanceToCoast(false, landMultiPolygon, point, crs);
    }
  }

  private DistanceToCoast distanceToCoast(boolean onLand, MultiPolygon mp, Point point, CoordinateReferenceSystem crs) throws IOException {
    try {
      DistanceOp d = new DistanceOp(point, mp);
      GeometryLocation[] locs = d.nearestLocations();
      System.out.println(locs[0].getCoordinate().y + "," + locs[0].getCoordinate().x);
      System.out.println(locs[1].getCoordinate().y + "," + locs[1].getCoordinate().x);
      return new DistanceToCoast(locs[0].getCoordinate().y, locs[0].getCoordinate().x,
          locs[1].getCoordinate().y, locs[1].getCoordinate().x,
          onLand, JTS.orthodromicDistance(locs[0].getCoordinate(), locs[1].getCoordinate(), crs));
    } catch (TransformException e) {
      throw new IOException(e);
    }
  }

  private MultiPolygon extractMultiPolygon(File shapeFile) throws IOException {
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

    Coaster coaster = new Coaster(landShapeFile, oceanShapeFile);
    DistanceToCoast distanceToCoast = coaster.distanceFromCoast(Double.parseDouble(lat), Double.parseDouble(lng));
    if (distanceToCoast.isOnLand()) {
      System.out.println("You are on land!");
    } else {
      System.out.println("You are at sea!");
    }
    System.out.printf("Distance from the coast: %.1f km\n", distanceToCoast.getDistanceToCoast() / 1000);
  }
}
