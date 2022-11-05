### ChatLog Server
ChatLog Server : Built in Java 11, a spring boot application.
MVP Version : v1.1

Built a chat log server that will be used by other internal services. This server presents an HTTP interface with the following commands.Don’t have to worry about authentication and assume <user> is an alphanumeric string of less than 16 characters.

● POST /chatlogs/<user>/
Creates a new chatlog entry for the user <user>. The POST data can either be url encoded or
JSON encoded. The data should contain the following fields.
■ message - a String representing the message text
■ timestamp - a Long representing the timestamp
■ isSent - a Boolean/Integer representing if this message was sent by the user or received by the user.
The response from the message should be a unique messageID that we can refer to the message by.

● GET /chatlogs/<user>
Returns chatlogs for the given user. These should be returned in reverse timeorder (most
recent messages first).Takes two optional parameters.
■ limit - an Integer stating how many messages should return. Default to 10
■ start - a key of the same type as messageID to determine where to start from. This is to help implement pagination. If not set, assume the most recent messages. You may return the response encoded in any format you wish.

● DELETE /chatlogs/<user>
Deletes all the chat logs for a given user.

● DELETE /chatlogs/<user>/<msgid>
Delete just the given chatlog for a given user. Returns an appropriate HTTP error response if the msgid is not found.