package org.geotools.tutorial.quickstart;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.operation.distance.DistanceOp;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureReader;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.simple.SimpleFeatureImpl;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.opengis.feature.Feature;

public class Coaster {

  public static void main(String[] args) throws Exception {
    File file = new File("/Users/tom/Downloads/ne_10m_land/ne_10m_land.shp");

    FileDataStore store = FileDataStoreFinder.getDataStore(file);
    SimpleFeatureSource featureSource = store.getFeatureSource();
    FeatureCollection collection = featureSource.getFeatures();
    FeatureIterator iterator = collection.features();

    System.out.println(featureSource.getSchema().getCoordinateReferenceSystem());

    try {
      while (iterator.hasNext()) {
        Feature feature = iterator.next();
        System.out.println(feature.getClass());
        System.out.println(feature.getIdentifier().getID());

        SimpleFeatureImpl sf = (SimpleFeatureImpl) feature;
        int attributeCount = sf.getAttributeCount();
        System.out.println(sf.getDefaultGeometryProperty().getType());
        System.out.println( sf.getDefaultGeometry().getClass());

        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
        WKTReader reader = new WKTReader(geometryFactory);
        Point point = (Point) reader.read("POINT (-3.32 56.46)");

        Object geo = sf.getDefaultGeometry();
        if (geo instanceof MultiPolygon) {
          MultiPolygon mp = (MultiPolygon) geo;
          System.out.println(mp.contains(point));
          DistanceOp d = new DistanceOp(point, mp);
          System.out.println(d.nearestLocations()[0].getCoordinate());
          System.out.println(d.nearestLocations()[1].getCoordinate());
        }

      }
    } finally {
      iterator.close();
    }
  }
}
