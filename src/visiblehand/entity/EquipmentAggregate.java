package visiblehand.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.SqlRow;

@Entity
public @Data
class EquipmentAggregate {
	@ManyToOne
	@JoinColumn(name="parent_id")
	Equipment parent;
	@ManyToOne
	@JoinColumn(name="child_id")
	Equipment child;
	
	public EquipmentAggregate() {
	}
	
	public EquipmentAggregate(Equipment parent, Equipment child) {
		this.parent = parent;
		this.child = child;
	}
}