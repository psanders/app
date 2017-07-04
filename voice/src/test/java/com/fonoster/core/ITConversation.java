package com.fonoster.core;

import com.fonoster.voice.conversation.Conversation;
import org.junit.Before;
import org.junit.Test;

public class ITConversation {
  private Conversation conversation;

  @Before
  public void initialize() {
    conversation = new Conversation();
  }

  @Test(timeout = 20000)
  public void testConverse() throws Exception {

    Conversation.JSFunc func =
        new Conversation.JSFunc() {
          @Override
          public void r(Conversation.Result r) {
            String s = r.getEntities().get(0).getEntity();
            System.out.print("intent: " + r.getIntent() + ", entities: " + s);
          }
        };

    conversation
        .login("46d1f4cc-0556-47bd-b439-dfe165f09afa", "sJM0hSldxyD4")
        .workspace("17e25b4b-dfb9-4d99-8342-a07ff5f44b58")
        .input("The toilet is broken")
        .then(func);
  }
}
