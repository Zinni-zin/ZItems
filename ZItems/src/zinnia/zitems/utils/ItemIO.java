package zinnia.zitems.utils;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;

import net.md_5.bungee.api.ChatColor;
import zinnia.zitems.items.ZItem;
import zinnia.zitems.main.ItemSave;
import zinnia.zitems.main.ZItemMain;
import zinnia.zitems.player.PlayerEvents;

public class ItemIO {
	// Create lists to save our config sections to, we use a custom class so we can check item ids
	LinkedList<SectionIDPair> itemSection = new LinkedList<SectionIDPair>();
	LinkedList<SectionIDPair> vEnchantSection = new LinkedList<SectionIDPair>();
	LinkedList<SectionIDPair> zEnchantSection = new LinkedList<SectionIDPair>();
	LinkedList<SectionIDPair> loreSection = new LinkedList<SectionIDPair>();
	//LinkedList<SectionIDPair> ingredientsSection = new LinkedList<SectionIDPair>();
	LinkedList<SectionIDPair> passiveSection = new LinkedList<SectionIDPair>();
	LinkedList<SectionIDPair> activeSection = new LinkedList<SectionIDPair>();
	
	HashMap<Integer, Integer> itemCrafting =  new HashMap<Integer, Integer>();
	
	ZItemMain plugin;
	
	private ItemSave itemFile; // Of course this is our item file
		
	public ItemIO(ZItemMain plugin) {
		this.plugin = plugin;
		
		itemFile = new ItemSave(plugin); // Create the file
	}
	
	/*
	 * --- Save/Load Items ---
	 */
	public void Save(ZItem item) {
		// Save the item name, material and durability
		itemFile.config.set("Item-" + item.getItemID() + ".name", item.itemName);	
		itemFile.config.set("Item-" + item.getItemID() + ".material", item.itemMaterial.toString());
		
		itemFile.config.set("Item-" + item.getItemID() + ".Durability", item.durability);	
		
		// Save the debuff time, and combat value(damage/defense)
		itemFile.config.set("Item-" + item.getItemID() + ".Debuff-Time", item.debuffTime);	
		itemFile.config.set("Item-" + item.getItemID() + ".Item-combat-value", item.itemCombatValue);
		
		// Save the elemental type and elemental combat value(damage/defense)
		if(item.elements != null)
			itemFile.config.set("Item-" + item.getItemID() + ".Element", item.elements.id);
		//itemFile.config.set("Item-" + item.getItemID() + ".Element.Combat-value", item.elementCombatValue);	
		
		// Loop through each vanilla enchantment the item has and save that
		for(Enchantment key : item.vEnchantments.keySet()) {
			if(key != null)
				itemFile.config.set("Item-" + item.getItemID() + ".vEnchants." + HelperFunctions.getEnchantName(key), item.vEnchantments.get(key));	
		}
		
		// Loop through each custom enchantment the item has and save that
		//for(EnchantTypes key : item.zEnchants.keySet())
			//itemFile.config.set("Item-" + item.getItemID() + ".zEnchants." + key.toString(), item.zEnchants.get(key));	
		
		// Save the passive effects
		for(int i = 0; i < item.passiveEffects.size(); i++) {
			itemFile.config.set("Item-" + item.getItemID() + ".PassiveEffects." + "Line-" + i, 
					"[" + HelperFunctions.GetPotionTypeName(item.passiveEffects.get(i).potionType) + "|" + Integer.toString(item.passiveEffects.get(i).strength) + "]");
		}
		// Line-[number]: [{Potion_effect}|{strength}]
		
		// Save the active effects
		for(int i = 0; i < item.activeEffects.size(); i++) {
			itemFile.config.set("Item-" + item.getItemID() + ".ActiveEffects." + "Line-" + i, 
					"[" + HelperFunctions.GetPotionTypeName(item.activeEffects.get(i).potionType) + "|" + Integer.toString(item.passiveEffects.get(i).strength) + ";"
							+ Integer.toString(item.passiveEffects.get(i).duration) + "/" + Boolean.toString(item.passiveEffects.get(i).useTicks) + "]");
		}
		// Line-[number]: [{Potion_effect}|{strength};{duration}/{useTicks}];
	
		// Save the item's lore
		for(int i = 0; i < item.itemLore.size(); i++)
			itemFile.config.set("Item-" + item.getItemID() + ".Lore." + "Line-" + i, item.itemLore.get(i));
		
		
		
		/*
		if(!item.zCrafting.isEmpty()) {
			for(int i = 0; i < item.zCrafting.size(); i++) {
				itemFile.config.set("Item-" + item.getItemID() + ".crafting-" + i + ".shapeless", item.zCrafting.get(i).isShapeless);
				
				for(Integer key : item.zCrafting.get(i).ingredients.keySet()) {
					itemFile.config.set("Item-" + item.getItemID() + ".crafting-" + i + ".ingredients." + key, item.zCrafting.get(i).ingredients.get(key));
				}
			}
		}
		// itemFile.config.set(item.getItemID() + ".Lore." + "Line-" + i, item.itemLore.get(i));
		*/
		
		itemFile.Save();
	}
	
