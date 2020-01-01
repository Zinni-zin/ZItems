package zinnia.zitems.player;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.codingforcookies.armorequip.ArmorEquipEvent;

import zinnia.zitems.items.ZItem;
import zinnia.zitems.main.ZItemMain;
import zinnia.zitems.utils.HelperFunctions;
import zinnia.zitems.utils.ZArmor;
import zinnia.zitems.utils.ZPotionEffect;

/*
 * !!! Important !!!
 * 
 * 
 */


public class PlayerEvents implements Listener {

	ZItemMain plugin; // Reference to the main plugin class

	HashMap<UUID, Boolean> canPotionEffect = new HashMap<UUID, Boolean>(); // Helps determine when to cancel the runnable for applying passive effects
	static HashMap<Integer, ZArmor> zArmor = new HashMap<Integer, ZArmor>(); // Helps us handle armor, make sure the item id is same as the z item id
	
	// These two lists are used to determine what players need to be put into the two hash maps above
	LinkedList<UUID> potionEffectPlayers = new LinkedList<UUID>();
	
	public static boolean checkZArmor = false;
	
	public PlayerEvents(ZItemMain plugin) {
		this.plugin = plugin;
		
		/*
		 * This section is for when the server is reloaded with players on it, we want to add them to the potion effect hashmaps
		 */
		for(Player p : Bukkit.getOnlinePlayers()) { // Loop through the online players.
			potionEffectPlayers.add(p.getUniqueId()); // Add them to a list so we can determine which players to put into a hashmap
		}
		
		// if any of our custom items are armor, add them to our zArmor hashmap
		for(Integer key : plugin.customItems.keySet()) {
			if(isArmor(plugin.customItems.get(key).item)) 
				zArmor.put(key, new ZArmor(plugin, key));
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		
		// Add the players into the map so we have players in the list
		potionEffectPlayers.add(player.getUniqueId()); 
		
		addPlayersToPotionMap(); // Put our players into the use potion effect maps
		
		// Check if the player has a permission to open the menu, if they do set default
		// values to their menu/player data, then load in the values
		if (player.hasPermission(ZItemMain.PERMISSION_STR)) {
			if (!plugin.playerData.containsKey(player.getUniqueId())) { // If the player isn't in the hashmap
				plugin.playerData.put(player.getUniqueId(), new PlayerData()); // Create the player data class
				plugin.playerData.get(player.getUniqueId()).setAvailableSlots((short) 0); // Set the available slots to 0

				plugin.loadPlayerData(player.getUniqueId()); // Load in the player data
			} else { // Otherwise just load the player data
				plugin.loadPlayerData(player.getUniqueId());
			}
		}
		
		LoginArmorBuff(player); // Apply any armor buffs on login
		
		if(!plugin.customItems.isEmpty())
			PotionEffect(e.getPlayer(), player.getInventory().getHeldItemSlot());
	}

	@EventHandler
	public void onEntityDamagedByEntity(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player) {
			Player player = (Player)e.getDamager();
			
			int heldSlot = player.getInventory().getHeldItemSlot();
			
			// Make sure the item is null
			if(player.getInventory().getItem(heldSlot) != null && player.getInventory().getItem(heldSlot).getType() != Material.AIR) {
				ItemStack heldItem = player.getInventory().getItem(heldSlot);
				
				// Check if it's a zitem, then create a zitem
				if(HelperFunctions.IsZItem(heldItem)) {
					ZItem zItem = plugin.customItems.get(HelperFunctions.GetZItemID(heldItem));
					
					// All these are for damage calculation 
					double damage = e.getDamage();
					double zItemDamage = 0; // Item damage
					double elementalDamage = 0; // Elemental damage
					double elementDmgMulti = 1; // This is the super effective elemental damage variable
					double elementDefenseMulti = 1; // This is the not very effect elemental damage variable
					
					// Set the item damage depending if we're adding the normal damage or not
					if(ZItemMain.addDamageFromItem)
						zItemDamage = e.getDamage() + zItem.itemCombatValue;
					else
						zItemDamage = zItem.itemCombatValue;
					
					// If the item as an element
					if(zItem.elements != null) { 
						// If we're applying the default damage to the elemental damage
						if(ZItemMain.addElementalDamageFromItem) { 
							if(ZItemMain.addDamageFromItem) { // Make sure we don't add the default damage twice
								elementalDamage = zItem.elements.elementValue; 
							} else {
								elementalDamage = e.getDamage() + zItem.elements.elementValue;
							}
						} else { // If we're not adding the default damage, just set the elemental damage
							elementalDamage = zItem.elements.elementValue;
						}
						
						for(String elementStrength : zItem.elements.effective) {
							for(int elementID : plugin.Elements.keySet()) { 
								if(elementStrength.equalsIgnoreCase(plugin.Elements.get(elementID).elementName))
									elementDmgMulti = zItem.elements.posDmgMultiplier; // Set the damage multiplier
							}
						}
					}
					
					// Use this to set the defense multiplier
					if(e.getEntity() instanceof Player) { 
						Player target = (Player)e.getEntity();
						// Get the armor and convert it into a zitem
						for(ItemStack armor : target.getInventory().getArmorContents()) {
							if(HelperFunctions.IsZItem(armor)) {
								ZItem zArmor = plugin.customItems.get(HelperFunctions.GetZItemID(armor));
								
								// Check if the elements are null
								if(zArmor.elements != null) { // Loop through all the weaknesses
									for(String elementWeaknesses : zArmor.elements.weak) {
										for(int elementID : plugin.Elements.keySet()) { // loop through all the elements
											// If the names are the same, add the negative damage multiplier
											if(elementWeaknesses.equalsIgnoreCase(plugin.Elements.get(elementID).elementName)) {
												elementDefenseMulti = plugin.Elements.get(elementID).negDmgMultiplier;
												break;
											}
										}
									}
								}
							}
						}
					}
					
					//player.sendMessage(ChatColor.BLUE + "Item Damage: " + ChatColor.GOLD + zItemDamage);
					//player.sendMessage(ChatColor.LIGHT_PURPLE + "Element Damage: " + ChatColor.GOLD + elementalDamage);
					//player.sendMessage(ChatColor.AQUA + "Element Damage Multiplier: " + ChatColor.GOLD + elementDmgMulti);
					//player.sendMessage(ChatColor.DARK_AQUA + "Element Defense Multiplier: " + ChatColor.GOLD + elementDefenseMulti);
					
					// Calculate the damage
					damage = ((zItemDamage + elementalDamage) * elementDmgMulti) * elementDefenseMulti;
					
					if(damage == 0) // Make sure there's some actual damage
						damage = e.getDamage();
					
					//player.sendMessage(ChatColor.YELLOW + "Default Damage: " + ChatColor.GOLD + e.getDamage());
					//player.sendMessage(ChatColor.DARK_PURPLE + "Total Damage: " + ChatColor.GOLD + damage);
					//player.sendMessage(ChatColor.GREEN + "-----------------------------");
					
					if(!zItem.dmgTarget && e.getEntity() instanceof Player) damage = 0;
					
					e.setDamage(damage); // Set the damage
					
					// Apply the potion effects to target
					if(e.getEntity() instanceof LivingEntity) {
						LivingEntity target = (LivingEntity)e.getEntity();
						
						if(target instanceof Player) {
							if(!zItem.activeEffects.isEmpty()) {
								for(ZPotionEffect pEffect : zItem.activeEffects) {
									pEffect.applyEffect(target);
								}
							}
							//zItem.enchantDebuff(target);
						} else if(ZItemMain.canDebuffMobs){
							//player.sendMessage(ChatColor.GREEN + "can debuff mobs");if(!zItem.activeEffects.isEmpty())
							if(!zItem.activeEffects.isEmpty()) {
								for(ZPotionEffect pEffect : zItem.activeEffects) {
									pEffect.applyEffect(target);
								}
							}
							//zItem.enchantDebuff(target);
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onItemChange(PlayerItemHeldEvent e) {
		ItemStack oldItem = e.getPlayer().getInventory().getItem(e.getPreviousSlot());
		
		// Just so we don't have an item trying to apply it's effect when another item is on
		if(oldItem != null && oldItem.getType() != Material.AIR) {
			if(HelperFunctions.IsZItem(oldItem)) { 
				ZItem zItem = plugin.customItems.get(HelperFunctions.GetZItemID(oldItem));
				if(zItem != null)
					zItem.addBuff = false;
			}
		}
		
		if(!plugin.customItems.isEmpty())
			PotionEffect(e.getPlayer(), e.getNewSlot()); // Apply potion effects if the player is holding a custom item
	}
	
	@EventHandler
	public void ArmorEquipEvent(ArmorEquipEvent e) {
		
		if(checkZArmor) { // if any of our custom items are armor, add them to our zArmor hashmap
			for(Integer key : plugin.customItems.keySet()) {
				if(isArmor(plugin.customItems.get(key).item)) 
					zArmor.put(key, new ZArmor(plugin, key));
			}
			checkZArmor = false;
		}
		
		// When equipping armor, call our function to check if it's a zItem, if it is it'll apply the passive effect
		if(e.getNewArmorPiece() != null && e.getNewArmorPiece().getType() != Material.AIR) { 
			//System.out.println("\nThe new armor piece isn't null!");
			for(Integer key : zArmor.keySet()) {
				zArmor.get(key).checkItem(plugin, e.getPlayer(), e.getNewArmorPiece());
			}
		}
		
		// When unequipping armor check if it's a zItem if it is remove the passive effect
		if(e.getOldArmorPiece() != null && e.getOldArmorPiece().getType() != Material.AIR) {
			for(Integer key : zArmor.keySet()) {
				zArmor.get(key).removeBuff(e.getOldArmorPiece());
			}
		}
	}
	
	public static void removeArmor(int key) {
		if(zArmor.containsKey(key)) {
			zArmor.get(key).removeBuff();
			zArmor.remove(key);
		}
	}
	
	@EventHandler
	public void onItemDamage(PlayerItemDamageEvent e) {
		if (!ZItemMain.hasItemDurability) // Simply if we're items are not going to have durability cancel the item damage event
			e.setCancelled(true);
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if(e.getInventory() == null) return;
		if(e.getClickedInventory() == null) return;
		if(e.getInventory().getType() == null) return;
		
		/* ---- 1.12.2 & 1.13.2 Check ----
		 * if(!e.getInventory().getName().equalsIgnoreCase("Chest") && !e.getInventory().getName().equalsIgnoreCase("Ender Chest") ||
				!e.getInventory().getName().equalsIgnoreCase("Shulker box")) return;
		 */
		
		InventoryType invType = e.getInventory().getType();
		
		if(invType == InventoryType.CHEST || invType == InventoryType.ENDER_CHEST || invType == InventoryType.SHULKER_BOX) {
			if(e.getCurrentItem() != null || e.getCurrentItem().getType() != Material.AIR) {
				if(HelperFunctions.IsZItem(e.getCurrentItem())) {
					HelperFunctions.UpdateItem(plugin, e.getCurrentItem());
				}
			}
		}	
	}

	@EventHandler
	public void onPrepareItemCraftEvent(PrepareItemCraftEvent e) {
		
		/*
		if(e.getInventory().getMatrix().length < 9) {
			return;
		}
		*/
		if(!plugin.customItems.isEmpty()) {
			for(Integer key : plugin.customItems.keySet()) {
				if(!plugin.customItems.get(key).zCrafting.isEmpty()) {
					for(Integer craftKey : plugin.customItems.get(key).zCrafting.keySet()) {
						if(!plugin.customItems.get(key).zCrafting.get(craftKey).GetIngredients().isEmpty()) {
							//boolean useFourSlots = plugin.customItems.get(key).zCrafting.getFourSlots();
							boolean shapeless = plugin.customItems.get(key).zCrafting.get(craftKey).getShapeless();
							
							//if(e.getInventory().getResult() == null || e.getInventory().getResult().getType() == Material.AIR)
							checkCraft(plugin.customItems.get(key).item, e.getInventory(), shapeless, plugin.customItems.get(key).zCrafting.get(craftKey).GetIngredients());
					
							//checkCraft(plugin.customItems.get(key).item, e.getInventory(), useFourSlots, shapeless, plugin.customItems.get(key).zCrafting.GetIngredients());
						}
					}
				}
			}
		}
				
		/*
		checkCraft(new ItemStack(Material.EMERALD_BLOCK), e.getInventory(), false, new HashMap<Integer, ItemStack>() {{ 
			put(4, plugin.customItems.get(0).item);
		}}); */
	}
	//public void checkCraft(ItemStack result, CraftingInventory inv, boolean useFourSlots, boolean shapeless, HashMap<Integer, ItemStack> ingredients) {
	public void checkCraft(ItemStack result, CraftingInventory inv, boolean shapeless, HashMap<Integer, ItemStack> ingredients) {
		ItemStack[] matrix = inv.getMatrix();
		
		int count = 4;
		if(matrix.length > 4) count = 9;
		
		if(shapeless) {
			int itemCount = -1;
			
			// This just checks if the user put all the ingredients in
			for(Integer key : ingredients.keySet())
				if(!HelperFunctions.ArrayContains(matrix, ingredients.get(key))) return;
			
			//  Check if the item is the same in the crafting table
			for(int i = 0; i < count; i++) {
				if(ingredients.containsValue(matrix[i])) {
					if(matrix[i].equals(ingredients.get(HelperFunctions.GetKey(ingredients, matrix[i])))) 
						itemCount++;
				}
			}
			
			if(itemCount == ingredients.size() - 1) {
				inv.setResult(result);
				itemCount = 0;
			}
		} else {
			for(int i = 0; i < count; i++) {
				if(ingredients.containsKey(i)) {
					if(matrix[i] == null || !matrix[i].equals(ingredients.get(i))) return;
				} else if(matrix[i] != null) return;
			}
			inv.setResult(result);
		}	
	}
	
	/* 
	 * 
	 * !!! HELPER FUNCTIONS !!!
	 * 
	 */
	private void PotionEffect(Player player, int itemSlot) {
		// Use the item meta to check if it's a custom item
		
		ItemStack item = player.getInventory().getItem(itemSlot); // Get the item the player is holding
		int id[] = new int[1]; // We have to use an array here so we can put the id variable in the runnable >.>
		
		for (Integer key : plugin.customItems.keySet()) { // Loop through the custom item hash map
			// Check if the item being switched to isn't null, then check the item meta to see if it's a custom item
			if (player.getInventory().getItem(itemSlot) != null && plugin.customItems.get(key).item.getItemMeta().equals(item.getItemMeta())) {
				canPotionEffect.put(player.getUniqueId(), true); // Set this to true since the player is holding a custom item
				id[0] = key; // Set our id to our key
				
				plugin.customItems.get(id[0]).addBuff = true;
				
				break; // Get out of the for loop
			} else { // If the player has switched off of the item
				canPotionEffect.put(player.getUniqueId(), false); // Set this to false, so we can break out of the for loop
			}
		}
		
		new BukkitRunnable() { 
			@Override
			public void run() {
				 // If the player has stopped holding the item, cancel the runnable
				if (!plugin.customItems.containsKey(id[0]) || plugin.customItems.get(id[0]) == null || !canPotionEffect.get(player.getUniqueId()) || !plugin.customItems.get(id[0]).addBuff)
					this.cancel();
				
				if(!this.isCancelled()) // If the runnable isn't cancelled give the player a buff 
				{
					for(int i = 0; i < plugin.customItems.get(id[0]).passiveEffects.size(); i++)
						plugin.customItems.get(id[0]).passiveEffects.get(i).applyEffect(player);
					//plugin.customItems.get(id[0]).enchantBuff(player); 
				}
				
			}
		}.runTaskTimer(plugin, 0, 100); // Repeat every five seconds
	}
	
	private void addPlayersToPotionMap() {
		
		// Remove players from the player list if they're in our hash map
		for(UUID key : canPotionEffect.keySet()) {
			for(UUID playerUUID : potionEffectPlayers) 
				if(key == playerUUID) 
					potionEffectPlayers.remove(playerUUID);
		}
	}
	
	// WHen the player joins the game, check they have an armor buff to apply
	private void LoginArmorBuff(Player player) {
		if(!zArmor.isEmpty()) {
			for(Integer key : zArmor.keySet()) {
				zArmor.get(key).loginCheck(plugin, player);
			}
		}
	}
	
	// Simply checks if an item is armor or not
	public static boolean isArmor(ItemStack item) {
		Material material = item.getType();
		
		// If any of the pieces are leather armor
		if(material == Material.LEATHER_HELMET || material == Material.LEATHER_CHESTPLATE || material == Material.LEATHER_LEGGINGS || material == Material.LEATHER_BOOTS)
			return true;
		
		// If any of the pieces are golden armor
		/* ---- 1.12.2 Gold Material Check ----
		 *  if(material == Material.GOLD_HELMET || material == Material.GOLD_CHESTPLATE || material == Material.GOLD_LEGGINGS || material == Material.GOLD_BOOTS)
		 * 
		 * ---- 1.13.2+ Gold Material Check ----
		 * if(material == Material.GOLDEN_HELMET || material == Material.GOLDEN_CHESTPLATE || material == Material.GOLDEN_LEGGINGS || material == Material.GOLDEN_BOOTS)
		 */
		if(material == Material.GOLDEN_HELMET || material == Material.GOLDEN_CHESTPLATE || material == Material.GOLDEN_LEGGINGS || material == Material.GOLDEN_BOOTS)
			return true;
		
		// If any of the pieces are chainmail armor
		if(material == Material.CHAINMAIL_HELMET || material == Material.CHAINMAIL_CHESTPLATE || material == Material.CHAINMAIL_LEGGINGS || material == Material.CHAINMAIL_BOOTS)
			return true;
		
		// If any of the pieces are iron armor
		if(material == Material.IRON_HELMET || material == Material.IRON_CHESTPLATE || material == Material.IRON_LEGGINGS || material == Material.IRON_BOOTS)
			return true;
		
		// If any of the pieces are diamond armor
		if(material == Material.DIAMOND_HELMET || material == Material.DIAMOND_CHESTPLATE || material == Material.DIAMOND_LEGGINGS || material == Material.DIAMOND_BOOTS)
		  return true;
		
		return false;
	}
}
