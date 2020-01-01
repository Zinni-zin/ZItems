package zinnia.zitems.utils;

import java.io.File;
import java.io.FileInputStream;
import java.util.LinkedList;
import java.util.Scanner;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import zinnia.zitems.items.ZItem;
import zinnia.zitems.main.CommandHandler;
import zinnia.zitems.main.CraftingFile;
import zinnia.zitems.main.ZItemMain;

public class CraftingIO {
	LinkedList<SectionIDPair> itemSection = new LinkedList<SectionIDPair>();
	LinkedList<SectionIDPair> craftingSection = new LinkedList<SectionIDPair>();
	LinkedList<SectionIDPair> ingredientSection = new LinkedList<SectionIDPair>();
	
	ZItemMain plugin;
	
	private CraftingFile craftingFile; // Of course this is our item file
		
	public CraftingIO(ZItemMain plugin) {
		this.plugin = plugin;
		
		craftingFile = new CraftingFile(plugin); // Create the file
	}
	
	public void Save(ZItem item) {
		if(!item.zCrafting.isEmpty()) {
			for(Integer craftingKey : item.zCrafting.keySet()) {
				
				// Save the crafting system
				craftingFile.config.set("Item-Recipes-" + Integer.toString(item.getItemID()) + ".Crafting-" + craftingKey + ".shapeless",
						item.zCrafting.get(craftingKey).isShapeless);
				
				// Save the ingredients
				for(Integer key : item.zCrafting.get(craftingKey).ingredients.keySet()) {
					// If the ingredient is a zitem save it as the id, otherwise save the material name
					if(HelperFunctions.IsZItem(item.zCrafting.get(craftingKey).ingredients.get(key))) {
						int otherID = HelperFunctions.GetZItemID(item.zCrafting.get(craftingKey).ingredients.get(key));
						
						craftingFile.config.set("Item-Recipes-" + Integer.toString(item.getItemID()) + ".Crafting-" + craftingKey + ".ingredients.slot-" + key, Integer.toString(otherID));
					} else {
						craftingFile.config.set("Item-Recipes-" + Integer.toString(item.getItemID()) + ".Crafting-" + craftingKey + ".ingredients.slot-" + key, 
								item.zCrafting.get(craftingKey).ingredients.get(key).getType().toString());
					}
				}
			}
			
			craftingFile.Save();
		}
	}
	
	public void Load() {
		int id = -1; // Item id we're assign crafting recipes to
		int craftingNumber = -1;
		
		try { // Create an input stream and scanner, so we can control how the file is being read
			FileInputStream fs = new FileInputStream(plugin.getDataFolder() + File.separator + craftingFile.GetFileName());
			Scanner s = new Scanner(fs);
			
			while(s.hasNextLine()) { 
				String line = s.nextLine();
								
				// Setup the item section
				if(line.contains("Item-Recipes-"))  {
					line = line.substring(0, line.indexOf(':')); 
					
					id = Integer.parseInt(line.substring(line.lastIndexOf('-') + 1)); // Get the item id out of the current line
					
					itemSection.add(new SectionIDPair(craftingFile.config.getConfigurationSection(line), id)); // Add the config section with the id attached to it
				}
				
				if(line.contains("Crafting-")) {
					line = line.trim();
					
					// Get the crafting system id
					craftingNumber = Integer.parseInt(line.substring(line.indexOf('-') + 1, line.indexOf(':')));
					
					line = "Item-Recipes-" + id + "." + line.substring(0, line.indexOf(':'));

					// Setup the crafting system section
					SectionIDPair section = new SectionIDPair(craftingFile.config.getConfigurationSection(line), id);
					section.miscID = craftingNumber;
					
					craftingSection.add(section);
					
					//System.out.println("!!! Crafting Line " + line + " !!!");
					//System.out.println("!!! Crafting Section: " + section.section + " !!!");
					
				}
				
				if(line.contains("ingredients")) {
					line = line.trim();
					line = "Item-Recipes-" + id + ".Crafting-" + craftingNumber + "." + line.substring(0, line.indexOf(':'));
					
					// Setup the ingredient section
					SectionIDPair section = new SectionIDPair(craftingFile.config.getConfigurationSection(line), id);
					section.miscID = craftingNumber;
					
					ingredientSection.add(section);
					
					//System.out.println("!!! Ingredient Line: " + line + " !!!");
					//System.out.println("!!! Ingredient Section: " + section.section + " !!!");
					
				}
			}
			
			s.close(); // Close the scanner
			fs.close(); // close the file stream
			
		} catch (Exception e) {
			System.out.println("Error reading file!");
			e.printStackTrace();
		}
		
		for(SectionIDPair section : itemSection) {
			//plugin.customItems.get(section.itemID).zCrafting.put(craftingNumber, new ZCrafting(craftingNumber));
			
			// Add the crafting system onto an item
			for(SectionIDPair craftingSection : craftingSection)
				if(section.itemID == craftingSection.itemID)
					plugin.customItems.get(section.itemID).zCrafting.put(craftingSection.miscID, new ZCrafting(craftingSection.miscID));
			
			// Determine if the recipe is shapeless
			loadCraftingSection(section.itemID);
			
			// Load in the ingredients
			for(SectionIDPair craftingSection : craftingSection)
				loadIngredientSection(section.itemID, craftingSection.miscID);
		}
	}
	
