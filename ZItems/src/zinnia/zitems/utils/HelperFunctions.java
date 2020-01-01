package zinnia.zitems.utils;

import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import zinnia.zitems.main.ZItemMain;

public class HelperFunctions {
	public static Enchantment getEnchantByName(String name) {
		// Weapon enchantments
		if(name.equalsIgnoreCase("smite")) return Enchantment.DAMAGE_UNDEAD;
		if(name.equalsIgnoreCase("looting")) return Enchantment.LOOT_BONUS_MOBS;
		if(name.equalsIgnoreCase("sharpness") || name.equalsIgnoreCase("sharp")) return Enchantment.DAMAGE_ALL;
		if(name.equalsIgnoreCase("fire") || name.equalsIgnoreCase("fireaspect") || name.equalsIgnoreCase("fire_aspect")) return Enchantment.FIRE_ASPECT;
		if(name.equalsIgnoreCase("knockback") || name.equalsIgnoreCase("knock_back") || name.equalsIgnoreCase("knock")) return Enchantment.KNOCKBACK;
		
		if(name.equalsIgnoreCase("bane_of_arthropods") || name.equalsIgnoreCase("baneofarthropods") || name.equalsIgnoreCase("bane") || name.equalsIgnoreCase("arthropods")) 
			return Enchantment.DAMAGE_ARTHROPODS;
		
		if(name.equalsIgnoreCase("sweeping_edge") || name.equalsIgnoreCase("sweeping") || name.equalsIgnoreCase("sweep") || name.equalsIgnoreCase("broom"))
			return Enchantment.SWEEPING_EDGE;
		
		if(name.equalsIgnoreCase("flame")) return Enchantment.ARROW_FIRE;
		if(name.equalsIgnoreCase("power")) return Enchantment.ARROW_DAMAGE;
		if(name.equalsIgnoreCase("punch")) return Enchantment.ARROW_KNOCKBACK;
		if(name.equalsIgnoreCase("infinity")) return Enchantment.ARROW_INFINITE;
		
		// Armor enchantments
		if(name.equalsIgnoreCase("protection")) return Enchantment.PROTECTION_ENVIRONMENTAL;
		if(name.equalsIgnoreCase("blast_protection") || name.equalsIgnoreCase("blastprotection") || name.equalsIgnoreCase("blast")) return Enchantment.PROTECTION_EXPLOSIONS;
		if(name.equalsIgnoreCase("fire_protection") || name.equalsIgnoreCase("fireprotect") || name.equalsIgnoreCase("fireprotection")) return Enchantment.PROTECTION_FIRE;
		
		if(name.equalsIgnoreCase("projectile_protection") || name.equalsIgnoreCase("projectileprotection") || name.equalsIgnoreCase("projectile")) 
			return Enchantment.PROTECTION_PROJECTILE;
		
		if(name.equalsIgnoreCase("featherfalling") || name.equalsIgnoreCase("feather_falling") || name.equalsIgnoreCase("feather") ||
				name.equalsIgnoreCase("fall") || name.equalsIgnoreCase("fallprotection")) 
			return Enchantment.PROTECTION_FALL;
		
		if(name.equalsIgnoreCase("thorns")) return Enchantment.THORNS;
		if(name.equalsIgnoreCase("frost_walker") || name.equalsIgnoreCase("frostWalker") || name.equalsIgnoreCase("frost")) return Enchantment.FROST_WALKER;		
		if(name.equalsIgnoreCase("respiration")) return Enchantment.OXYGEN;
		
		if(name.equalsIgnoreCase("depth") || name.equalsIgnoreCase("strider") || name.equalsIgnoreCase("depth_strider") || name.equalsIgnoreCase("depthstrider"))
			return Enchantment.DEPTH_STRIDER;
		
		// Tool enchantments
		if(name.equalsIgnoreCase("mending")) return Enchantment.MENDING;
		if(name.equalsIgnoreCase("efficiency")) return Enchantment.DIG_SPEED;
		if(name.equalsIgnoreCase("unbreaking")) return Enchantment.DURABILITY;
		if(name.equalsIgnoreCase("fortune")) return Enchantment.LOOT_BONUS_BLOCKS;
		if(name.equalsIgnoreCase("silk") || name.equalsIgnoreCase("silktouch") || name.equalsIgnoreCase("silk_touch")) return Enchantment.SILK_TOUCH;
		
		if(name.equalsIgnoreCase("aqua") || name.equalsIgnoreCase("affinity") || name.equalsIgnoreCase("aqua_affinity") || name.equalsIgnoreCase("aquaaffinity"))
			return Enchantment.WATER_WORKER;
		
		
		if(name.equalsIgnoreCase("lure")) return Enchantment.LURE;
		if(name.equalsIgnoreCase("luck") || name.equalsIgnoreCase("luckofthesea") || name.equalsIgnoreCase("luck_of_the_sea")) return Enchantment.LUCK;
		
		// Misc enchantments
		if(name.equalsIgnoreCase("binding") || name.equalsIgnoreCase("curseofbinding") || name.equalsIgnoreCase("curse_of_binding")) return Enchantment.BINDING_CURSE;
		if(name.equalsIgnoreCase("curse_of_vanishing") || name.equalsIgnoreCase("vanishing")) return Enchantment.VANISHING_CURSE;
		
		return null;
		// protection
	}
	
