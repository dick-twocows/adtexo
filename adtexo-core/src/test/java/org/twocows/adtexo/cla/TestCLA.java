package org.twocows.adtexo.cla;

import static org.junit.Assert.*;

import java.util.Properties;

import org.junit.Test;

public class TestCLA {

	@Test
	public void test() {
		final CLA cla = new CLA();
		final Properties properties = cla.apply(new String[]{"-greeting+", "Hello World"});
		System.out.println(properties);
	}

}
