package zinnia.zitems.main;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import zinnia.zitems.items.ZItem;
import zinnia.zitems.player.PlayerEvents;
import zinnia.zitems.utils.Element;
import zinnia.zitems.utils.HelperFunctions;
import zinnia.zitems.utils.ListPages;
import zinnia.zitems.utils.ZCrafting;
import zinnia.zitems.utils.ZPotionEffect;

// /zitem recipe [row] [Material] [Material] [Material]


// Remember to set PlayerEvents.checkZArmor to true when we create/change an item

/*
 * 
 * !!!!!!! IMPORTANT !!!!!!!
 * ~~MAYBE~~ ADD ZENCHANT STRENGTH VALUE TO ITEM LORE
 * ~~MAYBE~~ ADD ELEMENTAL STRENGTH VALUE AND MULTIPLIERS TO ITEM LORE
 * TEST: /Zitem listCEnchs
 * TEST: /Zitem listElemStrenghs [element id]
 * TEST: /Zitem listWeakElems [element id]
 * 
 */

public class CommandHandler {

	static boolean usedNonSaveCommand = false;
	static boolean canRefreshItems = true;	
	static boolean hasPermission = false; // Used to determine if we should send a message telling the player they don't have enough perms
	
	static ListPages helpPages = new ListPages();
	static ListPages itemPages = new ListPages();
	static ListPages elementPages = new ListPages();
	
	static ListPages zEnchantPages = new ListPages();
	//static ListPages elementStrengthPages = new ListPages();
	//static ListPages elementWeakPages = new ListPages();
	
	public static void MenuCommands(ZItemMain plugin, CommandSender sender, String[] args) {
		if (sender instanceof Player) { // If the sender is a player, create a player variable
			Player player = (Player) sender;

			player.sendMessage(ChatColor.LIGHT_PURPLE + "This command is incomplete, remember to finish it when we get the menu created");
		} else {
			sender.sendMessage(ChatColor.RED + "Must be a player to use this command!");
		}
	}

	public static void ZItemCommands(ZItemMain plugin, CommandSender sender, String[] args) {
		try {
			Help(sender, true, args);
			//getZEnchants(plugin, sender, args);
			//ListZEnchants(plugin, sender, args);
			ListElementStrengths(plugin, sender, args);
			ListElementWeakness(plugin, sender, args);
			
			if (sender.hasPermission(ZItemMain.ADMIN_PERM_STR) || sender.isOp()) { // If the sender has the admin perm
				GiveItem(plugin, sender, args);
				ListItems(plugin, sender, args);
				CreateItem(plugin, sender, args);
				removeItem(plugin, sender, args);
				setMaterial(plugin, sender, args);
				setName(plugin, sender, args);
				setDurability(plugin, sender, args);
				getDurability(plugin, sender, args);
				setCombatValue(plugin, sender, args);
				setDamageTarget(plugin, sender, args);	
				setElement(plugin, sender, args);
				removeElementFromItem(plugin, sender, args);
				//setElementCombatValue(plugin, sender, args);
				setDebuffTime(plugin, sender, args);
				setVEnchant(plugin, sender, args);
				//setZEnchant(plugin, sender, args);
				
				listPassiveEffect(plugin, sender, args);
				listActiveEffect(plugin, sender, args);
				addPassiveEffect(plugin, sender, args);
				addActiveEffect(plugin, sender, args);
				removePassiveEffect(plugin, sender, args);
				removeActiveEffect(plugin, sender, args);
				
				/* Test these commands
				 * 
				 * /Zitem listPassiveEff [item id]
				 * /Zitem listActiveEff [item id] 
				 * /Zitem addPassiveEff [item id] [potion type] [strength]
				 * /Zitem addActiveEff [item id] [potion type] [strength]
				 * /ZItem removePassiveEff [item id] [effect line number]
				 * /Zitem removeActiveEff [item id] [effect line number]
				 * 
				 */
				
				addLore(plugin, sender, args);
				removeVEnchant(plugin, sender, args);
				//removeZEnchant(plugin, sender, args);
				removeLore(plugin, sender, args);
				//vGiveItem(sender, plugin.getTarget(args[1]), args);
				
				addCraftingRecipe(plugin, sender, args);
				addCraftingIngredient(plugin, sender, args);
				removeCraftingRecipe(plugin, sender, args);
				removeIngredient(plugin, sender, args);
				viewCraftingList(plugin, sender, args);
				viewRecipeIngredients(plugin, sender, args);
				
				CreateElement(plugin, sender, args);
				removeElement(plugin, sender, args);
				ListElements(plugin, sender, args);
				setElementName(plugin, sender, args);
				SetElementCombatValue(plugin, sender, args);
				SetElemEffectMult(plugin, sender, args);
				SetElemWeakMult(plugin, sender, args);
				SetElemStrengths(plugin, sender, args);
				SetElemWeaknesses(plugin, sender, args);
				removeElementStrength(plugin, sender, args);
				removeElementWeakness(plugin, sender, args);
				
				// Check if we want to save the items
				if(!usedNonSaveCommand) {
					for(Integer key : plugin.customItems.keySet()) 
						plugin.itemFile.Save(plugin.customItems.get(key));
				}
				
				// Check if we want to refresh items
				if(canRefreshItems)
					HelperFunctions.RefreshItems(plugin);
				
				// Make it so next command we can save items and refresh items
				usedNonSaveCommand = false;
				canRefreshItems = true;
			} else if(!hasPermission) {
				sender.sendMessage(ChatColor.RED + "You don't have the permission for that!");
			}
			
			hasPermission = false;
		} catch(Exception e) { // If they don't have valid arguments send the help page
			sender.sendMessage(ChatColor.RED + "No valid arguments! The commands are:");
			Help(sender, false, args);
			usedNonSaveCommand = false;
			canRefreshItems = true;
			hasPermission = false;
			e.printStackTrace();
		}
	}

	/*
	@SuppressWarnings("deprecation")
	private static void vGiveItem(CommandSender sender, Player target, String[] args) {
		if(args[0].equalsIgnoreCase("vgive")) {
			try {
				int amount = 1;
				
				if(args.length == 4) {
					amount = Integer.parseInt(args[3]);
				}
				
				target.getInventory().addItem(new ItemStack(Material.getMaterial(Integer.parseInt(args[2])), amount));
			} catch (Exception e) {
				target.getInventory().addItem(new ItemStack(Material.getMaterial(args[2]), 1));
			}
		}
	}
	*/
	
	/*
	 * 
	 * !!! HELP COMMAND !!!
	 * 
	 */
	private static void Help(CommandSender sender, boolean requireArgs, String[] args) {
		if(!requireArgs || args[0].equalsIgnoreCase("Help")) {
			usedNonSaveCommand = true;
			canRefreshItems = false;
			hasPermission = true;
			
			if(helpPages.messages.isEmpty()) {
				helpPages.pageCap = 5;
				
				helpPages.messages.add(ChatColor.GOLD + "/Zitem help | " + ChatColor.GREEN + "Displays this page");
				helpPages.messages.add(ChatColor.GOLD + "/Zitem list | " + ChatColor.GREEN + "Displays a list of all the custom items created");
				
				helpPages.messages.add(ChatColor.GOLD + "/ZItem give [target player] [item id] | " + ChatColor.GREEN + "Gives a player a custom item by ID");
				helpPages.messages.add(ChatColor.GOLD + "/Zitem create [item id] [material] [item name] | " + ChatColor.GREEN + "Creates the item");
				helpPages.messages.add(ChatColor.GOLD + "/Zitem delete [item id] | " + ChatColor.GREEN + "Delete's an item");
				helpPages.messages.add(ChatColor.GOLD + "/Zitem type [item id] [material] | " + ChatColor.GREEN + "Set's an item's material");
				helpPages.messages.add(ChatColor.GOLD + "/Zitem name [item id] [name] | " + ChatColor.GREEN + "Set's an item's name");
				helpPages.messages.add(ChatColor.GOLD + "/Zitem durability [item id] [durability] | " + ChatColor.GREEN + "Sets the durability of an item(note: also works as subdata for items with no durability)");
				helpPages.messages.add(ChatColor.GOLD + "/Zitem getDurability [item id] | " + ChatColor.GREEN + "Get's the items durability, and the durability variable(mainly for testing)");
				
				helpPages.messages.add(ChatColor.GOLD + "/Zitem [combatvalue/damage/defense] [item id] [value] | " + ChatColor.GREEN + " gives the item a combat value");
				helpPages.messages.add(ChatColor.GOLD + "/Zitem dmgTarget [item id] [true/false] | " + ChatColor.GREEN + " allows you to determine if an item will damage a player or not");
			
				helpPages.messages.add(ChatColor.GOLD + "/Zitem element [item id] [element type] | " + ChatColor.GREEN + " gives the item an elemental type");
				helpPages.messages.add(ChatColor.GOLD + "/Zitem removeElement [item id] | " + ChatColor.GREEN + " remove the item an elemental type");
				//helpPages.messages.add(ChatColor.GOLD + "/Zitem elementvalue [item id] [value] | " + ChatColor.GREEN + " gives the item an elemental combat value");
				helpPages.messages.add(ChatColor.GOLD + "/Zitem debuffTime [item id] [value] | " + ChatColor.GREEN + " gives the item the duration of a debuff, if the item has a debuff set");
				helpPages.messages.add(ChatColor.GOLD + "/Zitem ench [item id] [enchantment] [strength] | " + ChatColor.GREEN + " adds an enchantment to the item");
				//helpPages.messages.add(ChatColor.GOLD + "/Zitem listCEnchs | " + ChatColor.GREEN + "Lists all the custom enchants");
				//helpPages.messages.add(ChatColor.GOLD + "/Zitem cEnch [item id] [custom enchantment] [strength] | " + ChatColor.GREEN + " adds one of the custom enchantments to the item");
				
				helpPages.messages.add(ChatColor.GOLD + "/Zitem listPassiveEff [item id] | " + ChatColor.GREEN + "lists all the passive effects an item has");
				helpPages.messages.add(ChatColor.GOLD + "/Zitem listActiveEff [item id] | " + ChatColor.GREEN + "lists all the active effects an item has");
				helpPages.messages.add(ChatColor.GOLD + "/Zitem addPassiveEff [item id] [potion type] [strength] | " + ChatColor.GREEN + "adds a passive effect to an item");
				helpPages.messages.add(ChatColor.GOLD + "/Zitem addActiveEff [item id] [potion type] [strength] [duration] [use ticks {opt true/false}] | " + ChatColor.GREEN + "adds an active potion effect to an item");
				helpPages.messages.add(ChatColor.GOLD + "/ZItem removePassiveEff [item id] [effect line number] | " + ChatColor.GREEN + "removes a passive effect from an item");
				helpPages.messages.add(ChatColor.GOLD + "/Zitem removeActiveEff [item id] [effect line number] | " + ChatColor.GREEN + "removes an active effecft from an item");
				
				//helpPages.messages.add(ChatColor.GOLD + "/Zitem getCEnch [optional: item id] | " + ChatColor.GREEN + " displays the custom enchantments on an item");
				helpPages.messages.add(ChatColor.GOLD + "/Zitem addLore [item id] [lore] |" + ChatColor.GREEN + " adds lore to the item");
				helpPages.messages.add(ChatColor.GOLD + "/Zitem removeEnch [item id] [enchantment] [strength] | " + ChatColor.GREEN + " removes an enchantments to the item");
				//helpPages.messages.add(ChatColor.GOLD + "/Zitem removeCEnch [item id] [custom enchantment] [strength] | " + ChatColor.GREEN + " removes one of the custom enchantments to the item");
				helpPages.messages.add(ChatColor.GOLD + "/Zitem removeLore [item id] [lore line number] |" + ChatColor.GREEN + " removes lore to the item");
				
				helpPages.messages.add(ChatColor.GOLD + "/Zitem createRecipe [item id] [crafting id] [Shapeless | true/false] | " + ChatColor.GREEN + " sets a crafting system");
				helpPages.messages.add(ChatColor.GOLD + "/Zitem addIngredient [item id] [crafting system number] [slot number] [item] |" + ChatColor.GREEN + " adds an ingredient to a crafting system at a slot number");
				helpPages.messages.add(ChatColor.GOLD + "/Zitem removeRecipe [item id] [crafting system number] | " + ChatColor.GREEN + " removes a crafting system from an item");
				helpPages.messages.add(ChatColor.GOLD + "/Zitem removeIngredient [item id] [crafting system number] [slot number] | " + ChatColor.GREEN + " removes an ingredient from a crafting system");
				helpPages.messages.add(ChatColor.GOLD + "/Zitem viewCraftingList [item id] | " + ChatColor.GREEN + " views all the crafting system an item has");
				helpPages.messages.add(ChatColor.GOLD + "/Zitem viewIngredients [item id] [crafting system number] | " + ChatColor.GREEN + " views all the ingredients in a crafting system that an item has");
				
				helpPages.messages.add(ChatColor.GOLD + "/Zitem cElement [element id] [element name] | " + ChatColor.GREEN + " creates an element type");
				helpPages.messages.add(ChatColor.GOLD + "/Zitem deleteElem [element id] | " + ChatColor.GREEN + " deletes an element type");
				helpPages.messages.add(ChatColor.GOLD + "/Zitem setElementName [element id] [element name] | " + ChatColor.GREEN + " renames an element");
				helpPages.messages.add(ChatColor.GOLD + "/Zitem listElements [element id] | " + ChatColor.GREEN + " lists all elements created");
				helpPages.messages.add(ChatColor.GOLD + "/Zitem listElemStrengths [element id] [page] | " + ChatColor.GREEN + " lists all the elements an element is strong against");
				helpPages.messages.add(ChatColor.GOLD + "/Zitem listWeakElems [element id] [page] | " + ChatColor.GREEN + " lists all the elements an is weak against");
				helpPages.messages.add(ChatColor.GOLD + "/Zitem setElementCV [element id] [combat value] | " + ChatColor.GREEN + " sets an element's combat value(damage/defense, depending if the item is an armor piece)");
				helpPages.messages.add(ChatColor.GOLD + "/Zitem setElementEM [element id] [multiplier] | " + ChatColor.GREEN + " sets an element's effective multiplier");
				helpPages.messages.add(ChatColor.GOLD + "/Zitem setElementWM [element id] [multiplier] | " + ChatColor.GREEN + " sets an element's weakness multiplier");
				helpPages.messages.add(ChatColor.GOLD + "/Zitem addEffectiveElement [element id] [other element id] | " + ChatColor.GREEN + " make an element effective against another element");
				helpPages.messages.add(ChatColor.GOLD + "/Zitem addWeaknessElement [element id] [other element id] | " + ChatColor.GREEN + " make an element weak against another element");
				helpPages.messages.add(ChatColor.GOLD + "/Zitem removeEffElem [element id] [other element id] | " + ChatColor.GREEN + " removes the effective type from an element");
				helpPages.messages.add(ChatColor.GOLD + "/Zitem removeWeakElem [element id] [other element id] | " + ChatColor.GREEN + " remove a weakness from an element");
			}
			
			if(!requireArgs) {
				helpPages.SetPageMessage("Help");
				helpPages.SendMessage(sender);
			} else {
				helpPages.SetPageMessage("Help", args);
				helpPages.SendMessage(sender, args);
			}
			
		}
	}
	