	public static String getEnchantName(Enchantment enchant) {
		// Weapon enchants
		if(enchant == Enchantment.DAMAGE_ALL) return "sharpness";
		if(enchant == Enchantment.DAMAGE_UNDEAD) return "smite";
		if(enchant == Enchantment.LOOT_BONUS_MOBS) return "looting";
		if(enchant == Enchantment.FIRE_ASPECT) return "fire_aspect";
		if(enchant == Enchantment.DAMAGE_ARTHROPODS) return "bane_of_arthropods";
		if(enchant == Enchantment.KNOCKBACK) return "knockback";		
		
		if(enchant == Enchantment.SWEEPING_EDGE) return "sweeping_edge";
		
		if(enchant == Enchantment.ARROW_FIRE) return "flame";
		if(enchant == Enchantment.ARROW_DAMAGE) return "power";
		if(enchant == Enchantment.ARROW_KNOCKBACK) return "punch";
		if(enchant == Enchantment.ARROW_INFINITE) return "infinity";
		
		// Armor enchants 
		if(enchant == Enchantment.PROTECTION_ENVIRONMENTAL) return "protection";
		if(enchant == Enchantment.PROTECTION_EXPLOSIONS) return "blast_protection";
		if(enchant == Enchantment.PROTECTION_FIRE) return "fire_protection"; 
		if(enchant == Enchantment.PROTECTION_PROJECTILE) return "projectile_protection"; 
		if(enchant == Enchantment.PROTECTION_FALL) return "feather_falling";
		if(enchant == Enchantment.THORNS) return "thorns";
		if(enchant == Enchantment.FROST_WALKER) return "frost_walker";
		if(enchant == Enchantment.OXYGEN) return "respiration";
		if(enchant == Enchantment.DEPTH_STRIDER) return "depth_strider";
		
		// Tool enchantments 
		if(enchant == Enchantment.MENDING) return "mending";
		if(enchant == Enchantment.DIG_SPEED) return "efficiency";
		if(enchant == Enchantment.DURABILITY) return "unbreaking";
		if(enchant == Enchantment.LOOT_BONUS_BLOCKS) return "fortune";
		if(enchant == Enchantment.SILK_TOUCH) return "silk_touch";
		if(enchant == Enchantment.WATER_WORKER) return "aqua_affinity";
		if(enchant == Enchantment.LURE) return "lure";
		if(enchant == Enchantment.LUCK) return "luck_of_the_sea";
		
		// Misc enchantments 
		if(enchant == Enchantment.BINDING_CURSE) return "curse_of_binding";
		if(enchant == Enchantment.VANISHING_CURSE) return "curse_of_vanishing";
		
		
		
		return null;
	}
	
