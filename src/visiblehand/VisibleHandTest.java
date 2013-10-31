package visiblehand;

import java.io.IOException;

import org.junit.BeforeClass;

public class VisibleHandTest {
	@BeforeClass
	public static void setup() throws IOException {
		VisibleHand.initEbean();
	}
}
