package io.docquery.util;

import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public final class MessageHelper {

	private MessageHelper() {

	}

	public static void addSuccessAttribute(RedirectAttributes ra, String message, Object... args) {
		addAttribute(ra, message, Message.Type.SUCCESS, args);
	}

	public static void addErrorAttribute(RedirectAttributes ra, String message, Object... args) {
		addAttribute(ra, message, Message.Type.DANGER, args);
	}

	public static void addInfoAttribute(RedirectAttributes ra, String message, Object... args) {
		addAttribute(ra, message, Message.Type.INFO, args);
	}

	public static void addWarningAttribute(RedirectAttributes ra, String message, Object... args) {
		addAttribute(ra, message, Message.Type.WARNING, args);
	}

	private static void addAttribute(RedirectAttributes ra, String message, Message.Type type, Object... args) {
		ra.addFlashAttribute(Message.MESSAGE_ATTRIBUTE, new Message(message, type, args));
	}

	public static void addSuccessAttribute(Model model, String message, Object... args) {
		addAttribute(model, message, Message.Type.SUCCESS, args);
	}

	public static void addErrorAttribute(Model model, String message, Object... args) {
		addAttribute(model, message, Message.Type.DANGER, args);
	}

	public static void addInfoAttribute(Model model, String message, Object... args) {
		addAttribute(model, message, Message.Type.INFO, args);
	}

	public static void addWarningAttribute(Model model, String message, Object... args) {
		addAttribute(model, message, Message.Type.WARNING, args);
	}

	private static void addAttribute(Model model, String message, Message.Type type, Object... args) {
		model.addAttribute(Message.MESSAGE_ATTRIBUTE, new Message(message, type, args));
	}

	/**
	 * A message to be displayed in web context. Depending on the type, different style will
	 * be applied.
	 */
	public static class Message {

		/**
		 * Name of the flash attribute.
		 */
		public static final String MESSAGE_ATTRIBUTE = "message";

		/**
		 * The type of the message to be displayed. The type is used to show message in a
		 * different style.
		 */
		public static enum Type {

			DANGER, WARNING, INFO, SUCCESS;

		}

		private final String msgText;

		private final Type type;

		private final Object[] args;

		public Message(String message, Type type) {
			this.msgText = message;
			this.type = type;
			this.args = null;
		}

		public Message(String message, Type type, Object... args) {
			this.msgText = message;
			this.type = type;
			this.args = args;
		}

		public String getMsgText() {
			return msgText;
		}

		public Type getType() {
			return type;
		}

		public Object[] getArgs() {
			return args;
		}

	}
}