package business.vehicle.as.TOA;

import business.city.TCity;
import business.vehicle.TVehicle;

public class TVehicleDetails {
    private TVehicle vehicle;
    private TCity city;

    public TVehicleDetails(TVehicle vehicle, TCity city) {
        this.vehicle = vehicle;
        this.city = city;
    }
}
