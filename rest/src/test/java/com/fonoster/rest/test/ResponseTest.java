package com.fonoster.rest.test;

import com.fonoster.rest.ResponseUtil;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

public class ResponseTest {

	@Test
	public void testResponseBuild() {

		Response r = Response.status(Response.Status.BAD_REQUEST).entity("Any text").build();
		assertEquals("Any text", r.getEntity());

		Response r2 = ResponseUtil.getResponse(ResponseUtil.BAD_REQUEST, "Any text");
		assertNotSame("Any text", r2.getEntity());
	}
}