	public static PotionEffectType GetPotionTypeByName(String name) {
		
		if(name.equalsIgnoreCase("speed")) return PotionEffectType.SPEED;
		if(name.equalsIgnoreCase("slowness") || name.equalsIgnoreCase("slow")) return PotionEffectType.SLOW;  
		if(name.equalsIgnoreCase("haste") || name.equalsIgnoreCase("fast_digging")) return PotionEffectType.FAST_DIGGING;  	
		if(name.equalsIgnoreCase("mining_fatigue") || name.equalsIgnoreCase("slow_digging") || name.equalsIgnoreCase("fatigue")) return PotionEffectType.SLOW_DIGGING;  
		if(name.equalsIgnoreCase("strength") || name.equalsIgnoreCase("increase_damage")) return PotionEffectType.INCREASE_DAMAGE;
		if(name.equalsIgnoreCase("instant_health") || name.equalsIgnoreCase("heal")) return PotionEffectType.HEAL;
		if(name.equalsIgnoreCase("instant_damage") || name.equalsIgnoreCase("harm") || name.equalsIgnoreCase("damage")) return PotionEffectType.HARM;
		if(name.equalsIgnoreCase("jump_boost") || name.equalsIgnoreCase("jump")) return PotionEffectType.JUMP;
		if(name.equalsIgnoreCase("nausea") || name.equalsIgnoreCase("confusion")) return PotionEffectType.CONFUSION;
		if(name.equalsIgnoreCase("regeneration") || name.equalsIgnoreCase("regen")) return PotionEffectType.REGENERATION;
		if(name.equalsIgnoreCase("resistance") || name.equalsIgnoreCase("resist") || name.equalsIgnoreCase("damage_resistance")) return PotionEffectType.DAMAGE_RESISTANCE;
		if(name.equalsIgnoreCase("fire_resistance") || name.equalsIgnoreCase("fire_resist")) return PotionEffectType.FIRE_RESISTANCE;
		if(name.equalsIgnoreCase("water_breathing") || name.equalsIgnoreCase("water_breath") || name.equalsIgnoreCase("breathing")) return PotionEffectType.WATER_BREATHING;
		if(name.equalsIgnoreCase("invisibility")) return PotionEffectType.INVISIBILITY;
		if(name.equalsIgnoreCase("blindness")) return PotionEffectType.BLINDNESS;
		if(name.equalsIgnoreCase("night_vision") || name.equalsIgnoreCase("night")) return PotionEffectType.NIGHT_VISION;
		if(name.equalsIgnoreCase("hunger")) return PotionEffectType.HUNGER;
		if(name.equalsIgnoreCase("weakness") || name.equalsIgnoreCase("weak")) return PotionEffectType.WEAKNESS;
		if(name.equalsIgnoreCase("poison")) return PotionEffectType.POISON;
		if(name.equalsIgnoreCase("wither")) return PotionEffectType.WITHER;
		if(name.equalsIgnoreCase("health_boost") || name.equalsIgnoreCase("health")) return PotionEffectType.HEALTH_BOOST;
		if(name.equalsIgnoreCase("absorption")) return PotionEffectType.ABSORPTION;
		if(name.equalsIgnoreCase("saturation")) return PotionEffectType.SATURATION;
		if(name.equalsIgnoreCase("glowing")) return PotionEffectType.GLOWING;
		if(name.equalsIgnoreCase("levitation")) return PotionEffectType.LEVITATION;
		if(name.equalsIgnoreCase("luck")) return PotionEffectType.LUCK;
		if(name.equalsIgnoreCase("unluck") || name.equalsIgnoreCase("unlucky")) return PotionEffectType.UNLUCK;
		
		// 1.13+ potion effects: slow_falling, conduit_power, dolphins_grace, bad_omen, hero_of_the_village
		
		return null;
	}
	
