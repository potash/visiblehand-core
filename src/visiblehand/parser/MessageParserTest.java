package visiblehand.parser;

import java.io.File;
import java.io.FilenameFilter;

import org.junit.BeforeClass;

import visiblehand.VisibleHand;

public class MessageParserTest {
	
	@BeforeClass
	public static void setup() {
		VisibleHand.loadData();
	}
	
	protected FilenameFilter hiddenFileFilter = new FilenameFilter() {
		public boolean accept(File dir, String name) {
			return !name.startsWith(".");
		}
	};

}
