package org.twocows.adtexo.node;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.twocows.adtexo.context.Context;

public class TestDefaultNode {

	Node defaultNode;
	
	@Before
	public void before() {
		final Context context = new Context();
		context.put(Node.ID_KEY, Node.createID());
		defaultNode = new Node(context);
		System.out.println(defaultNode);
	}
	
	@Test
	public void test() {
		defaultNode.start();
		System.out.println(defaultNode);
		defaultNode.stop();
		System.out.println(defaultNode);
		defaultNode.close();
		System.out.println(defaultNode);
	}

}