	/*
	 * 
	 * !!! GIVE ITEMS !!!
	 * Give the player a custom item Command: /ZItem give [target player] [item id]
	 * 
	 */
	private static void GiveItem(ZItemMain plugin, CommandSender sender, String[] args)  {
		Integer id = -1;
		
		boolean isValidID = false;
		
		if (args[0].equalsIgnoreCase("give")) {  // If it's the give command
			usedNonSaveCommand = true;
			canRefreshItems = false;
			
			for (Integer key : plugin.customItems.keySet()) { // Loop through all of the custom items
				if (args[2].equalsIgnoreCase(key.toString())) { // If the argument is equal to the item id
					isValidID = true;
					id = key;
					break;
				} else {
					isValidID = false;
				}
			}
				
			if (isValidID) { // If the argument is equal to the item id
				Player target = plugin.getTarget(args[1], sender);
				
				plugin.customItems.get(id).GiveItem(target); // Give the item to the target
				sender.sendMessage(ChatColor.GREEN + "Gave " + target.getName() + " " + ChatColor.GOLD + plugin.customItems.get(id).itemName);
				target.sendMessage(ChatColor.GREEN + "An admin gave you " + ChatColor.GOLD + plugin.customItems.get(id).itemName);
				
			} else { // If the player doesn't give a valid item id 
				try {
					if(Integer.parseInt(args[2]) >= plugin.customItems.size() - 1) {
						sender.sendMessage(ChatColor.RED + args[2].toString() + " is an invalid Item ID!"); // Tell the sender that they gave an invalid item id
						return; // get out of this loop function thingy
					}
				} catch(NumberFormatException e) {
					sender.sendMessage(ChatColor.RED + args[2].toString() + " is an invalid Item ID!"); // Tell the sender that they gave an invalid item id
					return; // get out of this loop function thingy)
				}
			}
		}
	}
	
	/*
	 * 
	 * !!! LIST ITEMS !!!
	 * 
	 */
	private static void ListItems(ZItemMain plugin, CommandSender sender, String[] args)  {
		if(args[0].equalsIgnoreCase("list")) {
			usedNonSaveCommand = true;
			canRefreshItems = false;
			
			if(plugin.customItems.isEmpty()) {
				sender.sendMessage(ChatColor.RED + "There is no items!");
				return;
			}
			
			if(itemPages.messages.isEmpty()) {
				itemPages.pageCap = 5; // We have 5 items per page
				
				for(Integer key : plugin.customItems.keySet())
					itemPages.messages.add(ChatColor.GREEN + "Item " + key + ": " + ChatColor.GOLD + plugin.customItems.get(key).itemName);
			}
			
			itemPages.SetPageMessage("Items", args);
			itemPages.SendMessage(sender, args);
		}
	}
	
	/*
	 * 
	 * !!! ZENCHANT LIST !!!
	 * 
	 *
	private static void ListZEnchants(ZItemMain plugin, CommandSender sender, String[] args)  {
		if(args[0].equalsIgnoreCase("listCEnchants") || args[0].equalsIgnoreCase("CEnchantList") || args[0].equalsIgnoreCase("listCEnchs") || 
				args[0].equalsIgnoreCase("CEnchList") || args[0].equalsIgnoreCase("listCustomEnchants") || args[0].equalsIgnoreCase("CustomEnchantList")) {
			usedNonSaveCommand = true;
			canRefreshItems = false;
			
			if(zEnchantPages.messages.isEmpty()) {
				zEnchantPages.pageCap = 5; // We have 5 custom enchants per page
				
				for(EnchantTypes zEnch : EnchantTypes.values())
					zEnchantPages.messages.add(ChatColor.GREEN + "Custom Enchant " + ChatColor.GOLD + zEnch.toString() );
			}
			
			zEnchantPages.SetPageMessage("Custom Enchants", args);
			zEnchantPages.SendMessage(sender, args);
		}
	}*/
	
	/*
	 * 
	 * !!! ELEMENT STRENGTH LIST !!!
	 * 
	 */
	private static void ListElementStrengths(ZItemMain plugin, CommandSender sender, String[] args)  {
		if(args[0].equalsIgnoreCase("listElemStrengths") || args[0].equalsIgnoreCase("ElemStengthtList") || args[0].equalsIgnoreCase("ElementStrengthList") || 
				args[0].equalsIgnoreCase("listElementStregnths")) {
			usedNonSaveCommand = true;
			canRefreshItems = false;
			hasPermission = true;
			
			if(plugin.Elements.isEmpty()) {
				sender.sendMessage(ChatColor.RED + "There are no elements!");
				return;
			}
			
			int elementID = getElementID(plugin, sender, args[1]);
			
			if(plugin.Elements.get(elementID).effective.isEmpty()) {
				sender.sendMessage(ChatColor.RED + "Element " + ChatColor.GOLD + plugin.Elements.get(elementID).elementName + ChatColor.RED + " is not effect against any other element!");
				return;
			}
			
			plugin.Elements.get(elementID).SetupStrengthPage();
			
			plugin.Elements.get(elementID).strengthPage.SetPageMessage("Element Strengths", args);
			plugin.Elements.get(elementID).strengthPage.SendMessagePageAfterID(sender, args);
		}
	}
	
	/*
	 * 
	 * !!! ELEMENT WEAKNESS LIST !!!
	 * 
	 */
	private static void ListElementWeakness(ZItemMain plugin, CommandSender sender, String[] args)  {
		if(args[0].equalsIgnoreCase("listElemWeaknesses") || args[0].equalsIgnoreCase("listWeaknessesElems") || args[0].equalsIgnoreCase("listWeakElems") || 
				args[0].equalsIgnoreCase("listElemWeak")) {
			usedNonSaveCommand = true;
			canRefreshItems = false;
			hasPermission = true;
			
			if(plugin.Elements.isEmpty()) {
				sender.sendMessage(ChatColor.RED + "There are no elements!");
				return;
			}
			
			int elementID = getElementID(plugin, sender, args[1]);
			
			if(plugin.Elements.get(elementID).weak.isEmpty()) {
				sender.sendMessage(ChatColor.RED + "Element " + ChatColor.GOLD + plugin.Elements.get(elementID).elementName + ChatColor.RED + " has no weaknesses!");
				return;
			}
			
			
			plugin.Elements.get(elementID).SetupWeakPage();
			
			plugin.Elements.get(elementID).weakPage.SetPageMessage("Element Weaknesses", args);
			plugin.Elements.get(elementID).weakPage.SendMessagePageAfterID(sender, args);
		}
	}
	
	/*
	 * 
	 * !!! CREATE ITEM !!!
	 * 
	 */
	//@SuppressWarnings("deprecation")
	private static void CreateItem(ZItemMain plugin, CommandSender sender, String[] args)  {
		if(args[0].equalsIgnoreCase("create")) {
			canRefreshItems = false;
			
			try {
				int id = Integer.parseInt(args[1]);
					
				if(!plugin.customItems.isEmpty()) { // Make sure the item id exists
					for(Integer key : plugin.customItems.keySet()) {
						if(id == key) {
							sender.sendMessage(ChatColor.RED + "Item id: " + id + " already exists!");
							return;
						}
					}
				}
					
				if(args.length < 4) { // Make sure we have at least a one character name
					sender.sendMessage(ChatColor.RED + "Please insert the item name!");
					return;
				}
				
				
				/*
				 * ---- 1.12.2 Check ----
				 * if(isInt(args[2])) // Try to get the material through item id
				 *	 itemMaterial = Material.getMaterial(Integer.parseInt(args[2]));
				 * else  // If there's no item id try to get it through a string
				 *	 itemMaterial = Material.valueOf(args[2].toUpperCase());
				 */
				
				Material itemMaterial; // Setup our material
				itemMaterial = Material.valueOf(args[2].toUpperCase());
				
				// If the user puts an invalid item id it'll be null
				if(itemMaterial == null) { // So this checks if the material is null
					sender.sendMessage(ChatColor.RED + args[2] + " is not a valid item material id!");
					return;
				}
				
				// Check if the material is something that can't be given
				if(!isGivable(itemMaterial)) { 
					sender.sendMessage(ChatColor.RED + args[2] + " is not a givable material!");
					return;
				}
				
				// Finally after all of that
				// we can put the item into our custom item hashmap
				plugin.customItems.put(id, new ZItem(id)); 
					
				
				// This little section just get's the name
				String name = ""; 
				for(int i = 3; i < args.length; i++)
					name += " " + args[i];

				name = name.trim(); // Make sure the item name doesn't have any leading or trailing whitespace
				
				plugin.customItems.get(id).itemName = name; // Set the item's name
					
				plugin.customItems.get(id).setItem(itemMaterial, 1); // Finally create the item
				
				itemPages.messages.add(ChatColor.GREEN + "Item " + id + ": " + ChatColor.GOLD + plugin.customItems.get(id).itemName);
				
				sender.sendMessage(ChatColor.GREEN + "Item " + id + ": " + ChatColor.GOLD + name + ChatColor.GREEN + " has been created!");
				
			} catch (NumberFormatException e) {
				sender.sendMessage(ChatColor.RED + args[1] + " is not a valid number!");
			} catch(IllegalArgumentException e) {
				sender.sendMessage(ChatColor.RED + args[2] + " is not a valid item material!");
			}
		}	 
	}
	
