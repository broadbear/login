package org.mike.helpers;

import java.util.ArrayList;
import java.util.List;

public class Flash {
	public static enum MessageType {
		success,
		info,
		danger,
		warning
	}
	List<Message> messages = new ArrayList<Message>();
	
	public List<Message> getMessages() {
		return messages;
	}
	
	public void addMessage(MessageType type, String text) {
		Message m = new Message(type, text);
		messages.add(m);
	}
	
	public static class Message {
		MessageType type;
		String text;
		
		public Message(MessageType type, String text) {
			this.type = type;
			this.text = text;
		}
		
		public MessageType getType() {
			return type;
		}
		
		public String getText() {
			return text;
		}
	}
}
