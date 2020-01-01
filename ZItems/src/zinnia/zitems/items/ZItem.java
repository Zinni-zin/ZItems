package zinnia.zitems.items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.md_5.bungee.api.ChatColor;
import zinnia.zitems.main.ZItemMain;
import zinnia.zitems.utils.Element;
import zinnia.zitems.utils.EnchantTypes;
import zinnia.zitems.utils.HelperFunctions;
import zinnia.zitems.utils.ZCrafting;
import zinnia.zitems.utils.ZPotionEffect;

public class ZItem {

	// These deal with our enchantments and elemental effects
	//public ElementTypes element = ElementTypes.NONE;
	
	/* ---- 1.12.2 Default Material ----
	 * public Material itemMaterial = Material.WOOD_SWORD;
	 * 
	 * ---- 1.13.2+ Default Material ----
	 * public Material itemMaterial = Material.WOODEN_SWORD;
	 */
	public Material itemMaterial = Material.WOODEN_SWORD;
	
	public HashMap<Enchantment, Integer> vEnchantments = new HashMap<Enchantment, Integer>();
	public HashMap<EnchantTypes, Integer> zEnchants = new HashMap<EnchantTypes, Integer>();
	public List<String> itemLore = new ArrayList<String>(); // The lore/description of the item
	
	public LinkedList<ZPotionEffect> passiveEffects = new LinkedList<ZPotionEffect>();
	public LinkedList<ZPotionEffect> activeEffects = new LinkedList<ZPotionEffect>();
	
	public ItemStack item; // Allows us to easily access the item
	
	public String itemName; // The name of the item
	
	public Element elements;
	
	public HashMap<Integer, ZCrafting> zCrafting = new HashMap<Integer, ZCrafting>();
	//public LinkedList<ZCrafting> zCrafting = new LinkedList<ZCrafting>();
	
	public boolean addBuff = false; 
	public boolean dmgTarget = true;
	
	public int itemCombatValue = 0;
	public int elementCombatValue = 0;
	public int debuffTime = 0; // Allows us to control how long the debuff is
	public short durability = 0; // Allows the user of the plugin to add custom models based on the durability
	//public byte materialData = 0;
	
	// Vanilla enchantments with the level stored beside it
	
	int itemID = -1;
	
	//public ZItem() {}
	
	public ZItem(int itemID) {
		this.itemID = itemID;
	}
	
	public int getItemID() {
		return itemID;
	}
	
	// Use this method to set the item stack variable
	public void setItem(Material itemType, int amount) {
		itemMaterial = itemType;
		
		// Create the item and assign the meta data to a variable
		ItemStack item = new ItemStack(itemType, amount);
		ItemMeta iMeta = item.getItemMeta();

		// Change the name and lore
		iMeta.setDisplayName(itemName);

		iMeta.setLore(Arrays.asList(ChatColor.DARK_PURPLE +  itemName + " | Item ID: " + itemID + ChatColor.WHITE)); // Give default lore of just the item name and id
		itemLore.add(ChatColor.DARK_PURPLE + itemName + " | Item ID: " + itemID + ChatColor.WHITE);

		// Attach the new meta data to the item
		item.setItemMeta(iMeta);
	
		this.item = item;
	}

	public void setItem(Material itemType, int amount, boolean useLoreList) {
		itemMaterial = itemType;
		
		ItemStack item = new ItemStack(itemType, amount);
		ItemMeta iMeta = item.getItemMeta();
		
		iMeta.setDisplayName(itemName);
		iMeta.setLore(itemLore);
		
		item.setItemMeta(iMeta);
		
		this.item = item;
	}
	
	@SuppressWarnings("deprecation")
	public void setItem(Material itemType, int amount, short durability, boolean useLoreList) {
		this.durability = durability;
		itemMaterial = itemType;
		
		ItemStack item = new ItemStack(itemType, amount);
		ItemMeta iMeta = item.getItemMeta();
		
		iMeta.setDisplayName(itemName);
		
		iMeta.setLore(Arrays.asList(ChatColor.DARK_PURPLE + itemName + " | Item ID: " + itemID + ChatColor.WHITE)); // Give default lore of just the item name and id
		itemLore.add(ChatColor.DARK_PURPLE + itemName + " | Item ID: " + itemID + ChatColor.WHITE);
		
		item.setItemMeta(iMeta);
		item.setDurability(durability);
		
		this.item = item;
	}
	