	/*
	 * 
	 * !!! REMOVE AN ITEM !!!
	 * 
	 */
	private static void removeItem(ZItemMain plugin, CommandSender sender, String[] args)  {
		if(args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("remove")) {
			canRefreshItems = false;
			
			int id = getID(plugin, sender, args[1]);
			
			String name = plugin.customItems.get(id).itemName;
			
			if(!plugin.customItems.get(id).zCrafting.isEmpty()) 
				plugin.craftingFile.removeItemCraft(id);
			
			plugin.customItems.remove(id);
			plugin.itemFile.RemoveItem(id);
			
			for(int i = 0; i < itemPages.messages.size(); i++) {
				if(itemPages.messages.get(i).contains("Item " + id + ":"))
					itemPages.messages.remove(i);
			}
			
			PlayerEvents.removeArmor(id);
			sender.sendMessage(ChatColor.GREEN + "Item " + id + ": " + ChatColor.GOLD + name + ChatColor.GREEN + " has been deleted!");
		}
	}
	
	/*
	 * 
	 * !!! SET ITEM MATERIAL !!!
	 * 
	 */
	//@SuppressWarnings("deprecation")
	private static void setMaterial(ZItemMain plugin, CommandSender sender, String[] args)  {
		if(args[0].equalsIgnoreCase("material") || args[0].equalsIgnoreCase("type") || args[0].equalsIgnoreCase("setmaterial") || args[0].equalsIgnoreCase("settype")) {
			try { 
				int id = getID(plugin, sender, args[1]);
			
				Material itemMaterial; // Setup our material
				
				/*
				 * ---- 1.12.2 Check ----
				 * if(isInt(args[2])) // Try to get the material through item id
				 *	 itemMaterial = Material.getMaterial(Integer.parseInt(args[2]));
				 * else  // If there's no item id try to get it through a string
				 *	 itemMaterial = Material.valueOf(args[2].toUpperCase());
				 */
				
				
				itemMaterial = Material.valueOf(args[2].toUpperCase());
				
				// If the user puts an invalid item id it'll be null
				if(itemMaterial == null) { // So this checks if the material is null
					sender.sendMessage(ChatColor.RED + args[2] + " is not a valid item material id!");
					return;
				}
				
				// Check if the material is something that can't be given
				if(!isGivable(itemMaterial)) { 
					sender.sendMessage(ChatColor.RED + args[2] + " is not a givable material!");
					return;
				}
				
				plugin.customItems.get(id).setMaterial(itemMaterial);
				
				sender.sendMessage(ChatColor.GREEN + "Material " + ChatColor.AQUA + args[2] + ChatColor.GREEN + " set on item: " + 
									ChatColor.GOLD + plugin.customItems.get(id).itemName);
				
			} catch(IllegalArgumentException e) {
				sender.sendMessage(ChatColor.RED + args[2] + " is an invalid material!");
			}
		}
	}
	
	/*
	 * 
	 * !!! SET ITEM NAME !!!
	 * 
	 */
	private static void setName(ZItemMain plugin, CommandSender sender, String[] args)  {
		if(args[0].equalsIgnoreCase("name") || args[0].equalsIgnoreCase("setName")) {
			try { 
				int id = getID(plugin, sender, args[1]);
			
				if(args.length < 3) { // Make sure we have at least a one character name
					sender.sendMessage(ChatColor.RED + "Please insert the item name!");
					return;
				}
				
				// This little section just get's the name
				String name = ""; 
				for(int i = 2; i < args.length; i++)
					name += " " + args[i];

				name = name.trim(); // Make sure the item name doesn't have any leading or trailing whitespace
				
				plugin.customItems.get(id).setItemName(name); // Set the item's name
				
				sender.sendMessage(ChatColor.GREEN + "Changed item " + ChatColor.GOLD + id + ChatColor.GREEN + " to " + ChatColor.GOLD + name);
				
			} catch(Exception e) {
				sender.sendMessage(ChatColor.RED + "Error adding name!");
			}
		}
	}
	
	/*
	 * 
	 * !!! SET ITEM DURABILITY !!!
	 * 
	 */
	private static void setDurability(ZItemMain plugin, CommandSender sender, String[] args)  {
		if(args[0].equalsIgnoreCase("durability") || args[0].equalsIgnoreCase("setdurability")) {
			try {
				int id = getID(plugin, sender, args[1]);
				
				short durability = Short.parseShort(args[2]);
				short invertDurability = (short) -(durability - plugin.customItems.get(id).item.getType().getMaxDurability());
				
				plugin.customItems.get(id).setDurability(invertDurability);
				// -(plugin.customItems.get(id).durability - plugin.customItems.get(id).item.getType().getMaxDurability())
				sender.sendMessage(ChatColor.GREEN + "Durability set on: " + ChatColor.GOLD + plugin.customItems.get(id).itemName);
				
			} catch(NumberFormatException e) {
				sender.sendMessage(ChatColor.RED + args[2] + "Is not a valid durability number!");
			}
		}
	}
	
	/*
	 * 
	 * !!! GETS AN ITEM'S DURABILITY !!!
	 * 
	 */
	private static void getDurability(ZItemMain plugin, CommandSender sender, String[] args)  {
		if(args[0].equalsIgnoreCase("getdurability") || args[0].equalsIgnoreCase("getdur")) {
			usedNonSaveCommand = true;
			canRefreshItems = false;
			
			int id = getID(plugin, sender, args[1]);
			
			//sender.sendMessage(ChatColor.GOLD + plugin.customItems.get(id).itemName + "'s" + ChatColor.AQUA + " durability variable is " + plugin.customItems.get(id).durability);
			//sender.sendMessage(ChatColor.GOLD + plugin.customItems.get(id).itemName + "'s" + ChatColor.AQUA + " item durability is " + plugin.customItems.get(id).item.getDurability());
			sender.sendMessage(ChatColor.GOLD + plugin.customItems.get(id).itemName + "'s" + ChatColor.AQUA  + " Durability is " + 
					-(plugin.customItems.get(id).durability - plugin.customItems.get(id).item.getType().getMaxDurability()));
		}
	}
	
	/*
	 * 
	 * !!! SET ITEM'S COMBAT VALUE !!!
	 * 
	 */
	private static void setCombatValue(ZItemMain plugin, CommandSender sender, String[] args)  {
		if(args[0].equalsIgnoreCase("combatvalue") || args[0].equalsIgnoreCase("damage") || args[0].equalsIgnoreCase("defense") || args[0].equalsIgnoreCase("dmg")) {
			
			try {
				int id = getID(plugin, sender, args[1]);
			
				int combatValue = Integer.parseInt(args[2]);
				
				ItemMeta meta = plugin.customItems.get(id).item.getItemMeta();
				
				String lore = "" + ChatColor.GRAY + ChatColor.ITALIC + "Damage: " + args[2];
				lore = Character.toUpperCase(lore.charAt(0)) + lore.substring(1);
				
				for(int i = 0; i < plugin.customItems.get(id).itemLore.size(); i++) {
					if(plugin.customItems.get(id).itemLore.get(i).contains("" + ChatColor.GRAY + ChatColor.ITALIC + "Damage:")) {
						plugin.customItems.get(id).itemLore.remove(i);
						plugin.itemFile.RemoveLore(id, i);
					}
				}
								
				plugin.customItems.get(id).itemLore.add(lore);
				meta.setLore(plugin.customItems.get(id).itemLore);
				
				plugin.customItems.get(id).item.setItemMeta(meta);
				
				plugin.customItems.get(id).setCombatValue(combatValue);
				sender.sendMessage(ChatColor.GREEN + "Combat value set on: " + ChatColor.GOLD + plugin.customItems.get(id).itemName);
				
			} catch (NumberFormatException e) {
				sender.sendMessage(ChatColor.RED + args[2] + " is not a valid number!");
			}
		}
	}
	
	/*
	 * 
	 * !!! SET IF THE ITEM DOES DAMAGE TO OTHERS OR NOT !!!
	 * 
	 */
	private static void setDamageTarget(ZItemMain plugin, CommandSender sender, String[] args)  {
		if(args[0].equalsIgnoreCase("dmgTarget") || args[0].equalsIgnoreCase("damageTarget")) {
			int id = getID(plugin, sender, args[1]);
			
			/*
			if(args.length != 3) {
				sender.sendMessage(ChatColor.RED + " not enough arguments!");
				return;
			}
			*/
			
			plugin.customItems.get(id).dmgTarget = (args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("yes")) ? true : false;
			
			if(plugin.customItems.get(id).dmgTarget)
				sender.sendMessage(ChatColor.GOLD + plugin.customItems.get(id).itemName + ChatColor.GREEN + " can't damage players anymore!");
			else 
				sender.sendMessage(ChatColor.GOLD + plugin.customItems.get(id).itemName + ChatColor.GREEN + " can damage players!");
		}
	}
	
	/*
	 * 
	 * !!! SET ITEM'S ELEMENT VALUE !!!
	 * 
	 */
	private static void setElement(ZItemMain plugin, CommandSender sender, String[] args)  {
		if(args[0].equalsIgnoreCase("element") || args[0].equalsIgnoreCase("elementtype") || args[0].equalsIgnoreCase("setelement")) {
			
			try {
				int id = getID(plugin, sender, args[1]);
				int elementID = getElementID(plugin, sender, args[2]);
				
				// Make it so it puts the element name in the lore
				ItemMeta meta = plugin.customItems.get(id).item.getItemMeta();
				
				String lore = plugin.Elements.get(elementID).elementName.toLowerCase();
				lore = Character.toUpperCase(lore.charAt(0)) + lore.substring(1);
				
				for(int key : plugin.Elements.keySet()) {
					for(int i = 0; i < plugin.customItems.get(id).itemLore.size(); i++)  {
						if(plugin.customItems.get(id).itemLore.get(i).equalsIgnoreCase("" + ChatColor.GRAY + ChatColor.ITALIC + plugin.Elements.get(key).elementName)) {
							plugin.customItems.get(id).itemLore.remove(i);
							plugin.itemFile.RemoveLore(id, i);
						}
					}
				}
				
				plugin.customItems.get(id).itemLore.add("" + ChatColor.GRAY + ChatColor.ITALIC + lore);
				meta.setLore(plugin.customItems.get(id).itemLore);
				
				plugin.customItems.get(id).item.setItemMeta(meta);
				
				plugin.customItems.get(id).elements = plugin.Elements.get(elementID);
				
				//plugin.customItems.get(id).setElement(ElementTypes.valueOf(args[2]));
				sender.sendMessage(ChatColor.GREEN + "Elemental type " + ChatColor.DARK_AQUA + plugin.Elements.get(elementID).elementName +
						ChatColor.GREEN + " set on: " + ChatColor.GOLD + plugin.customItems.get(id).itemName);
				
			} catch (Exception e){
				sender.sendMessage(ChatColor.RED + args[2] + " is not a valid element type!");
			}
		}
	}
	
