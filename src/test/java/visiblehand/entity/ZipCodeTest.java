package visiblehand.entity;

import org.junit.Test;

import visiblehand.EbeanTest;
import visiblehand.entity.utility.EGridSubregion;

import com.avaje.ebean.Ebean;

public class ZipCodeTest extends EbeanTest {

	@Test
	public void test() {
		System.out.println(Ebean.find(ZipCode.class).findRowCount());
//		for (ZipCode zip : Ebean.find(ZipCode.class).findList()) {
//		}
	}

}
