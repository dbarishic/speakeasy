package com.dbarishic.speakeasy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class AppTest {
  @Test
  public void successfulResponse() {
    App app = new App();
    Request request = new Request();
    request.setMessage("testMessage");
    Response result = app.handleRequest(request, null);
    assertEquals(result.getStatusCode(), 200);
    String content = result.getBody();
    assertNotNull(content);
    assertTrue(content.contains("testMessage"));
  }
}