	/*
	 * 
	 * !!! REMOVE ITEM'S ELEMENT VALUE !!!
	 * 
	 */
	private static void removeElementFromItem(ZItemMain plugin, CommandSender sender, String[] args)  {
		if(args[0].equalsIgnoreCase("removeElement") || args[0].equalsIgnoreCase("emoveelementtype") || args[0].equalsIgnoreCase("removeelem")) {
			
			int id = getID(plugin, sender, args[1]);
			
			if(plugin.customItems.get(id).elements != null) {
				int elementID = plugin.customItems.get(id).elements.id;
			
				// Make it so it puts the remove name in the lore
				ItemMeta meta = plugin.customItems.get(id).item.getItemMeta();
				
				for(int i = 0; i < plugin.customItems.get(id).itemLore.size(); i++) {
					if(plugin.customItems.get(id).itemLore.get(i).equalsIgnoreCase("" + ChatColor.GRAY + ChatColor.ITALIC + plugin.Elements.get(elementID).elementName)) {
						plugin.customItems.get(id).itemLore.remove(i);
						plugin.itemFile.RemoveLore(id, i);
					}
				}
				
				meta.setLore(plugin.customItems.get(id).itemLore);
				
				plugin.customItems.get(id).item.setItemMeta(meta);
				
				plugin.customItems.get(id).elements = null;
				plugin.itemFile.RemoveElement(id);
				
				//plugin.customItems.get(id).setElement(ElementTypes.valueOf(args[2]));
				sender.sendMessage(ChatColor.GREEN + "Elemental type " + ChatColor.DARK_AQUA + plugin.Elements.get(elementID).elementName +
						ChatColor.GREEN + " removed on: " + ChatColor.GOLD + plugin.customItems.get(id).itemName);
			} else {
				sender.sendMessage(ChatColor.RED + "Item " + ChatColor.GOLD + plugin.customItems.get(id).itemName + ChatColor.RED + " doesn't have an element!");
			}
		}
	}
	
	/*
	 * 
	 * !!! SET ITEM'S ELEMENT COMBAT VALUE !!!
	 * 
	 *
	private static void setElementCombatValue(ZItemMain plugin, CommandSender sender, String[] args) {
		if(args[0].equalsIgnoreCase("elementvalue") || args[0].equalsIgnoreCase("elementdamage") || 
				args[0].equalsIgnoreCase("elementdefense") || args[0].equalsIgnoreCase("elementdmg")) {
			canRefreshItems = false;
			
			try {
				int id = getID(plugin, sender, args[1]);
				
				plugin.customItems.get(id).setElementCombatValue(Integer.valueOf(args[2]));
			} catch (Exception e) {
				sender.sendMessage(ChatColor.RED + args[2] + " is not a valid number!");
			}
		}
	}
	*/
	
	/*
	 * 
	 * !!! SET ITEM'S DEBUFF DURATION VALUE !!!
	 * 
	 */
	private static void setDebuffTime(ZItemMain plugin, CommandSender sender, String[] args)  {
		if(args[0].equalsIgnoreCase("debuffTime") || args[0].equalsIgnoreCase("debuff") || args[0].equalsIgnoreCase("debuffDuration") ||
				args[0].equalsIgnoreCase("duration")) {
			canRefreshItems = false;
			
			try {
				int id = getID(plugin, sender, args[1]);
				
				plugin.customItems.get(id).debuffTime = Integer.parseInt(args[2]);
				sender.sendMessage(ChatColor.GREEN + "Debuff duration set on: " + ChatColor.GOLD + plugin.customItems.get(id).itemName);
			} catch(Exception e) {
				sender.sendMessage(ChatColor.RED + args[2] + " is not a valid time!");
			}
		}
	}
	
	/*
	 * 
	 * !!! SET VANILLA ENCHANT !!!
	 * 
	 */
	private static void setVEnchant(ZItemMain plugin, CommandSender sender, String[] args)  {
		if(args[0].equalsIgnoreCase("enchant") || args[0].equalsIgnoreCase("ench") || args[0].equalsIgnoreCase("vanillaEnchant") || 
				args[0].equalsIgnoreCase("vanillaEnch") || args[0].equalsIgnoreCase("vEnchant") || args[0].equalsIgnoreCase("vench")) {
			try {
				int id = getID(plugin, sender, args[1]);
				
				Enchantment enchant = HelperFunctions.getEnchantByName(args[2]);
				
				if(enchant == null) {
					sender.sendMessage(ChatColor.RED + args[2] + " is an invalid encahtnment");
					return;
				}
				
				plugin.customItems.get(id).addVEnchant(sender, enchant, Integer.parseInt(args[3]));
				
				
			} catch (NumberFormatException e) {
				sender.sendMessage(ChatColor.RED + args[3] + " is an invalid strength number!");
			} 
		}
	}
	
	/*
	 * 
	 * !!! SET CUSTOM ENCHANT !!!
	 * 
	 *
	private static void setZEnchant(ZItemMain plugin, CommandSender sender, String[] args)  {
		if(args[0].equalsIgnoreCase("cEnchant") || args[0].equalsIgnoreCase("cEnch") || args[0].equalsIgnoreCase("customEnchant") || 
				args[0].equalsIgnoreCase("zEnch") || args[0].equalsIgnoreCase("zEnchant")) {
			
			try {
				int id = getID(plugin, sender, args[1]);
				
				plugin.customItems.get(id).addZEnchant(sender, EnchantTypes.valueOf(args[2].toUpperCase()), Integer.parseInt(args[3]));
				
			} catch (ArrayIndexOutOfBoundsException e) {
				sender.sendMessage(ChatColor.RED + "Not enough arguments!");
			} catch (NumberFormatException e) {
				sender.sendMessage(ChatColor.RED + args[3] + " is an invalid strength number!");
			} catch(IllegalArgumentException e) {
				sender.sendMessage(ChatColor.RED + args[2] + " is an invalid custom enchantment!");
			}
		}
	}*/
	
	/*
	 * 
	 * !!! ADD PASSIVE EFFECTS !!!
	 * 
	 */
	private static void addPassiveEffect(ZItemMain plugin, CommandSender sender, String[] args)  {
		if(args[0].equalsIgnoreCase("addPassiveEffect") || args[0].equalsIgnoreCase("addPassive") || args[0].equalsIgnoreCase("aPassive") ||
				args[0].equalsIgnoreCase("addPassiveEffect") || args[0].equalsIgnoreCase("aPEffect") || args[0].equalsIgnoreCase("addPassiveEff")) {
			try {
				int id = getID(plugin, sender, args[1]);
			
				if(!plugin.customItems.get(id).passiveEffects.isEmpty()) {
					for(ZPotionEffect pEff : plugin.customItems.get(id).passiveEffects) {
						if(pEff.CheckPotionEffect(sender, args[2])) {
							sender.sendMessage(ChatColor.GOLD + plugin.customItems.get(id).itemName + ChatColor.RED + " already has that passive effect " + args[2]);
							return;
						}
					}
				}
			
				int strength = Integer.parseInt(args[3]);
				
				ZPotionEffect effect = new ZPotionEffect(5, strength);
				effect.setEffect(sender, args[2]);
				effect.calculateDuration();
				
				if(effect.potionType != null) {
					plugin.customItems.get(id).passiveEffects.add(effect);
					
					sender.sendMessage(ChatColor.BLUE + HelperFunctions.GetPotionTypeName(effect.potionType) + ChatColor.GREEN + " passive effect has been added to " + 
							ChatColor.GOLD + plugin.customItems.get(id).itemName);
					
					PlayerEvents.checkZArmor = true;
				}
				
				//sender.sendMessage(ChatColor.BLUE + effect.potionType.toString() + ChatColor.GREEN + " passive effect has been added to " + 
					//	ChatColor.GOLD + plugin.customItems.get(id).itemName);
				
			} catch(NumberFormatException e) {
				sender.sendMessage(ChatColor.RED + args[3] + " is not a valid number");
			}
		}
	}
	
	/*
	 * 
	 * !!! ADD ACTIVE EFFECTS !!!
	 * 
	 */
	private static void addActiveEffect(ZItemMain plugin, CommandSender sender, String[] args)  {
		if(args[0].equalsIgnoreCase("addActiveEffect") || args[0].equalsIgnoreCase("addActive") || args[0].equalsIgnoreCase("aActive") ||
				args[0].equalsIgnoreCase("addActiveEffect") || args[0].equalsIgnoreCase("addActiveEff")) {
			try {
				int id = getID(plugin, sender, args[1]);
			
				if(!plugin.customItems.get(id).activeEffects.isEmpty()) {
					for(ZPotionEffect pEff : plugin.customItems.get(id).passiveEffects) {
						if(pEff.CheckPotionEffect(sender, args[2])) {
							sender.sendMessage(ChatColor.GOLD + plugin.customItems.get(id).itemName + ChatColor.RED + " already has that active effect " + args[2]);
							return;
						}
					}
				}
			
				int strength = Integer.parseInt(args[3]);
			
				int duration = 5;
				
				if(isInt(args[4])) {
					duration = Integer.parseInt(args[4]);
				} else {
					sender.sendMessage(ChatColor.RED + args[4] + " is not a valid number!");
				}
					
				boolean useTicks = (args.length == 6 && (args[5].equalsIgnoreCase("true") || args[5].equalsIgnoreCase("yes") ||
						args[5].equalsIgnoreCase("useticks") || args[5].equalsIgnoreCase("ticks"))) ? true : false;
				
				ZPotionEffect effect = new ZPotionEffect(duration, strength);
				effect.setEffect(sender, args[2]);
				effect.useTicks = useTicks;
				effect.calculateDuration();
				
				if(effect.potionType != null) {
					plugin.customItems.get(id).activeEffects.add(effect);
				
					sender.sendMessage(ChatColor.BLUE + HelperFunctions.GetPotionTypeName(effect.potionType) + ChatColor.GREEN + " active effect has been added to " + 
							ChatColor.GOLD + plugin.customItems.get(id).itemName);
				}
				
			} catch(NumberFormatException e) {
				sender.sendMessage(ChatColor.RED + args[3] + " is not a valid number");
			}
		}
	}
	
	/*
	 * 
	 * !!! REMOVE PASSIVE EFFECTS !!!
	 * 
	 */
	private static void removePassiveEffect(ZItemMain plugin, CommandSender sender, String[] args)  {
		if(args[0].equalsIgnoreCase("removePassiveEffect") || args[0].equalsIgnoreCase("removePassive") || args[0].equalsIgnoreCase("removePassiveEff") || 
				args[0].equalsIgnoreCase("removePassiveEffect") || args[0].equalsIgnoreCase("rPEffect")) {
			try {
				int id = getID(plugin, sender, args[1]);
				
				// Make sure we're not trying to remove nothing
				if(!plugin.customItems.get(id).passiveEffects.isEmpty()) {
					int effectNumber = Integer.parseInt(args[2]);
					
					// Make sure the list has the effect number, if so remove it
					if(effectNumber < plugin.customItems.get(id).passiveEffects.size() && effectNumber >= 0) {
						plugin.customItems.get(id).passiveEffects.remove(effectNumber);
						
						plugin.itemFile.RemovePassiveEffect(id, effectNumber);
						
						sender.sendMessage(ChatColor.GREEN + "Successfully removed passive effect number: " + ChatColor.BLUE + effectNumber + 
								ChatColor.GREEN + " from " + ChatColor.GOLD + plugin.customItems.get(id).itemName);
					} else {
						sender.sendMessage(ChatColor.GOLD + plugin.customItems.get(id).itemName + ChatColor.RED + " doesn't have " + ChatColor.BLUE + 
								effectNumber + ChatColor.RED + " passive effects!");
					}
				} else {
					sender.sendMessage(ChatColor.GOLD + plugin.customItems.get(id).itemName + ChatColor.RED + " has no passive effects!");
				}

				
			} catch(NumberFormatException e) {
				sender.sendMessage(ChatColor.RED + args[2] + " is not a valid number");
			}
		}
	}
	
