package visiblehand.entity.utility;

import java.util.Date;

import javax.persistence.Embedded;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import visiblehand.entity.Receipt;
import visiblehand.entity.ReceiptMessage;
import lombok.Data;

public @Data class NaturalGasReceipt implements Receipt {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	private Date date;
	@Embedded
	private ReceiptMessage message;
	
	@Embedded
	private NaturalGas naturalGas;
}