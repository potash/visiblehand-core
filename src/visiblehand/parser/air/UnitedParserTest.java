package visiblehand.parser.air;

import org.junit.Test;

import visiblehand.EbeanTest;

public class UnitedParserTest extends EbeanTest {

	@Test
	public void testGetEquipment() {
		System.out.println(UnitedParser.getEquipment("Embraer 120"));
	}

}
