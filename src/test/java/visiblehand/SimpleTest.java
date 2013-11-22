package visiblehand;

import java.io.IOException;

import org.junit.Test;

import visiblehand.entity.Airline;

import com.avaje.ebean.Ebean;

public class SimpleTest {

	@Test
	public void test() throws IOException {
		VisibleHand.initEbean();
		System.out.println(Ebean.find(Airline.class, 24));
	}

}
