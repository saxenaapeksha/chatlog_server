package org.chatlog.controller;

import org.chatlog.pojo.ChatLog;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/chatlogs")
public class ChatLogController {
    private static final Map<String, List<String>> userToMessageIDRegistry = new HashMap<>();
    private static final Map<String, ChatLog> messageLogRegistry = new HashMap<>();

    @RequestMapping(method = RequestMethod.POST, value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity create(@RequestParam String user, @RequestBody ChatLog chatLog) {
        String messageID = generateMessageID();
        messageLogRegistry.put(messageID, new ChatLog(chatLog.getMessage(), chatLog.getTimestamp(), chatLog.isSent()));
        registerMessageIDWithUser(user, messageID);
        return new ResponseEntity(messageID, HttpStatus.valueOf(200));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity get(@RequestParam String user, @RequestParam(required = false, defaultValue = "10") Integer limit, @RequestParam(required = false) String key) {
        List<String> messageIDs = retrieveMessageIDs(user);
        List<ChatLog> chatLogs = retrieveChatLogs(messageIDs);
        limit = validateLimit(limit);
        int startKey = calculateStartKey(messageIDs, key, limit);
        chatLogs = paginateByLimit(sortChatLogByLatest(chatLogs), limit, startKey);
        return new ResponseEntity(chatLogs, HttpStatus.valueOf(200));
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/")
    public ResponseEntity deleteAll(@RequestParam String user) {
        List<String> messageIDs = retrieveMessageIDs(user);
        if (messageIDs == null) {
            Map<String, String> body = buildErrorResponseBody("No messages found for given user");
            return new ResponseEntity(body, HttpStatus.valueOf(200));
        }
        deleteAllChatLogsForUser(user, messageIDs);
        boolean isAllDeleted = !userToMessageIDRegistry.containsKey(user);
        return new ResponseEntity(isAllDeleted, HttpStatus.valueOf(200));
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/")
    public ResponseEntity deleteById(@RequestParam String user, @RequestParam String messageID) {
        List<String> messageIDs = retrieveMessageIDs(user);
        if (messageIDs == null || !messageIDs.contains(messageID)) {
            Map<String, String> body = buildErrorResponseBody("MessageID is not found");
            return new ResponseEntity(body, HttpStatus.valueOf(200));
        }
        deleteChatByID(messageID, messageIDs);
        boolean isDeletedByID = !messageLogRegistry.containsKey(messageID);
        return new ResponseEntity(isDeletedByID, HttpStatus.valueOf(200));
    }

    private void registerMessageIDWithUser(String user, String messageID) {
        List<String> messageIDs;
        if (userToMessageIDRegistry.containsKey(user))
            userToMessageIDRegistry.get(user).add(messageID);
        else {
            messageIDs = new ArrayList<String>();
            messageIDs.add(messageID);
            userToMessageIDRegistry.put(user, messageIDs);
        }
    }

    private List<String> retrieveMessageIDs(String user) {
        return userToMessageIDRegistry.get(user);
    }

    private Map<String, String> buildErrorResponseBody(String errorMessage) {
        Map<String, String> body = new HashMap<>();
        body.put("message", errorMessage);
        body.put("error", "true");
        return body;
    }

    private void deleteChatByID(String messageIDToBeDeleted, List<String> messageIDs) {
        messageLogRegistry.remove(messageIDToBeDeleted);
        messageIDs.remove(messageIDToBeDeleted);
    }

    private void deleteAllChatLogsForUser(String user, List<String> messageIDs) {
        for (String messageID : messageIDs) {
            messageLogRegistry.remove(messageID);
        }
        userToMessageIDRegistry.remove(user);
    }

    private Integer validateLimit(Integer limit) {
        return (limit == null) ? 10 : limit;
    }

    private int calculateStartKey(List<String> messageIDs, String key, Integer limit) {
        int totalMessages = messageIDs.size();
        int startKey;
        if (key == null)
            return totalMessages - limit;
        for (startKey = 0; startKey < totalMessages; startKey++) {
            if (key.equals(messageIDs.get(startKey)))
                break;
        }
        return startKey;
    }

    private List<ChatLog> paginateByLimit(List<ChatLog> chatLogs, Integer limit, int startKey) {
        return chatLogs.stream().skip(startKey).limit(limit).collect(Collectors.toList());
    }

    private List<ChatLog> sortChatLogByLatest(List<ChatLog> chatLogs) {
        chatLogs.sort(Comparator.comparing(ChatLog::getTimestamp));
        return chatLogs;
    }

    private List<ChatLog> retrieveChatLogs(List<String> messageIDs) {
        List<ChatLog> chatLogs = new ArrayList<>();
        for (String messageID : messageIDs)
            chatLogs.add(messageLogRegistry.get(messageID));
        return chatLogs;
    }

    private String generateMessageID() {
        return "M-" + String.format("%03d", messageLogRegistry.size() + 1);
    }
}
