package visiblehand;

import java.io.Console;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.PasswordAuthentication;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import visiblehand.parser.AirParser;

public class SendMessages {

	public static void main(String[] args) throws FileNotFoundException, MessagingException, IOException {
		Session session = VisibleHand.getSession();
		PasswordAuthentication auth = VisibleHand.getPasswordAuthentication();
		System.out.println("Opening inbox...");
		Folder inbox = VisibleHand.getInbox(auth);
		
		Console console = System.console();
		System.out.print("To:");
		InternetAddress[] toAddress = InternetAddress.parse(console.readLine());
		boolean sendAll = false;
		
		for (AirParser parser : VisibleHand.airParsers) {
			System.out.println("Fetching messages for " + parser.getClass().getSimpleName() + "...");
			for (Message message : inbox.search(parser.getSearchTerm())) {
				System.out.println(message.getFrom()[0].toString() + " on " + message.getSentDate() + ": " + message.getSubject());
				Boolean send = null;
				if (sendAll)
					send = true;
					
				while (send == null) {
					System.out.print("Send? [(Y)es]/(N)o/Yes to (A)ll: ");
					char c = console.readLine().toUpperCase().charAt(0);
					switch (c) {
						case 'Y': send = true; break;
						case 'N': send = false; break;
						case 'A': send = sendAll = true; break;
						default: System.out.print("Invalid option. Try again? ");
					}
				}
				if (send) {
					Message copy = new MimeMessage(session);
					
					copy.setContent(message.getContent(), message.getContentType());
					copy.setSubject(message.getSubject());
					// mail hosts don't allow arbitrary from so put it in the reply-to
					copy.setReplyTo(message.getFrom());
					
					copy.setRecipients(Message.RecipientType.TO, toAddress);
					Transport.send(copy, auth.getUserName(), new String(auth.getPassword()));
				}
			}
		}
	}

}
