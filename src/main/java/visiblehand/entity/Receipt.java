package visiblehand.entity;

import java.util.Date;


public interface Receipt {
	public abstract Date getDate();
	public abstract void setDate(Date date);
	
	public abstract void setMessage(ReceiptMessage message);
	public abstract ReceiptMessage getMessage();

}