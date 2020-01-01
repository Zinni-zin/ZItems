package zinnia.zitems.utils;

import java.util.LinkedList;

import net.md_5.bungee.api.ChatColor;

public class Element {
	public String elementName = "Base Element";
	public LinkedList<String> effective = new LinkedList<String>();
	public LinkedList<String> weak = new LinkedList<String>();
	
	public ListPages strengthPage = new ListPages();
	public ListPages weakPage = new ListPages();
	
	public int id = -1;
	
	public int elementValue = 0;
	public double posDmgMultiplier = 1;
	public double negDmgMultiplier = 1;
	
	public Element(int id) {
		this.id = id;
	}
	
	public void SetupStrengthPage() {
		if(strengthPage.messages.isEmpty()) {
			strengthPage.pageCap = 5; // We have 5 weaknesses per page
			
			for(String strength : effective)
				strengthPage.messages.add(ChatColor.GREEN + "Element " + ChatColor.DARK_PURPLE + elementName + 
						ChatColor.GREEN + " is effective against " + ChatColor.GOLD + strength);
		}
	}
	
	public void SetupWeakPage() {
		if(weakPage.messages.isEmpty()) {
			weakPage.pageCap = 5; // We have 5 weaknesses per page
			
			for(String weakness : weak)
				weakPage.messages.add(ChatColor.GREEN + "Element " + ChatColor.DARK_PURPLE + elementName + ChatColor.GREEN + " is weak against" + ChatColor.GOLD + weakness);
		}
	}
}
