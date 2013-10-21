package visiblehand;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class VisibleHandTest {
	@BeforeClass
	public static void setup() {
		VisibleHand.loadData();
	}
}
