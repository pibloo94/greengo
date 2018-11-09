package presentation.command.vehicle;

import business.ASException;
import business.vehicle.TVehicleDetails;
import business.vehicle.factory.ASVehicleFactory;
import presentation.command.Command;
import presentation.controller.Event;
import presentation.controller.LightContext;

import java.util.Collection;

public class ShowAllAvailableVehicles implements Command {
	@Override
	public LightContext execute(LightContext in) throws ASException {
        Collection<TVehicleDetails> ret = ASVehicleFactory.getInstance().generateASVehicle().showAllAvailableVehicles();
        return new LightContext(Event.SHOWALL_AVAILABLE_VEHICLE, ret);
	}
}
