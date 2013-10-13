package visiblehand.parser;

import java.io.File;
import java.io.FilenameFilter;

public class MessageParserTest {

	protected FilenameFilter hiddenFileFilter = new FilenameFilter() {
		public boolean accept(File dir, String name) {
			return !name.startsWith(".");
		}
	};

}