	// This simply determines if the crafting system is a shapeless recipe or not
	private void loadCraftingSection(int itemID) {
		for(SectionIDPair craftSection : craftingSection) {
			if(itemID == craftSection.itemID) {
				for(Integer key : plugin.customItems.get(itemID).zCrafting.keySet()) {
					if(craftSection.miscID == key)
						plugin.customItems.get(itemID).zCrafting.get(key).setShapeless(craftSection.section.getBoolean("shapeless"));
				}
			}
		}
	}
	

	private void loadIngredientSection(int itemID, int craftID) {
		// Make sure the item, and crafting system ids match up
		for(SectionIDPair ingredientSection : ingredientSection) {
			if(itemID == ingredientSection.itemID) {
				if(craftID == ingredientSection.miscID) {
					//System.out.println("!!! Before the null check! !!!");
					if(ingredientSection.section != null) {
						//System.out.println("!!! After the null check !!!");
						
						// Get the values in the file starting from the ingredient section
						for(String key : ingredientSection.section.getValues(false).keySet()) {
							String tempKey = key.trim(); 
							
							//System.out.println("\n\n------------------------------");
							//System.out.println("Temp Key: " + key);
							
							// Get the slot number from the string "slot-[number]". Then get the ingredient in string form
							int slotNum = Integer.parseInt(tempKey.substring(tempKey.lastIndexOf('-') + 1));
							String ingredient = (String)ingredientSection.section.getValues(false).get(key); 
							
							//System.out.println("Slot Number: " + slotNum);
							
							// If the ingredient is an integer consider it a zItem, otherwise consider it a normal material
							if(CommandHandler.isInt(ingredient)) {
								int zIngredient = Integer.parseInt(ingredient);
						
								if(plugin.customItems.containsKey(zIngredient))
									plugin.customItems.get(itemID).zCrafting.get(craftID).ingredients.put(slotNum, plugin.customItems.get(zIngredient).item);
								else return;
							} else {
								try {
									plugin.customItems.get(itemID).zCrafting.get(craftID).ingredients.put(slotNum, new ItemStack(Material.getMaterial(ingredient)));
								} catch (IllegalArgumentException e) {
									e.printStackTrace();
									return;
								}
							}
							
							//System.out.println("\n\n------------------------------\n\n");
						}
					}
				}
			}
		}
	} 
	
	public void removeItemCraft(int itemID) {
		craftingFile.config.set("Item-Recipes-" + Integer.toString(itemID), null);
	}
	
	public void removeCraftingSystem(int itemID, int systemNum) {
		craftingFile.config.set("Item-Recipes-" + Integer.toString(itemID) + ".Crafting-" + systemNum, null);
	}
	
	public void removeIngredient(int itemID, int systemNum, int slotNum) {
		craftingFile.config.set("Item-Recipes-" + Integer.toString(itemID) + ".Crafting-" + Integer.toString(systemNum) + ".ingredients.slot-" + Integer.toString(slotNum), null);
	}
	
	public void SaveFile() {
		craftingFile.Save();
	}
}
