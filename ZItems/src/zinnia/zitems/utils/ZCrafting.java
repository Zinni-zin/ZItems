package zinnia.zitems.utils;

import java.util.HashMap;

import org.bukkit.inventory.ItemStack;

public class ZCrafting {
	
	HashMap<Integer, ItemStack> ingredients = new HashMap<Integer, ItemStack>();
	
	boolean isShapeless = false;
	//boolean fourSlots = false;
	
	int id = -1;
	
	public ZCrafting(int id) {
		this.id = id;
	}
	
	public void AddIngredient(int slot, ItemStack ingredient) {
		
		if(ingredients.containsKey(slot))
			ingredients.replace(slot, ingredient);
		else
			ingredients.put(slot, ingredient);
	}
	
	public HashMap<Integer, ItemStack> GetIngredients() {
		return ingredients;
	}
	
	public void setShapeless(boolean value) {
		isShapeless = value;
	}
	
	public boolean getShapeless() {
		return isShapeless;
	}
	
	public int getID() {
		return id;
	}
	
	/*
	public void setFourSlots(boolean value) {
		fourSlots = value;
	}
	
	public boolean getFourSlots() {
		return fourSlots;
	}
	*/
}
