package visiblehand.entity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;

import visiblehand.data.CustomBooleanDeserializer;
import lombok.Data;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;

@JsonPropertyOrder({ "id", "name", "alias", "iata", "icao", "callsign",
		"country", "active" })
@Entity
public @Data
class Airline {
	@Id
	private int id;
	private String name;
	private String alias;
	private String IATA;
	private String ICAO;
	private String callsign;
	private String country;
	private Boolean active;
}