	/*
	 * 
	 * !!! REMOVE ACTIVE EFFECTS !!!
	 * 
	 */
	private static void removeActiveEffect(ZItemMain plugin, CommandSender sender, String[] args)  {
		if(args[0].equalsIgnoreCase("removeActiveEffect") || args[0].equalsIgnoreCase("removeActive") || args[0].equalsIgnoreCase("rAEffect") || 
				args[0].equalsIgnoreCase("removeActiveEff")) {
			try {
				int id = getID(plugin, sender, args[1]);
				
				// Make sure we're not trying to remove nothing
				if(!plugin.customItems.get(id).activeEffects.isEmpty()) {
					int effectNumber = Integer.parseInt(args[2]);
					
					// Make sure the list has the effect number, if so remove it
					if(effectNumber < plugin.customItems.get(id).activeEffects.size() && effectNumber >= 0) {
						plugin.customItems.get(id).activeEffects.remove(effectNumber);
						
						plugin.itemFile.RemoveActiveEffect(id, effectNumber);
						
						sender.sendMessage(ChatColor.GREEN + "Successfully removed active effect number: " + ChatColor.BLUE + effectNumber + 
								ChatColor.GREEN + " from " + ChatColor.GOLD + plugin.customItems.get(id).itemName);
					} else {
						sender.sendMessage(ChatColor.GOLD + plugin.customItems.get(id).itemName + ChatColor.RED + " doesn't have " + ChatColor.BLUE + 
								effectNumber + ChatColor.RED + " active effects!");
					}
				} else {
					sender.sendMessage(ChatColor.GOLD + plugin.customItems.get(id).itemName + ChatColor.RED + " has no active effects!");
				}

				
			} catch(NumberFormatException e) {
				sender.sendMessage(ChatColor.RED + args[2] + " is not a valid number");
			}
		}
	}
	
	/*
	 * 
	 * !!! LIST ALL PASSIVE EFFECTS ON AN ITEM !!!
	 * 
	 */
	private static void listPassiveEffect(ZItemMain plugin, CommandSender sender, String[] args)  {
		if(args[0].equalsIgnoreCase("listPassiveEffect") || args[0].equalsIgnoreCase("listPassive") || args[0].equalsIgnoreCase("listPassiveEff") || 
				args[0].equalsIgnoreCase("listPassiveEffect") || args[0].equalsIgnoreCase("listPEffect")) {
			
			usedNonSaveCommand = true;
			canRefreshItems = false;
			
			try {
				int id = getID(plugin, sender, args[1]);
				
				// Make sure the item has passive effects
				if(!plugin.customItems.get(id).passiveEffects.isEmpty()) {
					
					sender.sendMessage(ChatColor.GOLD + plugin.customItems.get(id).itemName + ChatColor.GREEN + " has:");
					
					for(int i = 0; i < plugin.customItems.get(id).passiveEffects.size(); i++) 
						sender.sendMessage(ChatColor.GREEN + "Passive Number: " + ChatColor.BLUE + i + ChatColor.GREEN + " | Effect: " 
								+ ChatColor.BLUE + plugin.customItems.get(id).passiveEffects.get(i).potionType.getName() + ChatColor.GREEN + 
								" | Strength: " + ChatColor.BLUE + plugin.customItems.get(id).passiveEffects.get(i).strength);
					
				} else {
					sender.sendMessage(ChatColor.GOLD + plugin.customItems.get(id).itemName + ChatColor.RED + " has no passive effects!");
				}

				
			} catch(NumberFormatException e) {
				sender.sendMessage(ChatColor.RED + args[2] + " is not a valid number");
			}
		}
	}
	
	/*
	 * 
	 * !!! LIST ALL ACTIVE EFFECTS ON AN ITEM !!!
	 * 
	 */
	private static void listActiveEffect(ZItemMain plugin, CommandSender sender, String[] args)  {
		if(args[0].equalsIgnoreCase("listActiveEffect") || args[0].equalsIgnoreCase("ActivePassive") || args[0].equalsIgnoreCase("listActiveEff") || 
				args[0].equalsIgnoreCase("listActiveEffect") || args[0].equalsIgnoreCase("listAEffect")) {
			
			usedNonSaveCommand = true;
			canRefreshItems = false;
			
			try {
				int id = getID(plugin, sender, args[1]);
				
				// Make sure the item has active effects
				if(!plugin.customItems.get(id).activeEffects.isEmpty()) {
					
					sender.sendMessage(ChatColor.GOLD + plugin.customItems.get(id).itemName + ChatColor.GREEN + " has:");
					
					for(int i = 0; i < plugin.customItems.get(id).activeEffects.size(); i++) 
						sender.sendMessage(ChatColor.GREEN + "Active Number: " + ChatColor.BLUE + i + ChatColor.GREEN + " | Effect: " 
								+ ChatColor.BLUE + plugin.customItems.get(id).activeEffects.get(i).potionType.getName() + ChatColor.GREEN +
								" | Strength: " + ChatColor.BLUE + plugin.customItems.get(id).activeEffects.get(i).strength + ChatColor.GREEN + 
								" | Duration: " + ChatColor.BLUE + plugin.customItems.get(id).activeEffects.get(i).duration + ChatColor.GREEN + 
								" | Use Ticks: " + ChatColor.BLUE + plugin.customItems.get(id).activeEffects.get(i).useTicks);
					
				} else {
					sender.sendMessage(ChatColor.GOLD + plugin.customItems.get(id).itemName + ChatColor.RED + " has no active effects!");
				}

				
			} catch(NumberFormatException e) {
				sender.sendMessage(ChatColor.RED + args[2] + " is not a valid number");
			}
		}
	}
	
	/*
	 * 
	 * !!! REMOVE A VANILLA ENCHANT !!!
	 * 
	 */
	private static void removeVEnchant(ZItemMain plugin, CommandSender sender, String[] args)  {
		if(args[0].equalsIgnoreCase("removeVEnchant") || args[0].equalsIgnoreCase("removeEnchant") || args[0].equalsIgnoreCase("removeEnch") ||
				args[0].equalsIgnoreCase("removeVEnch")) {
			
			try {
				int id = getID(plugin, sender, args[1]);
			
				if(HelperFunctions.getEnchantByName(args[2]) == null) {
					sender.sendMessage(ChatColor.RED + args[2] + " is not a valid enchantment");
					return;
				}
				
				plugin.customItems.get(id).removeVEnchant(sender, HelperFunctions.getEnchantByName(args[2]));
				
				plugin.itemFile.RemoveVEnchant(id, args[2]);
				
			} catch (ArrayIndexOutOfBoundsException e) {
				sender.sendMessage(ChatColor.RED + "There is no item id or enchantment!");
			}
		}
	}
	
	/*
	 * 
	 * !!! REMOVE A CUSTOM ENCHANT !!!
	 * 
	 *
	private static void removeZEnchant(ZItemMain plugin, CommandSender sender, String[] args)  {
		if(args[0].equalsIgnoreCase("removeZEnchant") || args[0].equalsIgnoreCase("removeZEnch") || args[0].equalsIgnoreCase("removeCustomEnchant") ||
				args[0].equalsIgnoreCase("removeCustomEnch") || args[0].equalsIgnoreCase("removeCEnchant") || args[0].equalsIgnoreCase("removeCEnch") ||
				args[0].equalsIgnoreCase("zEnchantRemove") || args[0].equalsIgnoreCase("zEnchRemove") || args[0].equalsIgnoreCase("customEnchantRemove") ||
				args[0].equalsIgnoreCase("customEnchRemove") || args[0].equalsIgnoreCase("cEnchantRemove") || args[0].equalsIgnoreCase("cEnchRemove")) {
			
			try {
				int id = getID(plugin, sender, args[1]);
				
				
				plugin.customItems.get(id).removeZEnchant(plugin, sender, EnchantTypes.valueOf(args[2].toUpperCase()));
				zEnchantPages.messages.remove();
			
				plugin.itemFile.RemoveZEnchant(id, args[2].toUpperCase());
			} catch (IllegalArgumentException e) {
				sender.sendMessage(ChatColor.RED + args[2] + " is an invalid custom enchant");
			}
		}
	} */
	
	/*
	 * 
	 * !!! GET CUSTOM ENCHANTS ON AN ITEM !!!
	 * 
	 *
	private static void getZEnchants(ZItemMain plugin, CommandSender sender, String[] args)  {
		if(args[0].equalsIgnoreCase("getZEnchant") || args[0].equalsIgnoreCase("getZEnch") || args[0].equalsIgnoreCase("getCEnch") || args[0].equalsIgnoreCase("getCEnchant") 
				|| args[0].equalsIgnoreCase("getCustomEnchant") || args[0].equalsIgnoreCase("getCustomEnch")) {
			
			if(args.length > 1) { // If they supply an item id, give them the enchants by that
				int id = getID(plugin, sender, args[1]);
			
				if(!plugin.customItems.get(id).zEnchants.isEmpty()) { // Loop through all the custom enchantments in the item, and send them to the player
					for(EnchantTypes key : plugin.customItems.get(id).zEnchants.keySet()) {
						sender.sendMessage(ChatColor.GREEN + "Item " + ChatColor.GOLD + plugin.customItems.get(id).itemName + ChatColor.GREEN +
								" custom enchantment: " + ChatColor.DARK_PURPLE + key.toString().toLowerCase());
					}
				}
			} else { // If the player is holding the item in their hand
				if(sender instanceof Player) {
					Player player = (Player)sender;
					
					// Get the item in the player's hand
					ItemStack item = player.getInventory().getItem(player.getInventory().getHeldItemSlot());
					if(item != null && item.getType() != Material.AIR) { // If the item exists
						if(HelperFunctions.IsZItem(item)) { // If the item is a custom item
							String itemLine = item.getItemMeta().getLore().get(0); 
							itemLine = itemLine.substring(itemLine.length() - 3, itemLine.length() - 2);
							itemLine.trim(); // Turn the entire lore line, into "Item ID: number"
							
							// Parse the id out of the item line
							int id = Integer.parseInt(itemLine.substring(itemLine.length() - 1));
							
							// Loop through all the custom enchantments in the item, and send them to the player
							for(EnchantTypes key : plugin.customItems.get(id).zEnchants.keySet()) { 
								sender.sendMessage(ChatColor.GREEN + "Item " + ChatColor.GOLD + plugin.customItems.get(id).itemName + ChatColor.GREEN +
										" custom enchantment: " + ChatColor.DARK_PURPLE + key.toString().toLowerCase());
							}
						}
					}
				}
			}
		}
	}
	*/
	
	/*
	 * 
	 * !!! ADD LORE !!!
	 * 
	 */
	private static void addLore(ZItemMain plugin, CommandSender sender, String[] args)  {
		if(args[0].equalsIgnoreCase("addLore") || args[0].equalsIgnoreCase("lore")) {
			
			int id = getID(plugin, sender, args[1]);
			
			String lore = "";
			
			// Just get the lore at the end of the command
			for(int i = 2; i < args.length; i++)
				lore += " " + args[i];
			
			lore = lore.trim(); 
			
			plugin.customItems.get(id).addLore(lore); // Add the lore to the item
			
			sender.sendMessage(ChatColor.GREEN + "Added item lore " + ChatColor.DARK_AQUA + lore + ChatColor.GREEN + " to " + ChatColor.GOLD + plugin.customItems.get(id).itemName);
 		}
	}
	
