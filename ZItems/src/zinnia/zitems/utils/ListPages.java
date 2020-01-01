package zinnia.zitems.utils;

import java.util.LinkedList;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ListPages {
	public LinkedList<String> messages = new LinkedList<String>();
	public String pageMessage = ChatColor.GOLD + "Page: ";
	
	public int pageCap = 0;
	
	public boolean useDivider = true;
	
	public void SendMessage(CommandSender sender, String[] args) {
		int page = 1; // Create a page variable to determine the page we're getting (default is 1)
		
		if(args.length <= 1) { // If there is no argument send the first page
			page = 1;
		}
		else { // Otherwise send the page with the number
			try {
				page = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				sender.sendMessage(ChatColor.RED + args[1] + " is not a valid number!");
				return;
			}
		}
		
		// Get the number to start the page on 
		int startingIndex = pageCap * (page - 1); 
		
		if(messages.isEmpty()) { // is there's no messages print out an error
			System.out.println("Error! there are no messages!");
			return;
		}
		
		if(pageCap <= 0) { // If there's no page cap
			System.out.println("Error! There is no page cap!");
			return;
		}
			
		// Check if this is a valid page number
		if(startingIndex > messages.size() - 1 || startingIndex < 0) {
			sender.sendMessage(ChatColor.RED + "Invalid page number!");
			return;
		} else { // If it's a valid page number, start the loop at the starting index
			for(int i = startingIndex; i <= (pageCap * page) - 1; i++) { // Then loop if the index is less than the page cap
				if(i < messages.size()) // If the index is less than the size of messages(so we don't get an error)
					sender.sendMessage(messages.get(i)); 
			}
		}
		
		// Simply send the message, that tells the user what page they're on out of the total pages
		sender.sendMessage(pageMessage);
		if(useDivider) // if this is using a divider, just send a bunch of dashes
			sender.sendMessage(ChatColor.GOLD + "-----------------");
	}
	
	public void SendMessagePageAfterID(CommandSender sender, String[] args) {
		int page = 1; // Create a page variable to determine the page we're getting (default is 1)
		
		if(args.length <= 2) { // If there is no argument send the first page
			page = 1;
		}
		else { // Otherwise send the page with the number
			try {
				page = Integer.parseInt(args[2]);
			} catch (NumberFormatException e) {
				sender.sendMessage(ChatColor.RED + args[2] + " is not a valid number!");
				return;
			}
		}
		
		// Get the number to start the page on 
		int startingIndex = pageCap * (page - 1); 
		
		if(messages.isEmpty()) { // is there's no messages print out an error
			System.out.println("Error! there are no messages!");
			return;
		}
		
		if(pageCap <= 0) { // If there's no page cap
			System.out.println("Error! There is no page cap!");
			return;
		}
			
		// Check if this is a valid page number
		if(startingIndex > messages.size() - 1 || startingIndex < 0) {
			sender.sendMessage(ChatColor.RED + "Invalid page number!");
			return;
		} else { // If it's a valid page number, start the loop at the starting index
			for(int i = startingIndex; i <= (pageCap * page) - 1; i++) { // Then loop if the index is less than the page cap
				if(i < messages.size()) // If the index is less than the size of messages(so we don't get an error)
					sender.sendMessage(messages.get(i)); 
			}
		}
		
		// Simply send the message, that tells the user what page they're on out of the total pages
		sender.sendMessage(pageMessage);
		if(useDivider) // if this is using a divider, just send a bunch of dashes
			sender.sendMessage(ChatColor.GOLD + "-----------------");
	}
	
	public void SendMessage(CommandSender sender) {
		int page = 1; // Create a page variable to determine the page we're getting (default is 1)
		
		// Get the number to start the page on 
		int startingIndex = pageCap * (page - 1); 
		
		if(messages.isEmpty()) { // is there's no messages print out an error
			System.out.println("Error! there are no messages!");
			return;
		}
		
		if(pageCap <= 0) { // If there's no page cap
			System.out.println("Error! There is no page cap!");
			return;
		}
			
		// Check if this is a valid page number
		if(startingIndex > messages.size() - 1 || startingIndex < 0) {
			sender.sendMessage(ChatColor.RED + "Invalid page number!");
			return;
		} else { // If it's a valid page number, start the loop at the starting index
			for(int i = startingIndex; i <= (pageCap * page) - 1; i++) { // Then loop if the index is less than the page cap
				if(i < messages.size()) // If the index is less than the size of messages(so we don't get an error)
					sender.sendMessage(messages.get(i)); 
			}
		}
		
		// Simply send the message, that tells the user what page they're on out of the total pages
		sender.sendMessage(pageMessage);
		if(useDivider) // if this is using a divider, just send a bunch of dashes
			sender.sendMessage(ChatColor.GOLD + "-----------------");
	}
	
	public int GetMaxPages() {
		int maxPages = 1; // Create a max page variable, and set it to a default of 1
		
		if(pageCap == 0) pageCap = 5;
		
		maxPages = messages.size() / pageCap; // Get the max amount of pages
		
		if(maxPages <= 0) // If there's no max pages
			return 1;
		
		// If there's a remainder increment the max pages by 1
		if(messages.size() % pageCap != 0) {
			maxPages++;
		}
		
		return maxPages; 
	}
	
	public void SetPageMessage(String pageType, String[] args) {
		try {
			int currentPage = 1;  // A variable for the current page we're on, default is 1
			
			if(!(args.length <= 1)) // If the first argument doesn't exist
				currentPage = Integer.parseInt(args[1]); // set the current page to the first argument
			
			// Set the message
			pageMessage =  ChatColor.GOLD + pageType + " Pages: " + ChatColor.DARK_AQUA + currentPage + "/" + GetMaxPages();
		} catch(NumberFormatException e) {
			return;
		}
	}
	
	public void SetPageMessage(String pageType) {
		pageMessage = ChatColor.GOLD + pageType + " Pages: " + ChatColor.DARK_AQUA + "1/" + GetMaxPages();
	}
}
