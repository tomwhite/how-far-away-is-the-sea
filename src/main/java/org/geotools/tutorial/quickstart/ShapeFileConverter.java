package org.geotools.tutorial.quickstart;

import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.io.OutputStreamOutStream;
import com.vividsolutions.jts.io.WKBWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.simple.SimpleFeatureImpl;
import org.opengis.feature.Feature;

public class ShapeFileConverter {

  public static void main(String[] args) throws Exception {
    if (args.length != 2) {
      System.err.println("Usage: " + ShapeFileConverter.class.getSimpleName() +
          " <shapefile> <outfile>");
      System.exit(1);
    }
    File shapeFile = new File(args[0]);
    File jtsFile = new File(args[1]);
    FileOutputStream out = new FileOutputStream(jtsFile);

    MultiPolygon multiPolygon = extractMultiPolygon(shapeFile);
    WKBWriter w = new WKBWriter();
    w.write(multiPolygon, new OutputStreamOutStream(out));
    out.close();
  }

  private static MultiPolygon extractMultiPolygon(File shapeFile) throws IOException {
    FileDataStore store = FileDataStoreFinder.getDataStore(shapeFile);
    SimpleFeatureSource featureSource = store.getFeatureSource();
    FeatureCollection collection = featureSource.getFeatures();
    System.out.println(featureSource.getSchema().getCoordinateReferenceSystem());
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
