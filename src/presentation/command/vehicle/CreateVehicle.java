package presentation.command.vehicle;

import business.vehicle.TVehicle;
import business.vehicle.factory.ASVehicleFactory;
import presentation.command.Command;
import presentation.controller.Event;
import presentation.controller.LightContext;

public class CreateVehicle implements Command {

	@Override
	public LightContext execute(LightContext in) {
		Integer ret = ASVehicleFactory.getInstance().generateASVehicle().create((TVehicle)in.getData());
		return new LightContext(Event.CREATE_VEHICLE, ret);
	}
}
