package visiblehand.parser;

import java.io.File;
import java.io.FilenameFilter;

import visiblehand.VisibleHandTest;

public class MessageParserTest extends VisibleHandTest {
	protected FilenameFilter hiddenFileFilter = new FilenameFilter() {
		public boolean accept(File dir, String name) {
			return !name.startsWith(".");
		}
	};

	protected File[] getTestMessages() {
		File dir = new File("data/mail/"
				+ this.getClass().getSimpleName().replace("Test", ""));
		return dir.listFiles(hiddenFileFilter);
	}
}
