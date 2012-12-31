package name.tomwhite.howfarawayisthesea;

public class DistanceToCoast {

  private final double latitude;
  private final double longitude;
  private final double coastLatitude;
  private final double coastLongitude;
  private final boolean onLand;
  private final double distanceToCoast;

  public DistanceToCoast(double latitude, double longitude,
      double coastLatitude, double coastLongitude,
      boolean onLand, double distanceToCoast) {
    this.latitude = latitude;
    this.longitude = longitude;
    this.coastLatitude = coastLatitude;
    this.coastLongitude = coastLongitude;
    this.onLand = onLand;
    this.distanceToCoast = distanceToCoast;
  }

  public double getLatitude() {
    return latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public double getCoastLatitude() {
    return coastLatitude;
  }

  public double getCoastLongitude() {
    return coastLongitude;
  }

  public boolean isOnLand() {
    return onLand;
  }

  public double getDistanceToCoast() {
    return distanceToCoast;
  }

  @Override
  public String toString() {
    return "DistanceToCoast{" +
        "latitude=" + latitude +
        ", longitude=" + longitude +
        ", coastLatitude=" + coastLatitude +
        ", coastLongitude=" + coastLongitude +
        ", onLand=" + onLand +
        ", distanceToCoast=" + distanceToCoast +
        '}';
  }
}