	/*
	 * 
	 * !!! REMOVE LORE !!!
	 * 
	 */
	private static void removeLore(ZItemMain plugin, CommandSender sender, String[] args)  {
		if(args[0].equalsIgnoreCase("removeLore")) {
			
			try {
				int id = getID(plugin, sender, args[1]);

				int line = Integer.parseInt(args[2]);
				
				// Our error checks are through the remove lore, so it's a boolean
				// So we can get out of this function if there's an error, we don't want two errors
				if(plugin.customItems.get(id).removeLore(sender, line)) { 
					plugin.itemFile.RemoveLore(id, line); // Make sure the lore isn't in the file
					sender.sendMessage(ChatColor.GREEN + "Remove item lore line " + ChatColor.DARK_AQUA + line + ChatColor.GREEN + " to " + ChatColor.GOLD + plugin.customItems.get(id).itemName);
				} else {
					return;
				}
			} catch (NumberFormatException e) {
				sender.sendMessage(ChatColor.RED + args[2] + " is not a valid number!");
			}
 		}
	}
	
	private static void addCraftingRecipe(ZItemMain plugin, CommandSender sender, String[] args)  {
		if(args[0].equalsIgnoreCase("createRecipe") || args[0].equalsIgnoreCase("createCrafting") || args[0].equalsIgnoreCase("createCraft")) {
			
			usedNonSaveCommand = true;
			canRefreshItems = false;
			
			try {
				int id = getID(plugin, sender, args[1]);
				int slot = Integer.parseInt(args[2]);
			
				// Determine if the crafting system is shapeless or not
				boolean isShapeless = (args.length == 4 && (args[3].equalsIgnoreCase("true") || args[3].equalsIgnoreCase("yes") || 
						args[3].equalsIgnoreCase("shapeless"))) ? true : false;
			
				// Create the crafting system
				ZCrafting zCraft = new ZCrafting(slot);
				zCraft.setShapeless(isShapeless);
			
				// Save the file
				plugin.customItems.get(id).zCrafting.put(slot, zCraft);
				plugin.craftingFile.Save(plugin.customItems.get(id));
			
				// Give a success message
				sender.sendMessage(ChatColor.GREEN + "Gave " + ChatColor.GOLD + plugin.customItems.get(id).itemName + ChatColor.GREEN + " a new crafting system!");
				sender.sendMessage(ChatColor.GREEN + "Crafting System id: " + ChatColor.DARK_AQUA + slot);
			} catch(NumberFormatException e) {
				sender.sendMessage(ChatColor.RED + args[2] + " is not a valid number!");
			}
		}
	}
	
	private static void addCraftingIngredient(ZItemMain plugin, CommandSender sender, String[] args)  {
		if(args[0].equalsIgnoreCase("addIngredient") || args[0].equalsIgnoreCase("ingredient") || args[0].equalsIgnoreCase("setIngredient")) {
			
			usedNonSaveCommand = true;
			canRefreshItems = false;
			
			try {
				int id = getID(plugin, sender, args[1]);
				
				// Make sure the item has a crafting system
				if(!plugin.customItems.get(id).zCrafting.isEmpty()) {
					int cIndex = Integer.parseInt(args[2]);
					
					// Make sure the item has the crafting system
					if(plugin.customItems.get(id).zCrafting.containsKey(cIndex)) {
						int slot = 1;
						
						// Make sure the slot number is valid
						if(isInt(args[3])) {
							slot = Integer.parseInt(args[3]);
						} else {
							sender.sendMessage(ChatColor.RED + args[3] + " is not a invalid slot number!");
						}
						
						if(slot <= 0 && slot >= 10) {
							sender.sendMessage(ChatColor.RED + args[3] + " is not a invalid slot number!");
							return;
						}
						
						// This sets the item, the default item being a wooden sword
						/*
						 * ---- 1.12.2 Default Item ----
						 * ItemStack item = new ItemStack(Material.WOOD_SWORD, 1);
						 * 
						 * ---- 1.13.2+ Default Item ----
						 * ItemStack item = new ItemStack(Material.WOODEN_SWORD, 1);
						 */
						
						ItemStack item = new ItemStack(Material.WOODEN_SWORD, 1);
						String itemName = "wood_sword";
						
						if (isInt(args[4])) {
							if(plugin.customItems.containsKey(Integer.parseInt(args[4]))) {
								item = plugin.customItems.get(Integer.parseInt(args[4])).item;
								itemName = plugin.customItems.get(Integer.parseInt(args[4])).itemName;
							} else {
								sender.sendMessage(ChatColor.RED + args[4] + " is not a valid custom item!");
							}
						} else {
							item = new ItemStack(Material.getMaterial(args[4].toUpperCase()));
							if(item != null)
								itemName = args[4];
						}
						
						// Add the ingredient and save it to the file
						plugin.customItems.get(id).zCrafting.get(cIndex).AddIngredient(slot - 1, item);
						plugin.craftingFile.Save(plugin.customItems.get(id));
						
						sender.sendMessage(ChatColor.GREEN + "Added ingredient " + ChatColor.DARK_AQUA + itemName + 
								ChatColor.GREEN + " to " + ChatColor.GOLD + plugin.customItems.get(id).itemName + ChatColor.GREEN + " in slot " + ChatColor.DARK_AQUA + slot);
					} else {
						sender.sendMessage(ChatColor.RED + Integer.toString(cIndex) + " is not a valid crafting system number for " +
								ChatColor.GOLD + plugin.customItems.get(id).itemName); 
					}
				} else {
					sender.sendMessage(ChatColor.GOLD + plugin.customItems.get(id).itemName + ChatColor.RED + " doesn't have any crafting systems!");
					return;
				}
				
			} catch(NumberFormatException e) {
				sender.sendMessage(ChatColor.RED + args[2] + " is an invalid number!");
			} catch (IllegalArgumentException e) {
				sender.sendMessage(ChatColor.RED + args[4] + " is an invalid material");
			}
			
		}
	}
	
	private static void removeCraftingRecipe(ZItemMain plugin, CommandSender sender, String[] args)  {
		if(args[0].equalsIgnoreCase("removeRecipe") || args[0].equalsIgnoreCase("removeCrafting") || args[0].equalsIgnoreCase("removeCraftingRecipe")) {
			
			usedNonSaveCommand = true;
			canRefreshItems = false;
			
			try {
				int id = getID(plugin, sender, args[1]);
			
				// Make sure the item has a crafting system
				if(!plugin.customItems.get(id).zCrafting.isEmpty()) {
					int slot = Integer.parseInt(args[2]);
					
					// Make sure the crafting system id is valid
					if(!plugin.customItems.get(id).zCrafting.containsKey(slot)) {
						sender.sendMessage(ChatColor.RED + args[2] + " is not a valid crafting system id!");
						return;
					}
					
					// Remove the crafting system and remove it from the file
					plugin.customItems.get(id).zCrafting.remove(slot);
					plugin.craftingFile.removeCraftingSystem(id, slot);
					plugin.craftingFile.SaveFile();
					
					// Send a success message
					sender.sendMessage(ChatColor.GREEN + "Crafting slot " + ChatColor.DARK_AQUA + slot + ChatColor.GREEN + " has been removed from " +
							ChatColor.GOLD + plugin.customItems.get(id).itemName);
				} else {
					sender.sendMessage(ChatColor.RED + "There is not crafting recipe for " + ChatColor.GOLD + plugin.customItems.get(id).itemName);
				}
			} catch(NumberFormatException e) {
				sender.sendMessage(ChatColor.RED + args[2] + " is not a valid number!");
			}
		}
	}
	
	private static void removeIngredient(ZItemMain plugin, CommandSender sender, String[] args)  {
		if(args[0].equalsIgnoreCase("removeIngredient") || args[0].equalsIgnoreCase("rIngredient")) {
			
			usedNonSaveCommand = true;
			canRefreshItems = false;
			
			try {
				int id = getID(plugin, sender, args[1]);
			
				// Make sure the item has crafting systems
				if(!plugin.customItems.get(id).zCrafting.isEmpty()) {
					int slot = Integer.parseInt(args[2]);
					
					// Make sure the crafting system exists
					if(!plugin.customItems.get(id).zCrafting.containsKey(slot)) {
						sender.sendMessage(ChatColor.RED + args[2] + " is not a valid crafting system id!");
						return;
					}
					
					if(!plugin.customItems.get(id).zCrafting.get(slot).GetIngredients().isEmpty()) {
						 
						int ingredientKey = -1;
						
						// args[3] is the ingredient slot number, make sure the correct argument is a number
						if(isInt(args[3])) { 
							ingredientKey = Integer.parseInt(args[3]) - 1; 
						} else {
							sender.sendMessage(ChatColor.RED + args[3] + " is not a valid number");
							return;
						}
						
						// Make sure the ingredient exists within the crafting system
						if(!plugin.customItems.get(id).zCrafting.get(slot).GetIngredients().containsKey(ingredientKey)) {
							sender.sendMessage(ChatColor.RED + args[3] + " is not a valid ingredient slot!");
							return;
						}
						
						
						// Removes the ingredient and removes it from the file
						plugin.customItems.get(id).zCrafting.get(slot).GetIngredients().remove(ingredientKey);
						plugin.craftingFile.removeIngredient(id, slot, ingredientKey);
						plugin.craftingFile.SaveFile();
						
						// Send a success message
						sender.sendMessage(ChatColor.GREEN + "Ingredient at slot: " + ChatColor.DARK_AQUA + Integer.toString(ingredientKey + 1)
							+ ChatColor.GREEN + " from item " + ChatColor.GOLD + plugin.customItems.get(id).itemName);
					}
				} else {
					sender.sendMessage(ChatColor.RED + "There is not crafting recipe for " + ChatColor.GOLD + plugin.customItems.get(id).itemName);
				}
			} catch(NumberFormatException e) {
				sender.sendMessage(ChatColor.RED + args[2] + " is not a valid number!");
			}
		}
	}
	
	private static void viewCraftingList(ZItemMain plugin, CommandSender sender, String[] args)  {
		if(args[0].equalsIgnoreCase("viewCraftingList") || args[0].equalsIgnoreCase("vCraftingList") | args[0].equalsIgnoreCase("vCraftList") ||
				args[0].equalsIgnoreCase("vCraftL") || args[0].equalsIgnoreCase("vcl") || args[0].equalsIgnoreCase("viewCraftList")) {
			
			usedNonSaveCommand = true;
			canRefreshItems = false;
			
			int id = getID(plugin, sender, args[1]);
			
			// Make sure the item has a crafting system
			if(!plugin.customItems.get(id).zCrafting.isEmpty()) {
				sender.sendMessage(ChatColor.GREEN + "Number of crafting recipes for item: " + ChatColor.GOLD + plugin.customItems.get(id).itemName);
				
				// Send the messages, and make sure to tell the user if it's shapeless or not
				for(Integer key : plugin.customItems.get(id).zCrafting.keySet()) {
					if(plugin.customItems.get(id).zCrafting.get(key).getShapeless())
						sender.sendMessage(ChatColor.GREEN + "Crafting: " + key + " | " + ChatColor.GOLD + "is shapeless");
					else 
						sender.sendMessage(ChatColor.GREEN + "Crafting: " + key + " | " + ChatColor.GOLD + "is not shapeless");
				}
			} else {
				sender.sendMessage(ChatColor.RED + "Item: " + plugin.customItems.get(id).itemName + ChatColor.RED + " doesn't have any crafting recipes!");
			}
		}
	}
	
