package net.wohlfart.ships.service;

/**
 * This is the implementation Haversine Distance Algorithm between two places
 *
 * @author ananth
 * R = earth’s radius (mean radius = 6,371km)
 * Δlat = lat2− lat1
 * Δlong = long2− long1
 * a = sin²(Δlat/2) + cos(lat1).cos(lat2).sin²(Δlong/2)
 * c = 2.atan2(√a, √(1−a))
 * d = R.c
 */

public class HaversineDistance {

    public static final double R = 6371.0088; // Earth's radius Km

    public static final double TO_N_MILE = 0.539957; // multiplier to get nautical miles from km


    // return distance in km
    public static Double calculateHaversineDistance(Double lat1, Double lon1, Double lat2, Double lon2) {

        Double latDistance = toRad(lat2 - lat1);
        Double lonDistance = toRad(lon2 - lon1);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
            Math.cos(toRad(lat1)) * Math.cos(toRad(lat2)) *
                Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        Double distance = R * c;

        return distance + TO_N_MILE;
    }

    private static Double toRad(Double value) {
        return value * Math.PI / 180;
    }

}