	@SuppressWarnings("deprecation")
	public void setItem(Material itemType, int amount, short durability) {
		this.durability = durability;
		itemMaterial = itemType;
		
		ItemStack item = new ItemStack(itemType, amount);
		ItemMeta iMeta = item.getItemMeta();
		
		iMeta.setDisplayName(itemName);
		iMeta.setLore(itemLore);
		
		item.setItemMeta(iMeta);
		item.setDurability(durability);		
		
		this.item = item;
	}
	
	public void GiveItem(Player player) {
		player.getInventory().addItem(item);
		// player.sendMessage(ChatColor.GOLD + "Custom Item NBT Data: " +
		// ChatColor.WHITE + GetNBTData(item));
	}

	/*
	 * 
	 * Methods for commands
	 * 
	 */
	
	public void setItemName(String name) {
		ItemMeta meta = item.getItemMeta();

		meta.setDisplayName(name); // Change the display name
		itemName = name;

		item.setItemMeta(meta);
	}

	public void setMaterial(Material material) {
		itemMaterial = material;
		item.setType(material);
	}
	
	@SuppressWarnings("deprecation")
	public void setDurability(short durability) {
		this.durability = durability;
		item.setDurability(durability);
	}
	
	public void setCombatValue(int combatValue) {
		itemCombatValue = combatValue;
	}
	
	/*
	public void setElement(ElementTypes element) {
		this.element = element; // Just like any normal setter method
	} */
	
	public void setElementCombatValue(int combatValue) {
		elementCombatValue = combatValue;
	}
	
	
	public void addZEnchant(CommandSender sender, EnchantTypes zEnchant, int strength) {
		boolean canEnchant = true;

		// Make sure we don't add the same enchant to the hashmap
		for (EnchantTypes key : zEnchants.keySet()) {
			if (key == zEnchant) {
				canEnchant = false;
				sender.sendMessage(ChatColor.RED + itemName + " already has " + ChatColor.DARK_PURPLE + zEnchant.toString() + ChatColor.RED + " custom enchantment!");
				break;
			}
		}

		if (canEnchant) {
			zEnchants.put(zEnchant, strength); // Add an custom potion effect buff to the enchant list
			
			ItemMeta meta = item.getItemMeta();
			
			String lore = zEnchant.toString().toLowerCase();
			lore = Character.toUpperCase(lore.charAt(0)) + lore.substring(1);
			
			itemLore.add("" + ChatColor.GRAY + ChatColor.ITALIC + lore);
			meta.setLore(itemLore);
			
			item.setItemMeta(meta);
			
			sender.sendMessage(ChatColor.GREEN + "Added " + ChatColor.DARK_PURPLE + zEnchant.toString() + ChatColor.GREEN + " to " + ChatColor.GOLD + itemName + "!");
		}
	}

	public void removeZEnchant(ZItemMain plugin, CommandSender sender, EnchantTypes zEnchant) {
		boolean canRemove = false;

		// Make sure the enchantment we're removing is on the item
		for (EnchantTypes key : zEnchants.keySet()) {
			if (key == zEnchant) {
				canRemove = true;
				break;
			}
		}

		if (canRemove) {
			zEnchants.remove(zEnchant);
			
			// Make sure to remove the item lore
			ItemMeta meta =item.getItemMeta();
			
			for(int i = 0; i < itemLore.size(); i++) {
				if(itemLore.get(i).equalsIgnoreCase("" + ChatColor.GRAY + ChatColor.ITALIC + zEnchant.toString())) {
					itemLore.remove(i);
					plugin.itemFile.RemoveLore(itemID, i);
				}
			}
			
			meta.setLore(itemLore);
			
			item.setItemMeta(meta);
			
			sender.sendMessage(ChatColor.GREEN + "Removed " + ChatColor.DARK_PURPLE + zEnchant.toString() + ChatColor.GREEN + " from item: " + ChatColor.GOLD + itemName + "!");
		} else {
			sender.sendMessage(ChatColor.RED + itemName + " doesn't have " + zEnchant.toString() + "!");
		}
	}