	private static void viewRecipeIngredients(ZItemMain plugin, CommandSender sender, String[] args)  {
		if(args[0].equalsIgnoreCase("viewIngredients") || args[0].equalsIgnoreCase("vIngredient") || args[0].equalsIgnoreCase("vIngredients")) {
			
			usedNonSaveCommand = true;
			canRefreshItems = false;
			
			try {  
				int id = getID(plugin, sender, args[1]);
			
				if(!plugin.customItems.get(id).zCrafting.isEmpty()) { // Make sure there's a crafting system
					
					int slot = Integer.parseInt(args[2]); // Get the crafting system number
				
					if(!plugin.customItems.get(id).zCrafting.containsKey(slot)) { // Make sure the item has the crafting system id
						sender.sendMessage(ChatColor.RED + args[2] + " is not a valid crafting system id!");
						return;
					}
					
					// Make sure there's ingredients 
					if(!plugin.customItems.get(id).zCrafting.get(slot).GetIngredients().isEmpty()) { 
						for(Integer key : plugin.customItems.get(id).zCrafting.get(slot).GetIngredients().keySet()) {
							
							// This is used to get the name, we check if it's a custom item, if so use that name, if not convert the material to a string and use that
							String matName = "";
							if(HelperFunctions.IsZItem(plugin.customItems.get(id).zCrafting.get(slot).GetIngredients().get(key))) 
								matName = plugin.customItems.get(id).zCrafting.get(slot).GetIngredients().get(key).getItemMeta().getDisplayName();
							else 
								matName = plugin.customItems.get(id).zCrafting.get(slot).GetIngredients().get(key).getType().toString().toLowerCase();
							
							// Send the ingredients with the slot number
							sender.sendMessage(ChatColor.GREEN + "Ingredient " + ChatColor.DARK_AQUA + matName + ChatColor.GREEN + " | slot: " + ChatColor.DARK_AQUA + 
									Integer.toString(key + 1));
						}
					} else { // If we have no ingredients
						sender.sendMessage(ChatColor.RED + "Item " + ChatColor.GOLD + plugin.customItems.get(id).itemName + ChatColor.RED + 
								" has no ingredients at slot: " + ChatColor.AQUA + slot);
					}
				} else { // If there's no crafting systems
					sender.sendMessage(ChatColor.GOLD + plugin.customItems.get(id).itemName + ChatColor.RED +  " doesn't have any crafting recipes!");
				}
			
			}  catch (NumberFormatException e) {
				sender.sendMessage(ChatColor.RED + args[2] + " is not a valid number");
			}
		}
	}
	
	/*
	 * 
	 * 
	 * 
	 * 
	 *
	 * !!!!!! ELEMENT COMMANDS !!!!!!
	 * 
	 * 
	 * 
	 *
	 * 
	 */
	
	/*
	 * 
	 * !!! Create Element !!!
	 * 
	 */
	private static void CreateElement(ZItemMain plugin, CommandSender sender, String[] args)  {
		if(args[0].equalsIgnoreCase("createElement") || args[0].equalsIgnoreCase("cElement")) {
			
			usedNonSaveCommand = true;
			canRefreshItems = false;
			
			try {
				int id = Integer.parseInt(args[1]);
				
				if(!plugin.Elements.isEmpty()) { // Make sure the element id exists
					for(Integer key : plugin.Elements.keySet()) {
						if(id == key) {
							sender.sendMessage(ChatColor.RED + "Element id: " + id + " already exists!");
							return;
						}
					}
				}
				
				String elementName = "";
			
				if(args.length < 3) { // Make sure we have at least a one character element name
					sender.sendMessage(ChatColor.RED + "Please insert the element name!");
					return;
				}
			
				// Just get the elementName at the end of the command
				for(int i = 2; i < args.length; i++)
					elementName += " " + args[i];
			
				elementName = elementName.trim(); 
			
				Element element = new Element(id);
				element.elementName = elementName;
				
				plugin.Elements.put(id, element);
			
				plugin.elementFile.Save();
			
				elementPages.messages.add(ChatColor.GREEN + "Element " + ChatColor.DARK_AQUA + id + ": " + ChatColor.GOLD + plugin.Elements.get(id).elementName);
				
				sender.sendMessage(ChatColor.GREEN + "Created element: " + ChatColor.GOLD + elementName);
			} catch (NumberFormatException e) {
				sender.sendMessage(ChatColor.RED + args[1] + "is not a valid id!");
			}
		}
	}
	
	/*
	 * 
	 * !!! REMOVE AN ITEM !!!
	 * 
	 */
	private static void removeElement(ZItemMain plugin, CommandSender sender, String[] args)  {
		if(args[0].equalsIgnoreCase("deleteElement") || args[0].equalsIgnoreCase("deleteElem")) {
			canRefreshItems = false;
			
			int id = getElementID(plugin, sender, args[1]);
			
			String name = plugin.Elements.get(id).elementName;
			
			// Delete the element
			plugin.Elements.remove(id);
			plugin.elementFile.RemoveElement(id);
			
			// If any item has this element remove it from the item
			for(Integer key : plugin.customItems.keySet()) {
				if(plugin.customItems.get(key).elements != null) {
					if(plugin.customItems.get(key).elements.id == id) {
						plugin.customItems.get(key).elements = null;
					}
				}
			}
			
			// If any element had this as a strength or weakness remove it from those lists
			for(Integer key : plugin.Elements.keySet()) {
				// Remove the element from the strength lists of other elements
				for(int i = 0; i < plugin.Elements.get(key).effective.size(); i++) {
					if(plugin.Elements.get(key).effective.get(i).equalsIgnoreCase(name)) {
						plugin.Elements.get(key).effective.remove(i);
						plugin.elementFile.RemoveElementStrength(key, i);
					}
				}
				
				// Remove the weakness from the strength lists of other elements
				for(int i = 0; i < plugin.Elements.get(key).weak.size(); i++) {
					if(plugin.Elements.get(key).weak.get(i).equalsIgnoreCase(name)) {
						plugin.Elements.get(key).weak.remove(i);
						plugin.elementFile.RemoveElementWeakness(key, i);
					}
				}
				
				// Remove the element from the strength listing page of other elements
				for(int i = 0; i < plugin.Elements.get(key).strengthPage.messages.size(); i++) {
					if(plugin.Elements.get(key).strengthPage.messages.get(i).contains(name))
						plugin.Elements.get(key).strengthPage.messages.remove(i);
				}
				
				// Remove the element from the weakness listing page of other elements
				for(int i = 0; i < plugin.Elements.get(key).weakPage.messages.size(); i++) {
					if(plugin.Elements.get(key).weakPage.messages.get(i).contains(name))
						plugin.Elements.get(key).weakPage.messages.remove(i);
				}
			}
			
			plugin.elementFile.Save(); // Save the file
			sender.sendMessage(ChatColor.GREEN + "Element " + id + ": " + ChatColor.GOLD + name + ChatColor.GREEN + " has been deleted!");
		}
	}
	
	/*
	 * 
	 * !!! SET ELEMENT NAME !!!
	 * 
	 */
	private static void setElementName(ZItemMain plugin, CommandSender sender, String[] args)  {
		if(args[0].equalsIgnoreCase("setElementName") || args[0].equalsIgnoreCase("elementName") || args[0].equalsIgnoreCase("setElemName") ||
				args[0].equalsIgnoreCase("ElemName")) {
			try { 
				int id = getElementID(plugin, sender, args[1]);
			
				if(args.length < 3) { // Make sure we have at least a one character name
					sender.sendMessage(ChatColor.RED + "Please insert the element name!");
					return;
				}
				
				// This little section just get's the name
				String name = ""; 
				for(int i = 2; i < args.length; i++)
					name += " " + args[i];

				name = name.trim(); // Make sure the element's name doesn't have any leading or trailing whitespace
				
				plugin.Elements.get(id).elementName = name;
				
				sender.sendMessage(ChatColor.GREEN + "Changed item " + ChatColor.GOLD + id + ChatColor.GREEN + " to " + ChatColor.GOLD + name);
				plugin.elementFile.Save();
				
			} catch(Exception e) {
				sender.sendMessage(ChatColor.RED + "Error adding name!");
			}
		}
	}
	
	/*
	 * 
	 * !!! SET AN ELEMENTS VALUE !!!
	 * 
	 */
	private static void SetElementCombatValue(ZItemMain plugin, CommandSender sender, String[] args)  {
		if(args[0].equalsIgnoreCase("setCElementValue") || args[0].equalsIgnoreCase("setCElementV") || args[0].equalsIgnoreCase("setElementCValue") ||
				args[0].equalsIgnoreCase("setElementCV")) {
			
			usedNonSaveCommand = true;
			canRefreshItems = false;
			
			try {
				int combatValue = Integer.parseInt(args[2]); // Get the combat value
				
				int id = getElementID(plugin, sender, args[1]);
				
				if(id != -1) { // Set the combat value
					plugin.Elements.get(id).elementValue = combatValue;
															
					plugin.elementFile.Save(); // Save the file
			
					// Send the user saying the command was successful 
					sender.sendMessage(ChatColor.GREEN + "Added combat value " + ChatColor.DARK_AQUA + combatValue + ChatColor.GREEN + 
							" to element " + ChatColor.GOLD + plugin.Elements.get(id).elementName);
				}
			} catch(NumberFormatException e) {
				sender.sendMessage(ChatColor.RED + args[2] + " is not a valid number");
			}
		}
	}
	
	/*
	 * 
	 * !!! SET AN ELEMENT'S EFFECTIVE DAMAGE MULTILPIER VALUE !!!
	 * 
	 */
	private static void SetElemEffectMult(ZItemMain plugin, CommandSender sender, String[] args)  {
		if(args[0].equalsIgnoreCase("setElementEM") || args[0].equalsIgnoreCase("setElementEffectM") || args[0].equalsIgnoreCase("setElementEffectiveMultiplier") ||
				args[0].equalsIgnoreCase("EelemEM") || args[0].equalsIgnoreCase("EEM")) {
			
			usedNonSaveCommand = true;
			canRefreshItems = false;
			
			try {
				// Get the damage multiplier
				double multiplier = Double.parseDouble(args[2]);
				
				int id = getElementID(plugin, sender, args[1]);
				
				if(id != -1) { // Set the elements damage multiplier 
					plugin.Elements.get(id).posDmgMultiplier = multiplier;
															
					plugin.elementFile.Save(); // Save the file
			
					// Send the user saying the command was successful 
					sender.sendMessage(ChatColor.GREEN + "Added effective multiplier " + ChatColor.DARK_AQUA + multiplier + ChatColor.GREEN + 
							" to element " + ChatColor.GOLD + plugin.Elements.get(id).elementName);
				}
			} catch(NumberFormatException e) {
				sender.sendMessage(ChatColor.RED + args[2] + " is not a valid number");
			}
		}
	}
	
	/*
	 * 
	 * !!! SET AN ELEMENT'S WEAKNESS DAMAGE MULTILPIER VALUE !!!
	 * 
	 */
	private static void SetElemWeakMult(ZItemMain plugin, CommandSender sender, String[] args)  {
		if(args[0].equalsIgnoreCase("setElementWM") || args[0].equalsIgnoreCase("setElementWeakM") || args[0].equalsIgnoreCase("setElementWeaknessMultiplier") ||
				args[0].equalsIgnoreCase("EelemWM") || args[0].equalsIgnoreCase("EWM")) {
			
			usedNonSaveCommand = true;
			canRefreshItems = false;
			
			try {
				// Get the defense multiplier
				double multiplier = Double.parseDouble(args[2]);
				
				int id = getElementID(plugin, sender, args[1]);
				
				if(id != -1) { // Set the elements defense multiplier
					plugin.Elements.get(id).negDmgMultiplier = multiplier;
														 	
					plugin.elementFile.Save(); // Save the file
			
					// Send the user saying the command was successful
					sender.sendMessage(ChatColor.GREEN + "Added weakness multiplier " + ChatColor.DARK_AQUA + multiplier + ChatColor.GREEN + 
							" to element " + ChatColor.GOLD + plugin.Elements.get(id).elementName);
				}
			} catch(NumberFormatException e) {
				sender.sendMessage(ChatColor.RED + args[2] + " is not a valid number");
			}
		}
	}
	
