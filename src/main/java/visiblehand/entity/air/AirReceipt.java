package visiblehand.entity.air;

import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import lombok.Data;
import visiblehand.entity.Receipt;
import visiblehand.entity.ReceiptMessage;

@Entity
public @Data class AirReceipt implements Receipt {
	public AirReceipt() {	
	}
	
	public AirReceipt(Message message) {
		this.message = new ReceiptMessage(message);
	}

	private static Session session = Session.getDefaultInstance(new Properties());
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	private Airline airline;
	
	@ManyToMany(cascade=CascadeType.ALL)
	private List<Flight> flights;
	
	private String confirmation;
	
	private Date date;
	
	@Embedded
	private ReceiptMessage message;
	
}