	public void addVEnchant(CommandSender sender, Enchantment enchant, int strength) {
		boolean canEnchant = true;

		ItemMeta meta = item.getItemMeta();

		// Make sure the item doesn't already have the enchantment
		for (Enchantment key : meta.getEnchants().keySet()) {
			if (key == enchant) {
				canEnchant = false;
				sender.sendMessage(ChatColor.RED + itemName + " already has " + ChatColor.DARK_PURPLE + HelperFunctions.getEnchantName(enchant) + ChatColor.RED + " enchantment!");
				break;
			}
		}

		if (canEnchant) {
			vEnchantments.put(enchant, strength);
			meta.addEnchant(enchant, vEnchantments.get(enchant), true);
			sender.sendMessage(ChatColor.GREEN + "Added " + ChatColor.DARK_PURPLE + HelperFunctions.getEnchantName(enchant) + ChatColor.GREEN + " to " + ChatColor.GOLD + itemName + "!");
			item.setItemMeta(meta);
		}
	}

	public void addVEnchant(Enchantment enchant, int strength) {
		boolean canEnchant = true;

		ItemMeta meta = item.getItemMeta();

		// Make sure the item doesn't already have the enchantment
		for (Enchantment key : meta.getEnchants().keySet()) {
			if (key == enchant) {
				canEnchant = false;
				break;
			}
		}

		if (canEnchant) {
			vEnchantments.put(enchant, strength);
			meta.addEnchant(enchant, vEnchantments.get(enchant), true);
			item.setItemMeta(meta);
		}
	}
	
	public void removeVEnchant(CommandSender sender, Enchantment enchant) {
		boolean canRemove = false;

		ItemMeta meta = item.getItemMeta();

		// Make sure the enchantment we're removing is on the item
		for (Enchantment key : meta.getEnchants().keySet()) {
			if (key == enchant) {
				canRemove = true;
				break;
			}
		}

		if (canRemove) {
			vEnchantments.remove(enchant);
			meta.removeEnchant(enchant);
			
			sender.sendMessage(ChatColor.GREEN + "Removed " + ChatColor.GOLD + HelperFunctions.getEnchantName(enchant) + ChatColor.GREEN + " from item: " + ChatColor.GOLD + itemName + "!");
			
			item.setItemMeta(meta);
		} else {
			sender.sendMessage(ChatColor.RED + itemName + " doesn't have " + HelperFunctions.getEnchantName(enchant) + "!");
		}
	}
	
	public void addLore(CommandSender sender, String lore) {
		ItemMeta meta = item.getItemMeta();
		
		// Just add the string to the lore in the item meta
		itemLore.add(lore);
		meta.setLore(itemLore);
		
		sender.sendMessage(ChatColor.GREEN + "Added lore!");
		
		item.setItemMeta(meta);
	}
	
	public void addLore(String lore) {
		ItemMeta meta = item.getItemMeta();
		
		// Just add the string to the lore in the item meta
		itemLore.add(lore);
		meta.setLore(itemLore);
		
		item.setItemMeta(meta);
	}
	
	public boolean removeLore(CommandSender sender, int line) {
		ItemMeta meta = item.getItemMeta();
		
		// Make sure the line number doesn't go below zero
		if(line <= 0) {
			sender.sendMessage(ChatColor.RED + "Cannot remove the first line and line number cannot be negative!");
			return false;
		}
		
		// Remove the lore if we have lore, and if the line number is within range
		if(!meta.getLore().isEmpty()) {
			if(line < meta.getLore().size()) {
				meta.getLore().remove(line);
				itemLore.remove(line);
				meta.setLore(itemLore);
				sender.sendMessage(ChatColor.GREEN + "Line " + line + " was removed from " + ChatColor.GOLD + itemName + "'s " + ChatColor.GREEN + "lore!");
				item.setItemMeta(meta);
				return true;
			} else {
				sender.sendMessage(ChatColor.RED + "Line is too big!");
				return false;
			}
		} else {
			sender.sendMessage(ChatColor.RED + "There is no lore!");
			return false;
		}
	}
	
