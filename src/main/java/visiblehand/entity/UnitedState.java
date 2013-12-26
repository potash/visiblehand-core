package visiblehand.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

import com.avaje.ebean.Ebean;

@Entity
public @Data class UnitedState {
	@Id
	private String id;
	private String name;
	
	public static UnitedState find(String id) {
		return Ebean.find(UnitedState.class, id);
	}
}
