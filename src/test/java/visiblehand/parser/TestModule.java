package visiblehand.parser;

import java.util.Date;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import visiblehand.entity.Receipt;
import visiblehand.entity.ReceiptMessage;
import visiblehand.entity.Utility;
import visiblehand.entity.air.Airline;
import visiblehand.entity.air.Airport;
import visiblehand.entity.air.Equipment;
import visiblehand.entity.air.Flight;
import visiblehand.entity.air.Route;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class TestModule extends SimpleModule {
	public TestModule() {
		super("TestModule");
	}

	@Override
	public void setupModule(SetupContext context) {
		context.setMixInAnnotations(Receipt.class, ReceiptMixIn.class);
		
		context.setMixInAnnotations(Flight.class, FlightMixIn.class);
		context.setMixInAnnotations(Equipment.class, EquipmentMixIn.class);
		context.setMixInAnnotations(Airline.class, AirlineMixIn.class);
		context.setMixInAnnotations(Route.class, RouteMixIn.class);
		context.setMixInAnnotations(Airport.class, AirportMixIn.class);
		
		context.setMixInAnnotations(Utility.class, UtilityMixIn.class);
	}

	abstract class ReceiptMixIn {
		@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
		abstract Date getDate();
		@JsonIgnore
		abstract ReceiptMessage getMessage();
	}
	
	abstract class FlightMixIn {
		@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm")
		abstract Date getDate();
		@JsonIgnore
		abstract int getId();
		@JsonIgnore
		abstract DescriptiveStatistics getFuelBurnStatistics();
		@JsonIgnore
		abstract double getFuelBurn();
		@JsonIgnore
		abstract double getCO2();
	}
	
	@JsonAutoDetect(getterVisibility=JsonAutoDetect.Visibility.NONE)
	abstract class EquipmentMixIn {
		@JsonProperty("IATA")
		abstract String getIATA();
	}
	
	@JsonAutoDetect(getterVisibility=JsonAutoDetect.Visibility.NONE)
	abstract class AirlineMixIn {
		@JsonProperty
		abstract String getName();
	}
	
	@JsonAutoDetect(getterVisibility=JsonAutoDetect.Visibility.NONE, isGetterVisibility=JsonAutoDetect.Visibility.NONE)
	abstract class RouteMixIn {
		@JsonProperty
		abstract Airport getSource();
		@JsonProperty
		abstract Airport getDestination();
	}
	
	@JsonAutoDetect(getterVisibility=JsonAutoDetect.Visibility.NONE)
	abstract class AirportMixIn {
		@JsonProperty
		abstract String getName();
	}
	
	@JsonAutoDetect(getterVisibility=JsonAutoDetect.Visibility.NONE)
	abstract class UtilityMixIn {
		@JsonProperty
		abstract String getName();
	}


}
