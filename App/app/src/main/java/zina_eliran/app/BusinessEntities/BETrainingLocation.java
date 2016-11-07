package zina_eliran.app.BusinessEntities;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class BETrainingLocation extends BEBaseEntity {

    private double longitude;
    private double latitude;
    private double altitude;
    Calendar locationMeasureTime;

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public LatLng getLocationLatLng() {
        return null;
    }

    public String getAddressPart(Context context, BEAddressPartsEnum addressPart) {
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(
                    this.getLatitude(),
                    this.getLongitude(),
                    1);

            switch (addressPart) {
                case country:
                    return addresses.get(0).getCountryName();
                case city:
                    return addresses.get(0).getLocality();
                case street:
                    return addresses.get(0).getThoroughfare();
                case streetNumber:
                    return addresses.get(0).getSubThoroughfare();
            }

        } catch (Exception e) {
            CMNLogHelper.logError("BETraining", e.getMessage());
        }
        return "Address part Not found | " + addressPart;
    }

    public Calendar getLocationMeasureTime() {
        return locationMeasureTime;
    }

    public void setLocationMeasureTime(Calendar locationMeasureTime) {
        this.locationMeasureTime = locationMeasureTime;
    }

    public static List<LatLng> getLatLngList(List<BETrainingLocation> locationsList) {
        List<LatLng> list = new ArrayList<>();
        for (BETrainingLocation item : locationsList) {
            list.add(new LatLng(item.getLatitude(), item.getLongitude()));
        }
        return list;
    }

    public static float getDistance(BETrainingLocation location1, BETrainingLocation location2) {
        Location l1 = new Location("l1");
        l1.setLatitude(location1.getLatitude());
        l1.setLongitude(location1.getLongitude());
        l1.setAltitude(location1.getAltitude());
        Location l2 = new Location("l2");
        l2.setLatitude(location2.getLatitude());
        l2.setLongitude(location2.getLongitude());
        l2.setAltitude(location2.getAltitude());
        return l1.distanceTo(l2);
    }

    public static float getTimeMeasureDiff(BETrainingLocation l1, BETrainingLocation l2, int timePart) {
        return (l1.getLocationMeasureTime().getTimeInMillis() - Calendar.getInstance().getTimeInMillis())/(timePart*1000);
    }

    public static float getLocationRouteDistance(List<BETrainingLocation> locationsList, boolean isKilometer) {
        //in Meters
        float distance = 0;
        for (int i = 0; i < locationsList.size() - 1; i++) {
           distance += getDistance(locationsList.get(i), locationsList.get(i+1));
        }
        return !isKilometer ? distance : distance/1000;
    }
}
