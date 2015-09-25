package it.unozerouno.dndbot;


public class CommandParser {
	private final static String groupCommand = "@DnDBot";
	public enum BotCommands{
		ROLL("/roll"),
		STATS_NPC("/stats_npc"),
		STATS_CHARACTER("/stats_pc"),
		NOT_A_COMMAND("/error");
		private String value;
		private String argument;
		private BotCommands(String value) {
			this.value = value;
		}
		private BotCommands(String value, String argument) {
			this.value = value;
			this.argument = argument;
		}
		public void setArgument(String argument) {
			this.argument = argument;
		}
		public String getValue() {
			return value;
		}
		public String getArgument() {
			return argument;
		}
	}
	
	public static BotCommands parseCommand(String message){
		for (BotCommands command : BotCommands.values()) {
 			if ((command != BotCommands.NOT_A_COMMAND) && (message.startsWith(command.getValue()) || message.startsWith(command.getValue() + groupCommand))){
				command.setArgument(getArgument(command,message));
				return command;
			}
		}
		return BotCommands.NOT_A_COMMAND;
	}

	public static String getArgument(BotCommands command, String input) {
		if(input.startsWith(command.getValue()+" ")) return input.substring(command.getValue().length() + " ".length());
		if(input.startsWith(command.getValue()+groupCommand)) return input.substring(command.getValue().length() + groupCommand.length());
		return input;
	}
	
	
}