	/*
	 * 
	 * !!! ADD AN ELEMENT THIS ELEMENT IS EFFECTIVE TO !!!
	 * 
	 */
	private static void SetElemStrengths(ZItemMain plugin, CommandSender sender, String[] args)  {
		if(args[0].equalsIgnoreCase("addEffectiveElement") || args[0].equalsIgnoreCase("addEffectiveElem") || args[0].equalsIgnoreCase("addEffElem") ||
				args[0].equalsIgnoreCase("addEffElement") || args[0].equalsIgnoreCase("AEE")) {
			
			usedNonSaveCommand = true;
			canRefreshItems = false;
			
			try {
				int id = getElementID(plugin, sender, args[1]);
				
				// Get the other element to add
				int otherElemID = Integer.parseInt(args[2]);
				
				if(id != -1 && otherElemID != -1) {
					
					if(!plugin.Elements.containsKey(otherElemID)) { // If the other element doesn't exist
						sender.sendMessage(ChatColor.RED + "element doesn't exist!");
						return;
					}
					
					// Add an element that this element will be effective to
					plugin.Elements.get(id).effective.add(plugin.Elements.get(otherElemID).elementName);
					
					plugin.elementFile.Save(); // Save the file
			
					plugin.Elements.get(id).strengthPage.messages.add(ChatColor.GREEN + "Element " + ChatColor.DARK_PURPLE + plugin.Elements.get(id).elementName + 
							ChatColor.GREEN + " is effective against " + ChatColor.GOLD + plugin.Elements.get(otherElemID).elementName);
					
					// Send the user saying the command was successful
					sender.sendMessage(ChatColor.GOLD + plugin.Elements.get(id).elementName + ChatColor.GREEN + " is now effective to " + ChatColor.GOLD +
							plugin.Elements.get(otherElemID).elementName);
				}
			} catch(NumberFormatException e) {
				sender.sendMessage(ChatColor.RED + args[2] + " is not a valid number");
			}
		}
	}
	
	/*
	 * 
	 * !!! ADD AN ELEMENT THIS ELEMENT IS WEAK TO !!!
	 * 
	 */
	private static void SetElemWeaknesses(ZItemMain plugin, CommandSender sender, String[] args)  {
		if(args[0].equalsIgnoreCase("addWeaknessElement") || args[0].equalsIgnoreCase("addWeakElem") || args[0].equalsIgnoreCase("addWeaklem") ||
				args[0].equalsIgnoreCase("AWE")) {
			
			usedNonSaveCommand = true;
			canRefreshItems = false;
			
			try {				
				int id = getElementID(plugin, sender, args[1]);
				
				int otherElemID = Integer.parseInt(args[2]);
				
				if(id != -1 && otherElemID != -1) {
					
					if(!plugin.Elements.containsKey(otherElemID)) { // If the other element doesn't exist
						sender.sendMessage(ChatColor.RED + "element doesn't exist!");
						return;
					}
					
					// Add an element that this element will be weak to
					plugin.Elements.get(id).weak.add(plugin.Elements.get(otherElemID).elementName);
					
					plugin.elementFile.Save(); // Save the file
					
					plugin.Elements.get(id).weakPage.messages.add(ChatColor.GREEN + "Element " + ChatColor.DARK_PURPLE + 
							plugin.Elements.get(id).elementName + ChatColor.GREEN + " is weak against" + ChatColor.GOLD + plugin.Elements.get(otherElemID).elementName);
					
					// Send the user saying the command was successful
					sender.sendMessage(ChatColor.GOLD + plugin.Elements.get(id).elementName + ChatColor.GREEN + " is now weak to " + ChatColor.GOLD +
							plugin.Elements.get(otherElemID).elementName);
				}
			} catch(NumberFormatException e) {
				sender.sendMessage(ChatColor.RED + args[2] + " is not a valid number");
			}
		}
	}
	
	private static void removeElementStrength(ZItemMain plugin, CommandSender sender, String[] args)  {
		if(args[0].equalsIgnoreCase("removeEffectiveElement") || args[0].equalsIgnoreCase("removeEffectiveElem") || args[0].equalsIgnoreCase("removeEffElem") ||
				args[0].equalsIgnoreCase("removeEffElement") || args[0].equalsIgnoreCase("REE")) {
			
			usedNonSaveCommand = true;
			canRefreshItems = false;
			
			try {
				int id = getElementID(plugin, sender, args[1]);
				
				// Get the other element to add
				int otherElemID = Integer.parseInt(args[2]);
				
				if(id != -1 && otherElemID != -1) {
					
					// If the other element doesn't exist
					if(!plugin.Elements.containsKey(otherElemID)) { 
						sender.sendMessage(ChatColor.RED + " element doesn't exist!");
						return;
					}
				
				
					// Remove the element from any other element's strength lists
					for(int i = 0; i < plugin.Elements.get(id).effective.size(); i++) {
						if(plugin.Elements.get(id).effective.get(i).equalsIgnoreCase(plugin.Elements.get(otherElemID).elementName)) {
							plugin.Elements.get(id).effective.remove(i);
							plugin.elementFile.RemoveElementStrength(id, i);
						}
					}
				
					// Remove the element from any other element's strength page listing
					for(int i = 0; i < plugin.Elements.get(id).strengthPage.messages.size(); i++) {
						if(plugin.Elements.get(id).strengthPage.messages.get(i).contains(plugin.Elements.get(otherElemID).elementName)) 
							plugin.Elements.get(id).strengthPage.messages.remove(i);
					}
				
					sender.sendMessage(ChatColor.GREEN + "Element "  + ChatColor.GOLD + plugin.Elements.get(id).elementName + ChatColor.GREEN +
							" is no longer effective against " + ChatColor.DARK_PURPLE + plugin.Elements.get(otherElemID).elementName);
				
					plugin.elementFile.Save(); // Save the elements file
				}
			} catch(NumberFormatException e) {
				sender.sendMessage(ChatColor.RED + args[2] + " is not a valid number");
			}
		}
	}
	
	private static void removeElementWeakness(ZItemMain plugin, CommandSender sender, String[] args)   {
		if(args[0].equalsIgnoreCase("removeWeaknessElement") || args[0].equalsIgnoreCase("removeWeakElem") || args[0].equalsIgnoreCase("removeWeaklem") ||
				args[0].equalsIgnoreCase("RWE")) {
			
			usedNonSaveCommand = true;
			canRefreshItems = false;
			
			try {
				int id = getElementID(plugin, sender, args[1]);
				
				// Get the other element to add
				int otherElemID = Integer.parseInt(args[2]);
				
				if(id != -1 && otherElemID != -1) {
					
					 // If the other element doesn't exist
					if(!plugin.Elements.containsKey(otherElemID)) {
						sender.sendMessage(ChatColor.RED + " element doesn't exist!");
						return;
					}
				
				
					// Remove the element from any other element's weakness lists
					for(int i = 0; i < plugin.Elements.get(id).effective.size(); i++) {
						if(plugin.Elements.get(id).weak.get(i).equalsIgnoreCase(plugin.Elements.get(otherElemID).elementName)) {
							plugin.Elements.get(id).weak.remove(i);
							plugin.elementFile.RemoveElementWeakness(id, i);
						}
					}
				
					// Remove the element from any other element's weakness page listing
					for(int i = 0; i < plugin.Elements.get(id).strengthPage.messages.size(); i++) {
						if(plugin.Elements.get(id).weakPage.messages.get(i).contains(plugin.Elements.get(otherElemID).elementName)) 
							plugin.Elements.get(id).weakPage.messages.remove(i);
					}
								
					sender.sendMessage(ChatColor.GREEN + "Element " + ChatColor.DARK_PURPLE + plugin.Elements.get(otherElemID).elementName + ChatColor.GREEN + 
							" no longer resists " + ChatColor.GOLD + plugin.Elements.get(id).elementName);
				
					plugin.elementFile.Save(); // Save the element file
				}
			} catch(NumberFormatException e) {
				sender.sendMessage(ChatColor.RED + args[2] + " is not a valid number");
			}
		}
	}
	
	/*
	 * 
	 * !!! LIST ELEMENTS !!!
	 * 
	 */
	private static void ListElements(ZItemMain plugin, CommandSender sender, String[] args)  {
		if(args[0].equalsIgnoreCase("listElements") || args[0].equalsIgnoreCase("listElems") || args[0].equalsIgnoreCase("listElemens")
				|| args[0].equalsIgnoreCase("elementList") || args[0].equalsIgnoreCase("elemsList") || args[0].equalsIgnoreCase("elemensList")) {
			usedNonSaveCommand = true;
			canRefreshItems = false;
			
			if(elementPages.messages.isEmpty()) {
				elementPages.pageCap = 5; // We display 5 elements
				
				for(Integer key : plugin.Elements.keySet()) // Loop through the elements and display them
					elementPages.messages.add(ChatColor.GREEN + "Element " + ChatColor.DARK_AQUA + key + ": " + ChatColor.GOLD + plugin.Elements.get(key).elementName);
			}
			
			elementPages.SetPageMessage("Elements", args);
			elementPages.SendMessage(sender, args);
		}
	}
	
	/*
	 * 
	 * !!! HELPER FUNCTIONS !!!
	 * 
	 */
	private static int getID(ZItemMain plugin, CommandSender sender, String argValue) {
		try {
			int id = Integer.parseInt(argValue);
			boolean isValidID = false;
			
			for (Integer key : plugin.customItems.keySet()) { // Loop through all of the custom items
				if (id == key) { // If the argument is equal to the item id
					isValidID = true;
					id = key;
					break;
				} else {
					isValidID = false;
				}
			}
			
			if(isValidID) {
				return id;
			}
			else {
				sender.sendMessage(ChatColor.RED + "An item with the id '" + argValue + "' doesn't exist!");
				return -1;
			}
		} catch(NumberFormatException e) {
			sender.sendMessage(ChatColor.RED + argValue + " is not a valid item id!");
			return -1;
		}
	}
	
	private static int getElementID(ZItemMain plugin, CommandSender sender, String argValue) {
		try {
			int id = Integer.parseInt(argValue);
			boolean isValidID = false;
			
			for (Integer key : plugin.Elements.keySet()) { // Loop through all of the elements
				if (id == key) { // If the argument is equal to the element id
					isValidID = true;
					id = key;
					break;
				} else {
					isValidID = false;
				}
			}
			
			if(isValidID) {
				return id;
			}
			else {
				sender.sendMessage(ChatColor.RED + "An element with the id '" + argValue + "' doesn't exist!");
				return -1;
			}
		} catch(NumberFormatException e) {
			sender.sendMessage(ChatColor.RED + argValue + " is not a valid element id!");
			return -1;
		}
	}
	
	public static boolean isInt(String strToParse) {
		// Simply try to parse the string as an int
		// if it works return true, fails return false
		try { 
			Integer.parseInt(strToParse);
			return true;
		} catch(NumberFormatException e) {
			return false;
		} 
	}
	
	public static boolean isGivable(Material material) { 
		// If it's a material that can't be added, return false
		/*
		 * ---- 1.12.2 Check ----
		 * if(material == Material.WATER || material == Material.STATIONARY_WATER || material == Material.LAVA || material == Material.STATIONARY_LAVA || material == Material.FIRE
		 * 	|| material == Material.AIR)
		 */
		if(material == Material.WATER || material == Material.LAVA || material == Material.FIRE || material == Material.AIR)
			return false;
		else 
			return true;
	}
}