	public void Load() {
		int id = -1; // This is the item id we're assigning to items
		
		try { // Create an input stream and scanner, so we can control how the file is being read
			FileInputStream fs = new FileInputStream(plugin.getDataFolder() + File.separator + itemFile.GetFileName());
			Scanner s = new Scanner(fs);
			
			while(s.hasNextLine()) { 
				String line = s.nextLine();
				
								
				// If the line is a custom item
				if((line.contains("Item-") || line.contains("item-")) && !line.contains("combat")) 
				{
					// Remove the colon, it'll cause problems with the config section
					line = line.substring(0, line.indexOf(':')); 
					//System.out.println("Line contains Item- | Line: " + line);
					//System.out.println("Config Section: " + itemFile.config.getConfigurationSection(line));
					
					//if(itemFile.config.getConfigurationSection(line) != null) {
					//	System.out.println("Config section when created isn't null!");
					//}
					
					id = Integer.parseInt(line.substring(line.indexOf('-') + 1)); // Get the item id out of the current line
					itemSection.add(new SectionIDPair(itemFile.config.getConfigurationSection(line), id)); // Add the config section with the id attached to it
										
					plugin.customItems.put(id, new ZItem(id)); // add the item to the custom item hashmap
					
					//System.out.println("Item ID: " + id); // I'm bad at remembering if I need to put 1 in the index of the substring
				}
				
				// Add the vanilla enchant config sections
				if(line.contains("vEnchants")) {
					line = line.trim(); // Make sure there's no trailing or leading spaces
					line = "Item-" + id + "." + line.substring(0, line.indexOf(':')); // Remove the colon
					vEnchantSection.add(new SectionIDPair(itemFile.config.getConfigurationSection(line), id)); // add the config section with an item id
				}
				
				// Add the custom enchant config sections
				if(line.contains("zEnchants")) {
					line = line.trim(); // Make sure there's no trailing or leading spaces
					line = "Item-" + id + "." + line.substring(0, line.indexOf(':')); // Remove the colon
					zEnchantSection.add(new SectionIDPair(itemFile.config.getConfigurationSection(line), id)); // add the config section with an item id
				}
				
				// Add the lore config section
				if(line.contains("Lore") || line.contains("lore")) {
					line = line.trim(); // Make sure there's no trailing or leading spaces
					line = "Item-" + id + "." + line.substring(0, line.indexOf(':')); // Remove the colon
					loreSection.add(new SectionIDPair(itemFile.config.getConfigurationSection(line), id)); // add the config section with an item id
				}
				
				// Add the passive config section
				if(line.contains("PassiveEffects") || line.contains("passiveeffects")) {
					line = line.trim(); // Make sure there's no trailing or leading spaces
					line = "Item-" + id + "." + line.substring(0, line.indexOf(':')); // Remove the colon
					passiveSection.add(new SectionIDPair(itemFile.config.getConfigurationSection(line), id)); // add the config section with an item id
				}
				
				// Add the active config section
				if(line.contains("ActiveEffects") || line.contains("activeeffects")) {
					line = line.trim(); // Make sure there's no trailing or leading spaces
					line = "Item-" + id + "." + line.substring(0, line.indexOf(':')); // Remove the colon
					activeSection.add(new SectionIDPair(itemFile.config.getConfigurationSection(line), id)); // add the config section with an item id
				}
				
				/*
				if(line.contains("crafting")) {
					line = line.trim();
					line = "Item-" + id + "." + line.substring(0, line.indexOf(':'));
					itemCrafting.put(id, 0);
					//craftingSection.add(new SectionIDPair(itemFile.config.getConfigurationSection(line), id));
				}
				
				if(line.contains("ingredients")) {
					line = line.trim();
					line = "Item-" + id + "." + line.substring(0, line.indexOf(':'));
					ingredientsSection.add(new SectionIDPair(itemFile.config.getConfigurationSection(line), id));
				}
				*/
			}
			
			s.close(); // Close the scanner
			fs.close(); // close the file stream
			
		} catch (Exception e) {
			System.out.println("Error reading file!");
			e.printStackTrace();
		}
		
		// Fill in the item variables
		for(SectionIDPair section : itemSection) {
			itemData(section.section, section.itemID); // Do all the easy item stuff
			
			// Actually set the item so it's not null
			plugin.customItems.get(section.itemID).setItem(plugin.customItems.get(section.itemID).itemMaterial, 1, plugin.customItems.get(section.itemID).durability);
			
			addEnchants(section.itemID); // add the item enchant
			
			//plugin.customItems.get(section.itemID).zCrafting.add();
			
			/*
			for(SectionIDPair ingredient : ingredientsSection) {
				if(ingredient.itemID == section.itemID) {
					for(int i = 0; i < plugin.customItems.get(section.itemID).zCrafting.size(); i++) {
						for(String key : ingredient.section.getValues(false).keySet())
							plugin.customItems.get(ingredientsSection).zCrafting.get(i).ingredients.put(key, ingredient.section.getValues(false).get(key));
					}
				}
				//plugin.customItems.get(section.itemID).zCrafting
			}
			*/
			
			if(PlayerEvents.isArmor(plugin.customItems.get(section.itemID).item))
				PlayerEvents.checkZArmor = true; // If it's armor make sure we check it after it's loaded, so armor works
		}
	}
	
