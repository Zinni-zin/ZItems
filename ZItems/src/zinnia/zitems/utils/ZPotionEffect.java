package zinnia.zitems.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import zinnia.zitems.main.CommandHandler;

public class ZPotionEffect {
	
	public PotionEffectType potionType;
	
	public int duration;	
	public int strength;
	
	public boolean useTicks = false;
	
	public ZPotionEffect(int duration, int strength) {
		this.strength = strength;
		this.duration = duration;
	}
	
	public void calculateDuration() {
		if(!useTicks) duration *= 20;
	}
	
	public void applyEffect(LivingEntity target) {
		//target.sendMessage(ChatColor.AQUA + "Potion Type: " + potionType);
		if(potionType != null) 
			target.addPotionEffect(new PotionEffect(potionType, duration, strength));
	}
	
	@SuppressWarnings("deprecation")
	public void setEffect(CommandSender sender, String effect) {
		// Simply try and set the potion effect and if we get any error send the error
		try { 
			if(CommandHandler.isInt(effect)) {
				potionType = PotionEffectType.getById(Integer.parseInt(effect));
			} else {
				potionType = HelperFunctions.GetPotionTypeByName(effect);
						// PotionEffectType.getByName(effect.toUpperCase());
				
			}
		} catch(Exception e) {
			sender.sendMessage(ChatColor.BLUE + effect + ChatColor.RED + " is an invalid potion effect type!");
			potionType = null; 
			return;
		}
		
		if(potionType == PotionEffectType.HEAL || potionType == PotionEffectType.HARM)
			potionType = null;
		
		if(potionType == null)
			sender.sendMessage(ChatColor.BLUE + effect + ChatColor.RED + " is an invalid potion effect type!");
	} 
	
	@SuppressWarnings("deprecation")
	public void setEffect(String effect) {
		// Simply try and set the potion effect and if we get any error send the error
		try { 
			if(CommandHandler.isInt(effect)) {
				potionType = PotionEffectType.getById(Integer.parseInt(effect));
			} else {
				potionType = HelperFunctions.GetPotionTypeByName(effect);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		if(potionType == PotionEffectType.HEAL || potionType == PotionEffectType.HARM)
			potionType = null;
	} 
	
	@SuppressWarnings("deprecation")
	public boolean CheckPotionEffect(CommandSender sender, String effect) {
		try { 
			PotionEffectType compare;
			if(CommandHandler.isInt(effect)) {
				compare = PotionEffectType.getById(Integer.parseInt(effect));
				return (compare == potionType) ? true : false;
			} else {
				compare = HelperFunctions.GetPotionTypeByName(effect);
				return (compare == potionType) ? true : false;
			}
		} catch(Exception e) {
			sender.sendMessage(ChatColor.RED + effect + " is an invalid potion effect type!");
			return false;
		}
	}
}