	public static String GetPotionTypeName(PotionEffectType type) {
		if(type == PotionEffectType.SPEED) return "speed";
		if(type == PotionEffectType.SLOW) return "slowness";  
		if(type == PotionEffectType.FAST_DIGGING) return "haste";  	
		if(type == PotionEffectType.SLOW_DIGGING) return "mining_fatigue";  
		if(type == PotionEffectType.INCREASE_DAMAGE) return "strength";
		if(type == PotionEffectType.HEAL) return "instant_health";
		if(type == PotionEffectType.HARM) return "instant_damage";
		if(type == PotionEffectType.JUMP) return "jump_boost";
		if(type == PotionEffectType.CONFUSION) return "nausea";
		if(type == PotionEffectType.REGENERATION) return "regeneration";
		if(type == PotionEffectType.DAMAGE_RESISTANCE) return "resistance";
		if(type == PotionEffectType.FIRE_RESISTANCE) return "fire_resistance";
		if(type == PotionEffectType.WATER_BREATHING) return "water_breathing";
		if(type == PotionEffectType.INVISIBILITY) return "invisibility";
		if(type == PotionEffectType.BLINDNESS) return "blindness";
		if(type == PotionEffectType.NIGHT_VISION) return "night_vision";
		if(type == PotionEffectType.HUNGER) return "hunger";
		if(type == PotionEffectType.WEAKNESS) return "weakness";
		if(type == PotionEffectType.POISON) return "poison";
		if(type == PotionEffectType.WITHER) return "wither";
		if(type == PotionEffectType.HEALTH_BOOST) return "health_boost";
		if(type == PotionEffectType.ABSORPTION) return "absorption";
		if(type == PotionEffectType.SATURATION) return "saturation";
		if(type == PotionEffectType.GLOWING) return "glowing";
		if(type == PotionEffectType.LEVITATION) return "levitation";
		if(type == PotionEffectType.LUCK) return "luck";
		if(type == PotionEffectType.UNLUCK) return "unluck";
		
		// 1.13+ potion effects: slow_falling, conduit_power, dolphins_grace, bad_omen, hero_of_the_village
		
		return null;
	}
	
	
	public static void RefreshItems(ZItemMain plugin) {
		for(Player player : Bukkit.getOnlinePlayers()) { 
			for(ItemStack item : player.getInventory()) { // Loop through the player's inventory
				if(IsZItem(item)) { // if the item is a zitem update it
					UpdateItem(plugin, item);
				}
			}
			for(ItemStack item : player.getInventory().getArmorContents()) { // Same as above but looping through armor slots
				if(IsZItem(item)) { 
					UpdateItem(plugin, item);
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void UpdateItem(ZItemMain plugin, ItemStack item) {
		// The first line in an item's lore always has the item name and id, get the id out of the item's lore
		String itemLine = item.getItemMeta().getLore().get(0);
		itemLine = itemLine.substring(itemLine.length() - 3, itemLine.length() - 2);
		itemLine.trim(); 

		try {
			// Parse the item id out of the item line
			int itemID = Integer.parseInt(itemLine.substring(itemLine.length() - 1));
			 
			if(plugin.customItems.containsKey(itemID)) { // If the zitem exits in our custom item has map
				// Change the item to match the zItem
				item.setItemMeta(plugin.customItems.get(itemID).item.getItemMeta());
				item.setDurability(plugin.customItems.get(itemID).durability);
				item.setType(plugin.customItems.get(itemID).itemMaterial);
			}
		} catch (NumberFormatException e) { return; }
	}
	
	public static int GetZItemID(ItemStack item) {
		int id = -1;
		
		if(IsZItem(item)) {
			String itemLine = item.getItemMeta().getLore().get(0);
			itemLine = itemLine.substring(itemLine.length() - 3, itemLine.length() - 2);
			itemLine.trim(); 
			
			id = Integer.parseInt(itemLine.substring(itemLine.length() - 1));
			
			return id;
		}
		
		return id;
	}
	
	public static boolean IsZItem(ItemStack item) {
		if(item == null || item.getItemMeta() == null || item.getItemMeta().getLore() == null || item.getItemMeta().getLore().isEmpty())
			return false;
		
		return (item.getItemMeta().getLore().get(0).contains("Item ID:"))? true : false;
	}
	
	public static <K, V> K GetKey(Map<K, V> map, V value) {
		for(Entry<K, V> entry : map.entrySet()) {
			if(entry.getValue().equals(value))
				return entry.getKey();
		}
		
		return null;
	}
	
	public static boolean ArrayContains(Object[] array, Object item) {
			for(Object o : array)
				if(o != null)
					if(o.equals(item)) 
						return true;
		
		return false;
	}
}