	private void itemData(ConfigurationSection config, int itemID) {
		if(config != null) {
			// Load in all the basic data
			plugin.customItems.get(itemID).itemName = config.getString("name");
			plugin.customItems.get(itemID).itemMaterial = Material.valueOf(config.getString("material"));
			plugin.customItems.get(itemID).durability = (short)config.getInt(".Durability");
			plugin.customItems.get(itemID).debuffTime = config.getInt("Debuff-Time");
			plugin.customItems.get(itemID).itemCombatValue = config.getInt("Item-combat-value");
			
			if(config.contains("Element")) {
				int elementID = config.getInt("Element");
				
				for (Integer key : plugin.Elements.keySet()) { // Loop through all of the elements
					if (elementID == key) { 
						plugin.customItems.get(itemID).elements = plugin.Elements.get(elementID);
						break;
					}
				}
			}
			
			//plugin.customItems.get(itemID).elementCombatValue = config.getInt("Element.Combat-value");
		}
	}
	
	private void addEnchants(int itemID) {
	
		// Loop through all the vanilla enchantment config sections, if they're not empty
		if(!vEnchantSection.isEmpty()) {
			for(SectionIDPair section : vEnchantSection) {
				if(itemID == section.itemID) { // Make sure the section's item id and the current one are the same
					if(section.section != null) { // make sure the section exists
						for(String key : section.section.getValues(false).keySet()) { // Add every vanilla enchantment within the section
							plugin.customItems.get(itemID).addVEnchant(HelperFunctions.getEnchantByName(key), (Integer) section.section.getValues(false).get(key));
							
						}
					}
				}
			}
		}
		
		// Passive Line-[number]: [{Potion_effect}|{strength}]
		// Loop through all the passive config sections, if they're not empty
		if(!passiveSection.isEmpty()) {
			for(SectionIDPair section : passiveSection) {
				if(itemID == section.itemID) {
					if(section.section != null) { // Make sure the section's item id and the current one are the same
						for(String key : section.section.getValues(false).keySet()) { 
							try { 
								String value = section.section.getValues(false).get(key).toString();
								value = value.trim();
								
								// Parse out the value from the file and then apply the values and add it to the item
								int strength = Integer.parseInt(value.substring(value.indexOf('|') + 1, value.indexOf(']')));
								
								ZPotionEffect effect = new ZPotionEffect(5, strength);
								effect.calculateDuration();
								effect.setEffect(value.substring(value.indexOf('[') + 1, value.indexOf('|')));
								
								plugin.customItems.get(itemID).passiveEffects.add(effect);
							} catch (Exception e) { 
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
		
		// Active  Line-[number]: [{Potion_effect}|{strength};{duration}/{useTicks}];
		// Loop through all the active config sections, if they're not empty
		if(!activeSection.isEmpty()) {
			for(SectionIDPair section : activeSection) {
				if(itemID == section.itemID) {
					if(section.section != null) { // Make sure the section's item id and the current one are the same
						for(String key : section.section.getValues(false).keySet()) { 
							try { 
								String value = section.section.getValues(false).get(key).toString();
								value = value.trim();
										
								// Parse out the value from the file
								int strength = Integer.parseInt(value.substring(value.indexOf('|') + 1, value.indexOf(';')));
								int duration = Integer.parseInt(value.substring(value.indexOf(';') + 1, value.indexOf('/')));
								
								// Figure out if this effect is using ticks or not
								String tickStr = value.substring(value.indexOf('/') + 1, value.indexOf(']'));
								boolean useTicks = (tickStr.equalsIgnoreCase("true")) ? true : false;
								
								// Apply the values then add it to the active effects
								ZPotionEffect effect = new ZPotionEffect(duration, strength);
								effect.setEffect(value.substring(value.indexOf('[') + 1, value.indexOf('|')));
								effect.useTicks = useTicks;
								
								plugin.customItems.get(itemID).activeEffects.add(effect);
							} catch (Exception e) { 
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
		
		/* Loop through all the custom enchantment config sections, if they're not empty
		if(!zEnchantSection.isEmpty()) {
			for(SectionIDPair section : zEnchantSection) {
				if(itemID == section.itemID) { // Make sure the section's item id and the current one are the same
					if(section.section != null) { // make sure the section exists
						for(String key : section.section.getValues(false).keySet()) {// Add every custom enchantment within the section
							plugin.customItems.get(itemID).zEnchants.put(EnchantTypes.valueOf(key), (Integer) section.section.getValues(false).get(key));
						}
					}
				}
			}
		}
		*/
		
		// Loop through all the lore config sections, if they're not empty
		if(!loreSection.isEmpty()) {
			for(SectionIDPair section : loreSection) {
				if(itemID == section.itemID) {
					if(section.section != null) { // Make sure the section's item id and the current one are the same
						for(String key : section.section.getValues(false).keySet()) { // Add all the lore in this section
							if(((String)section.section.getValues(false).get(key)).contains("Item ID:")) {
								plugin.customItems.get(itemID).addLore(ChatColor.DARK_PURPLE + plugin.customItems.get(itemID).itemName + " | Item ID: " +
																		section.itemID + ChatColor.WHITE);
							} else { 
								plugin.customItems.get(itemID).addLore((String) section.section.getValues(false).get(key));
							}
						}
					}
				}
			}
		}
	}
	
	public void RemoveItem(int itemID) {
		itemFile.config.set("Item-" + Integer.toString(itemID), null);
	}
	
	public void RemoveVEnchant(int itemID, String enchantName) {
		itemFile.config.set("Item-" + Integer.toString(itemID) + ".vEnchants." + enchantName, null);
	}
	
	public void RemoveZEnchant(int itemID, String customEnchantName) {
		itemFile.config.set("Item-" + Integer.toString(itemID) + ".zEnchants." + customEnchantName, null);
	}
	
	public void RemovePassiveEffect(int itemID, int lineNumber) {
		itemFile.config.set("Item-" + Integer.toString(itemID) + ".PassiveEffects.Line-" + Integer.toString(lineNumber), null);
	}
	
	public void RemoveActiveEffect(int itemID, int lineNumber) {
		itemFile.config.set("Item-" + Integer.toString(itemID) + ".ActiveEffects.Line-" + Integer.toString(lineNumber), null);
	}
	
	public void RemoveLore(int itemID, int lineNumber) {
		itemFile.config.set("Item-" + Integer.toString(itemID) + ".Lore.Line-" + Integer.toString(lineNumber), null);
	}
	
	public void RemoveElement(int itemID) {
		itemFile.config.set("Item-" + Integer.toString(itemID) + ".Element", null);
	}

}
