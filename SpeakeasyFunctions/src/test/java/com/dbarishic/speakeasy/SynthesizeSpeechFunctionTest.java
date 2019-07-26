package com.dbarishic.speakeasy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class SynthesizeSpeechFunctionTest {
  @Test
  public void successfulResponse() {
    SynthesizeSpeechFunction synthesizeSpeechFunction = new SynthesizeSpeechFunction();
    Request request = new Request();
    request.setMessage("testMessage");
    Response result = synthesizeSpeechFunction.handleRequest(request, null);
    assertEquals(result.getStatusCode(), 200);
    String content = result.getBody();
    assertNotNull(content);
    assertTrue(content.contains("testMessage"));
  }
}
