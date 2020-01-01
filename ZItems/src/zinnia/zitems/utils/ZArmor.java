package zinnia.zitems.utils;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import zinnia.zitems.items.ZItem;
import zinnia.zitems.main.ZItemMain;

/*
 * Of course the loading was figured out, but now the armor doesn't work >.>
 */

public class ZArmor {

	boolean canPotionEffect = false;
	
	ZItem zItem;
	ItemStack item;
	
	public ZArmor(ZItemMain plugin, int id) {
		zItem = plugin.customItems.get(id);
		item = zItem.item;
	}
	
	public void potionRunnable(ZItemMain plugin, Player player) {
		new BukkitRunnable() { 
			@Override
			public void run() {
				//player.sendMessage(ChatColor.GREEN + "Armor reached runnable");
			
				if(!canPotionEffect) { // Cancel if the player isn't able to do potion effects
					this.cancel();
					//player.sendMessage(ChatColor.RED + zItem.itemName + "'s potion effect has been cancelled!");
				}
	
			
				if(!this.isCancelled()) { // If the event isn't canceled put the potion effect onto the player
					//player.sendMessage(ChatColor.GOLD + "Armor isn't cancelled");
					//player.sendMessage(ChatColor.GRAY + "Passive Effect Size: " + zItem.passiveEffects.size());
					
					if(!zItem.passiveEffects.isEmpty()) {
						for(int i = 0; i < zItem.passiveEffects.size(); i++)  {
							zItem.passiveEffects.get(i).applyEffect(player);
							//player.sendMessage(ChatColor.BLUE + "Potion Effect type: " + zItem.passiveEffects.get(i).potionType);
						}
					}
					
					//zItem.enchantBuff(player);
				}
			}
		}.runTaskTimer(plugin, 0, 100);
	}
	
	// If the item we're equipping is a zItem apply the passive buff, otherwise if it's not a zItem remove set the potion effect to false
	public void checkItem(ZItemMain plugin, Player player, ItemStack item) {
		if(this.item.getItemMeta().equals(item.getItemMeta())) {
			canPotionEffect = true;
			//player.sendMessage(ChatColor.AQUA + "Armor Effect Applied!");
			potionRunnable(plugin, player);
		} else {
			//player.sendMessage(ChatColor.DARK_PURPLE + "ZItem type: " + ChatColor.GOLD + this.item.getType() + ChatColor.WHITE + " | " + 
		//ChatColor.LIGHT_PURPLE + "New armor piece: " + ChatColor.GOLD + item.getType());
			
			if(item.getType() == this.item.getType()) 
				canPotionEffect = false;
		}
	}
	
	// Mainly used if this item is being deleted
	public void removeBuff() {
		canPotionEffect = false;
	}
	
	// If the item we are removing is a zItem set the potion effect to false
	public void removeBuff(ItemStack item) {
		if(this.item.getItemMeta().equals(item.getItemMeta()))
			canPotionEffect = false;
	}
	
	// Check if the player is wearing custom armor of each armor type
	public void loginCheck(ZItemMain plugin, Player player) {
		if(player.getInventory().getHelmet() != null && item.getItemMeta().equals(player.getInventory().getHelmet().getItemMeta())) {
			canPotionEffect = true;
					
			potionRunnable(plugin, player);
		}
				
		if(player.getInventory().getChestplate() != null && item.getItemMeta().equals(player.getInventory().getChestplate().getItemMeta())) {
			canPotionEffect = true;
			
			potionRunnable(plugin, player);
		}
					
		if(player.getInventory().getLeggings() != null && item.getItemMeta().equals(player.getInventory().getLeggings().getItemMeta())) {
			canPotionEffect = true;
						
			potionRunnable(plugin, player);
		}
					
		if(player.getInventory().getBoots() != null && item.getItemMeta().equals(player.getInventory().getBoots().getItemMeta())) {
			canPotionEffect = true;
						
			potionRunnable(plugin, player);
		}
	}
}
