package org.chatlog.controller;

import org.chatlog.pojo.ChatLog;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class ChatLogControllerTest {

    private ChatLogController chatLogController;
    private ChatLog messageLog;

    @Before
    public void setup() {
        messageLog = new ChatLog();
        messageLog.setMessage("Hello");
        messageLog.setTimestamp(LocalDateTime.now());
        messageLog.setSent(true);
    }

    @After
    public void after() {
        chatLogController.deleteAll("USER001");
    }

    @Test
    public void createMultipleEntryTest() {
        chatLogController = new ChatLogController();
        ResponseEntity responseEntity = createChatLog("USER001");
        Assert.assertNotNull(responseEntity);
        Assert.assertEquals("M-001", responseEntity.getBody());
        messageLog.setMessage("How Are you?");
        responseEntity = createChatLog("USER001");
        Assert.assertNotNull(responseEntity);
        Assert.assertEquals("M-002", responseEntity.getBody());
        messageLog.setMessage("When can we meet?");
        responseEntity = createChatLog("USER001");
        Assert.assertNotNull(responseEntity);
        Assert.assertEquals("M-003", responseEntity.getBody());
        messageLog.setMessage("I am going to John's");
        responseEntity = createChatLog("USER001");
        Assert.assertNotNull(responseEntity);
        Assert.assertEquals("M-004", responseEntity.getBody());
        messageLog.setMessage("I will meet you at 6");
        responseEntity = createChatLog("USER001");
        Assert.assertNotNull(responseEntity);
        Assert.assertEquals("M-005", responseEntity.getBody());
        messageLog.setMessage("Don't forget to bring gift.");
        responseEntity = createChatLog("USER001");
        Assert.assertNotNull(responseEntity);
        Assert.assertEquals("M-006", responseEntity.getBody());
        messageLog.setMessage("Hello John");
        responseEntity = createChatLog("USER002");
        Assert.assertNotNull(responseEntity);
        Assert.assertEquals("M-007", responseEntity.getBody());
        messageLog.setMessage("see you");
        responseEntity = createChatLog("USER001");
        Assert.assertNotNull(responseEntity);
        Assert.assertEquals("M-008", responseEntity.getBody());
        messageLog.setMessage("cab has arrived");
        responseEntity = createChatLog("USER001");
        Assert.assertNotNull(responseEntity);
        Assert.assertEquals("M-009", responseEntity.getBody());
        messageLog.setMessage("don't be late");
        responseEntity = createChatLog("USER001");
        Assert.assertNotNull(responseEntity);
        Assert.assertEquals("M-010", responseEntity.getBody());
        messageLog.setMessage("tata");
        responseEntity = createChatLog("USER001");
        Assert.assertNotNull(responseEntity);
        Assert.assertEquals("M-011", responseEntity.getBody());
        Assert.assertEquals(200, responseEntity.getStatusCodeValue());
    }


    @Test
    public void getChatLogsByStartKeyTest() {
        chatLogController = new ChatLogController();
        createBatchChatLog("USER001");
        ResponseEntity responseEntity = chatLogController.get("USER001", 5, "M-002");
        List<ChatLog> body = (List<ChatLog>) responseEntity.getBody();
        Assert.assertEquals(body.size(), 5);
    }

    @Test
    public void getChatLogsByDefaultKeyTest() {
        chatLogController = new ChatLogController();
        createBatchChatLog("USER001");
        ResponseEntity responseEntity = chatLogController.get("USER001", 2, null);
        List<ChatLog> body = (List<ChatLog>) responseEntity.getBody();
        Assert.assertEquals(body.size(), 2);
    }

    @Test
    public void getChatLogsByDefaultLimitTest() {
        chatLogController = new ChatLogController();
        createBatchChatLog("USER001");
        ResponseEntity responseEntity = chatLogController.get("USER001", null, "M-001");
        List<ChatLog> body = (List<ChatLog>) responseEntity.getBody();
        Assert.assertEquals(body.size(), 10);
    }

    @Test
    public void deleteAllTest() {
        chatLogController = new ChatLogController();
        createBatchChatLog("USER001");
        ResponseEntity responseEntity = chatLogController.deleteAll("USER001");
        Boolean isAllDeleted = (Boolean) responseEntity.getBody();
        Assert.assertTrue(isAllDeleted);
    }

    @Test
    public void deleteAllForInvalidUserTest() {
        chatLogController = new ChatLogController();
        createBatchChatLog("USER001");
        ResponseEntity responseEntity = chatLogController.deleteAll("USER0010");
        Map<String, String> body = (Map<String, String>) responseEntity.getBody();
        Assert.assertEquals(body.get("message"), "No messages found for given user");
        Assert.assertEquals(body.get("error"), "true");
    }

    @Test
    public void deleteByInvalidIdTest() {
        chatLogController = new ChatLogController();
        createBatchChatLog("USER001");
        ResponseEntity responseEntity = chatLogController.deleteById("USER001", "M-110");
        Map<String, String> body = (Map<String, String>) responseEntity.getBody();
        Assert.assertEquals(body.get("message"), "MessageID is not found");
        Assert.assertEquals(body.get("error"), "true");
    }

    @Test
    public void deleteByIdTest() {
        chatLogController = new ChatLogController();
        createBatchChatLog("USER001");
        ResponseEntity responseEntity = chatLogController.deleteById("USER001", "M-001");
        boolean isDeletedByID = (boolean) responseEntity.getBody();
        Assert.assertTrue(isDeletedByID);
    }

    private ResponseEntity createChatLog(String user) {
        return chatLogController.create(user, messageLog);
    }

    private void createBatchChatLog(String user) {
        for (int i = 0; i < 10; i++) {
            messageLog.setMessage("Hello" + i);
            chatLogController.create(user, messageLog);
        }
    }
}
