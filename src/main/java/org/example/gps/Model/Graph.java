package org.example.gps.Model;

import org.example.gps.Utils.DestinationTypes;
import java.time.LocalDate;
import java.util.HashMap;

public class Graph {
    HashMap<String, Nodo> graph = new HashMap<>();
    private static final HashMap<String, Double> DAY_TRAFFIC_MULTIPLIER = new HashMap<>();

    static {
        DAY_TRAFFIC_MULTIPLIER.put("MONDAY", 2.0);
        DAY_TRAFFIC_MULTIPLIER.put("TUESDAY", 2.0);
        DAY_TRAFFIC_MULTIPLIER.put("WEDNESDAY", 2.0);
        DAY_TRAFFIC_MULTIPLIER.put("THURSDAY", 2.0);
        DAY_TRAFFIC_MULTIPLIER.put("FRIDAY", 2.0);
        DAY_TRAFFIC_MULTIPLIER.put("SATURDAY", 1.0);
        DAY_TRAFFIC_MULTIPLIER.put("SUNDAY", 1.0);
    }

    private double getDistance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371e3; // metres
        double φ1 = lat1 * Math.PI / 180; // φ, λ in radians
        double φ2 = lat2 * Math.PI / 180;
        double Δφ = (lat2 - lat1) * Math.PI / 180;
        double Δλ = (lon2 - lon1) * Math.PI / 180;
        double a = Math.sin(Δφ / 2) * Math.sin(Δφ / 2) +
                Math.cos(φ1) * Math.cos(φ2) *
                        Math.sin(Δλ / 2) * Math.sin(Δλ / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return (R * c) / 1000;
    }

    private String getDay() {
        LocalDate today = LocalDate.now();
        return today.getDayOfWeek().toString();
    }

    private double getHourTrafficMultiplier(double hour) {
        int h = (int) hour;

        if (h >= 1 && h <= 6) {
            return 1.0; // Low traffic
        } else if (h >= 7 && h <= 12) {
            return 1.5; // Moderate traffic
        } else if (h >= 13 && h <= 18) {
            return 2.0; // High traffic
        } else {
            return 1.0; // Moderate to low traffic
        }
    }

    private double getLocationTrafficMultiplier(Nodo node) {
        DestinationTypes nodeType = node.getType();

        if (nodeType == null) return 1.0;

        switch (nodeType) {
            case MALL:
                return 1.8;
            case SCHOOL:
                return 1.6;
            case HOSPITAL:
                return 1.4;
            default:
                return 1.0;
        }
    }

    public double getSpeed(double baseSpeed, double hour, Nodo location) {
        String day = getDay();
        double dayMultiplier = DAY_TRAFFIC_MULTIPLIER.getOrDefault(day, 1.0);
        double hourMultiplier = getHourTrafficMultiplier(hour);
        double locationMultiplier = getLocationTrafficMultiplier(location);
        double trafficFactor = dayMultiplier * hourMultiplier * locationMultiplier;
        return baseSpeed / trafficFactor;
    }

    private double getTimeBetweenNodes(Nodo n1, Nodo n2, double hour, double baseSpeed) {
        double distance = getDistance(n1.getLatitud(), n1.getLongitud(), n2.getLatitud(), n2.getLongitud());
        double effectiveSpeed = getSpeed(baseSpeed, hour, n1);
        System.out.println(distance + "-" +effectiveSpeed);
        return distance / effectiveSpeed;
    }

    public double getTimeBetweenNodesInMinutes(Nodo n1, Nodo n2, double baseSpeed, int currentHour) {
        return getTimeBetweenNodes(n1, n2, currentHour,baseSpeed) * 60.0;
    }

    public void generateAboutFile() {
        Nodo n1 = new Nodo(1,"Nombre_1",DestinationTypes.PARKING,19.426142,-99.149704,2236.9);
        Nodo n2 = new Nodo(2,"Nombre_2",DestinationTypes.RESTAURANT,19.421727,-99.132586,2242.0);
        System.out.println(getTimeBetweenNodesInMinutes(n1,n2,20,4));
        // TODO: Implementation for file generation
    }
}