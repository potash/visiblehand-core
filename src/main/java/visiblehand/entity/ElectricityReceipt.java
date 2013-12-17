package visiblehand.entity;

import java.util.Date;

import javax.persistence.Embedded;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

public @Data class ElectricityReceipt implements Receipt {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	private Date date;
	@Embedded
	private ReceiptMessage message;
	
	@Embedded
	private Electricity electricity;
}