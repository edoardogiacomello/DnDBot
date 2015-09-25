package it.unozerouno.dndbot;

import it.unozerouno.dndbot.CommandParser.BotCommands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import com.edoardogiacomello.telegrambot.main.TelegramBot;
import com.edoardogiacomello.telegrambot.main.TelegramEvents;
import com.edoardogiacomello.telegrambot.types.Message;
import com.edoardogiacomello.telegrambot.types.Update;

public class DnDBot extends TelegramEvents {
	 private TelegramBot bot;
		private long pollingTime = 1500;
		
		public static void main(String[] args) {
			if (args.length < 1)
				throw new IllegalArgumentException(
						"Please, specify at least the auth token using -t <token>");
			if (args[0].equals("-t")) {
				DnDBot bot = new DnDBot();
				bot.startBot(args[1]);
			}

}
		private String rollDices(String argument){
			int numberOfDices = 1;
			int numberOfFaces = 6;
			String error = "Sorry, I can't understand what you asked for.";
			StringBuffer result = new StringBuffer();
			int sum = 0;
			final int threshold = 20;
			ArrayList<Integer> diceResults = new ArrayList<Integer>();
			String judgement = "";
			
			argument = argument.toLowerCase();
			try{
			String[] values = argument.split("d");
			if(!values[0].equals("")){numberOfDices = Integer.parseInt(values[0]);}
			numberOfFaces = Integer.parseInt(values[1]);
			for (int i = 0; i < numberOfDices; i++) {
				int dice = rollDice(1,numberOfFaces);
				if(numberOfDices <= threshold) {diceResults.add(dice);}
				sum += dice; 
			}
			//Printing single dice results
			if((numberOfDices >= 0)&&(numberOfDices <= threshold) ){
				result.append("\n Rolls: ");
				for (int dice : diceResults) {
					result.append(dice + ", ");
				}
				//Removes last ", "
				result.delete(result.length()-2, result.length());
			}
			//judgement:
			judgement = "";
			Float max = (float)(numberOfFaces*numberOfDices);
			Float min = (float) numberOfDices;
			Float rating = (((float)sum) - min)/(max-min);
			Integer ratingp = (int)(rating*100f);
			if (rating >= 0.8) judgement = "\n" + ratingp.toString() + "%: Oustanding rolls!";
			else if (rating >= 0.6) judgement = "\n" + ratingp.toString() + "%: Good rolls!";
			else if (rating >= 0.4) judgement = "\n" + ratingp.toString() + "%: Average rolls..";
			else if (rating >= 0.2) judgement = "\n" + ratingp.toString() + "%: Bad rolls!";
			else if (rating >= 0) judgement = "\n" + ratingp.toString() + "%: Auful rolls!";
			
			
			result.append(judgement);
			result.append("\n Result: " + Integer.toString(sum));
			}
			catch (Exception e){
				return error;
				}
			return result.toString();
			
		}
		
		private int rollDice(int nDices, int nFaces){
			int result = 0;
			for (int i = 0; i < nDices; i++) {
				result += ThreadLocalRandom.current().nextInt(1, nFaces + 1);
			}
			return result;
		}
		private String generateStatsPC(){
			StringBuffer output = new StringBuffer();
			int[] stats = new int[6];
			
			//rolling stats
			for (int i = 0; i < 6; i++) {
				int[] rolls = new int[4];
				rolls[0] = rollDice(1,6);
				rolls[1] = rollDice(1,6);
				rolls[2] = rollDice(1,6);
				rolls[3] = rollDice(1,6);
				Arrays.sort(rolls);
				stats[i] = rolls[1] + rolls[2] + rolls[3];
			}
			
			output.append("\nSTR: " + stats[0] + " [" + getModifier(stats[0]) + "]");
			output.append("\nDEX: " + stats[1] + " [" + getModifier(stats[1]) + "]");
			output.append("\nCON: " + stats[2] + " [" + getModifier(stats[2]) + "]");
			output.append("\nINT: " + stats[3] + " [" + getModifier(stats[3]) + "]");
			output.append("\nWIS: " + stats[4] + " [" + getModifier(stats[4]) + "]");
			output.append("\nCHA: " + stats[5] + " [" + getModifier(stats[5]) + "]");
			return output.toString();
		}
		private String generateStatsAverage(){
			StringBuffer output = new StringBuffer();
			int[] stats = new int[6];
			
			//rolling stats
			for (int i = 0; i < 6; i++) {
				stats[i] = rollDice(3,6);
			}
			
			output.append("\nSTR: " + stats[0] + " [" + getModifier(stats[0]) + "]");
			output.append("\nDEX: " + stats[1] + " [" + getModifier(stats[1]) + "]");
			output.append("\nCON: " + stats[2] + " [" + getModifier(stats[2]) + "]");
			output.append("\nINT: " + stats[3] + " [" + getModifier(stats[3]) + "]");
			output.append("\nWIS: " + stats[4] + " [" + getModifier(stats[4]) + "]");
			output.append("\nCHA: " + stats[5] + " [" + getModifier(stats[5]) + "]");
			return output.toString();
		}
		
		private String getModifier(int stat){
			double fractionalMod = ((double)stat - 10)/2d;
			long integerMod = (long)fractionalMod;
			return Long.toString(integerMod);
		}
		
		private void startBot(String token) {
			if (token.isEmpty()) {
				System.out
						.println("Auth token cannot be empty. Please specify at least the auth token, with -t <token>");
				return;
			}

			
			//Initializing and starting the bot
			bot = new TelegramBot(token, true);
			bot.registerForUpdates(this);
			bot.startPolling(pollingTime);
			
			
			//Waiting an input
			Scanner scanner = new Scanner(System.in);
			try {
				System.out.println("Bot running, press enter to exit.");
				scanner.nextLine();

			} finally {
				scanner.close();
				bot.stopPolling();
			}

		}

		@Override
		public void onUpdate(Update update) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onMessageReceived(Message message) {
			String input = message.getText();
			System.out.println("got message");
			if (input == null) return;
			//parsing commands
			if (input.startsWith("/")){
				BotCommands command = CommandParser.parseCommand(input);
				String reply = "";
				switch (command) {
				case ROLL:
					if (command.getArgument() == null || command.getArgument().equals("")){
						reply = "please, specify what dice to roll, i.e. /roll 3d6";
					break;
					}
					reply = rollDices(command.getArgument());
					break;
				case STATS_NPC:
					reply = generateStatsAverage();
					break;
				case STATS_CHARACTER:
					reply = generateStatsPC();
					break;
				case NOT_A_COMMAND:
					reply = "What?";
					break;
				default:
					break;
				}
				bot.sendMessage(message.getChat().getChatId(), reply, false, message.getMessageId(), null);
			} 			
		}
}
