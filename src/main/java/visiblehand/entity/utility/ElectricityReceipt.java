package visiblehand.entity.utility;

import java.util.Date;

import javax.mail.Message;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import lombok.Data;
import visiblehand.entity.Receipt;
import visiblehand.entity.ReceiptMessage;

@Entity
public @Data class ElectricityReceipt implements Receipt {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	private Date date;
	
	@OneToOne
	private ReceiptMessage message;
	
	@ManyToOne
	private Utility utility;
	
	@Embedded
	private Electricity electricity;
}