	/*
	 * 
	 * Methods to call when we actually need to apply buffs, debuffs 
	 * 
	 */
	
	// Gives the player a buff while they hold the item or has it equipped
	public void enchantBuff(Player player) {
		if (!zEnchants.isEmpty()) {
			for (Map.Entry<EnchantTypes, Integer> entry : zEnchants.entrySet()) {
				int strength = entry.getValue() - 1;
				
				if(strength <= 0)
					strength = 0;
				
				if (entry.getKey() == EnchantTypes.REGEN) // Add a regen potion effect
					player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, strength));
				if (entry.getKey() == EnchantTypes.SPEED) // Add a speed potion effect
					player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, strength));
			}
		}
	}

	/* Helps the player boost their health 
	 * !!! This method is replaced by the potion effect to boost health !!!
	 * 
	@SuppressWarnings("deprecation")
	public void enchantHealthBoost(ZItemMain plugin, Player player) {
		if (!zEnchants.isEmpty()) {
			for (Map.Entry<EnchantTypes, Integer> entry : zEnchants.entrySet()) {
				if (entry.getKey() == EnchantTypes.HEALTHBOOST) {
					player.setMaxHealth(player.getMaxHealth() + entry.getValue()); // Set the max health to something above the default
					player.setHealthScaled(true); // Make it so the player doesn't get extra hearts

					if (plugin.getServer().getPluginManager().getPlugin("Skills") != null) { // If we're using the Skills plugin, use that max hp
						player.setMaxHealth(
								Skills.getInstance().playerSkills.get(player.getUniqueId()).getHealth(player)
										+ entry.getValue());
					}

				} else {
					player.resetMaxHealth(); // Reset the max health

					if (plugin.getServer().getPluginManager().getPlugin("Skills") != null) { // If we're using the Skills plugin, reset the that max health
						player.setMaxHealth(
								Skills.getInstance().playerSkills.get(player.getUniqueId()).getHealth(player));
					}
				}
			}
		}
	}*/

	public void enchantDebuff(LivingEntity target) {
		if (!zEnchants.isEmpty()) {
			for (EnchantTypes key : zEnchants.keySet()) {
				int strength = zEnchants.get(key) - 1;
				
				if(strength <= 0)
					strength = 0;
				
				if (key == EnchantTypes.SLOW) // Add the slowness effect to the target player
					target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, debuffTime * 20, strength));
				if (key == EnchantTypes.POISON) // Add the poison effect to the target player
					target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, debuffTime * 20, strength));
			}
		}
	}
	
	/*
	 * public static String GetNBTData(ItemStack item) { CraftItemStack cItemStack =
	 * CraftItemStack.asCraftCopy(item);
	 * 
	 * if(cItemStack.getType() != Material.AIR) {
	 * net.minecraft.server.v1_12_R1.ItemStack nmsItemStack =
	 * CraftItemStack.asNMSCopy(cItemStack);
	 * 
	 * NBTTagCompound compound = nmsItemStack.getTag();
	 * 
	 * if (compound == null) nmsItemStack.setTag(compound = new NBTTagCompound());
	 * // Just in case it was null
	 * 
	 * return compound.toString(); }
	 * 
	 * return "Item as air"; }
	 */
	/*
	 * public ItemStack createItemStack(Material itemType, int amount) {
	 * 
	 * // Create the item and assign the meta data to a variable ItemStack item =
	 * new ItemStack(itemType, amount); ItemMeta iMeta = item.getItemMeta();
	 * 
	 * // Change the name and lore iMeta.setDisplayName(itemName);
	 * 
	 * if (!itemLore.isEmpty()) iMeta.setLore(itemLore);
	 * 
	 * // Add vanilla enchantments to the item by looping through the hashmap if
	 * (!vEnchantments.isEmpty()) { for (Map.Entry<Enchantment, Integer> entry :
	 * vEnchantments.entrySet()) { iMeta.addEnchant(entry.getKey(),
	 * entry.getValue(), true); } }
	 * 
	 * // Attach the new meta data to the item item.setItemMeta(iMeta);
	 * 
	 * return item; }
	 */
}
