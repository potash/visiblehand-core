package visiblehand.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import visiblehand.VisibleHandTest;

public class MessageParserTest extends VisibleHandTest {
	public static FilenameFilter testFileFilter = new FilenameFilter() {
		public boolean accept(File dir, String name) {
			return name.matches("\\d+");
		}
	};
	
	public static FilenameFilter resultFileFilter = new FilenameFilter() {
		public boolean accept(File dir, String name) {
			return name.matches("\\d+.json");
		}
	};
	
	public static String testDirectoryName = "data/test/";
	
	// returns alphabetical-order list of test messages
	// corresponding to the given parser
	protected static Message[] getTestMessages(MessageParser parser) throws FileNotFoundException, MessagingException {
		File dir = new File(testDirectoryName
				+ parser.getClass().getSimpleName());
		File[] files = dir.listFiles(testFileFilter);
		Arrays.sort(files);
		Message[] messages = new Message[files.length];
		Session session = Session.getDefaultInstance(new Properties());
		for (int i = 0; i < files.length; i++) {
			messages[i] = new MimeMessage(session, new FileInputStream(files[i]));
		}
		return messages;
	}
}
