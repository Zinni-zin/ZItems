package zinnia.zitems.main;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;

import com.codingforcookies.armorequip.ArmorBlockedMatsFile;
import com.codingforcookies.armorequip.ArmorListener;

import zinnia.zitems.items.ZItem;
import zinnia.zitems.player.PlayerData;
import zinnia.zitems.player.PlayerEvents;
import zinnia.zitems.player.PlayerMenu;
import zinnia.zitems.utils.CraftingIO;
import zinnia.zitems.utils.Element;
import zinnia.zitems.utils.ElementIO;
import zinnia.zitems.utils.ItemIO;

public class ZItemMain extends JavaPlugin {
	
	public HashMap<UUID, PlayerData> playerData = new HashMap<UUID, PlayerData>(); // Store player data
	public HashMap<UUID, PlayerMenu> playerMenu = new HashMap<UUID, PlayerMenu>(); // Menu the player stores items in
	public HashMap<Integer, ZItem> customItems = new HashMap<Integer, ZItem>(); // Stores the custom items we create, integer being the item id
	
	public HashMap<Integer, Element> Elements = new HashMap<Integer, Element>();
	
	public FileConfiguration config; // Config file variable
	
	private PlayerSave playerFile; // File for holding player data
	//public ElementFile elementFile; // This just saves custom element information
	
	public ElementIO elementFile; // This just saves custom element information
	public ItemIO itemFile; // The class that saves and loads all the item data
	public CraftingIO craftingFile; // The class saves and loads all the crafting recipes
	
	ArmorBlockedMatsFile armorBlockedMatsFile;
	
	public static final String PERMISSION_STR = "menuspells.open.menu"; // The string permission just for easy checking
	public static final String ADMIN_PERM_STR = "menuspells.admin"; // The string permission for admins, for easy checking
	public Permission menuPerm = new Permission(PERMISSION_STR); // A permission for opening the menu
	public Permission adminPerm = new Permission(ADMIN_PERM_STR); // A permission for admins
	
	public static boolean hasItemDurability = false;
	public static boolean canDebuffMobs = true;
	public static boolean addDamageFromItem = false;
	public static boolean addElementalDamageFromItem = false;
	
	private short defaultSlots = 5; // Determines the default amount of slots the player can use
	
	/*
	 * Important!
	 * ON ENABLE METHOD(Initialization)!
	 */
	@Override
	public void onEnable() {
		config = getConfig();
		playerFile = new PlayerSave(this);
		//elementFile = new ElementFile(this);
		
		// If the player data file doesn't exist, create one and save all the defaults, then we want to load our data, this way we get the defaults only when the file is created
		if (!(new File("plugins" + File.separator + "ZItems" + File.separator + "config.yml").exists())) 
			saveConfigData();
		
		loadConfigData();
		
		elementFile = new ElementIO(this);
		elementFile.Load();
		
		itemFile = new ItemIO(this);
		itemFile.Load();
		
		craftingFile = new CraftingIO(this);
		craftingFile.Load();
		
		armorBlockedMatsFile = new ArmorBlockedMatsFile(this);
		
		/* !!!! DEBUG INFO !!!! 
		 
		for(int key : customItems.keySet()) {
			System.out.println("-------------------------------");
			System.out.println("Item Name: " + customItems.get(key).itemName);
			System.out.println("Item Material: " + customItems.get(key).itemMaterial.toString());
			System.out.println("Item Combat Value: " + customItems.get(key).itemCombatValue);
			
			if(customItems.get(key).elements != null) {
				System.out.println("Item Element Type: " + customItems.get(key).elements.elementName);
				System.out.println("Item Element ID: " + customItems.get(key).elements.id);
			}
			
			System.out.println("Item Debuff Time: " + customItems.get(key).debuffTime);
			System.out.println("Item Durability: " + customItems.get(key).durability);
			
			for(Enchantment vEnchant : customItems.get(key).vEnchantments.keySet()) 
				System.out.println("Vanilla Item Enchantment: " + HelperFunctions.getEnchantName(vEnchant) + " | Strength: " + customItems.get(key).vEnchantments.get(vEnchant));
			
			
			for(EnchantTypes zEnchant : customItems.get(key).zEnchants.keySet()) 
				System.out.println("Custom Item Enchantment: " + zEnchant.toString() + " | Strength: " + customItems.get(key).zEnchants.get(zEnchant));
			
			if(customItems.get(key).itemLore.isEmpty()) {
				System.out.println("Lore is empty!");
			}
			
			for(String s : customItems.get(key).itemLore) {
				System.out.println("Item Lore: " + s);
			}
		}
		
		System.out.println("-------------------------------");
		
		for(int key : Elements.keySet()) {
			System.out.println("-------------------------------");
			System.out.println("Element Name: " + Elements.get(key).elementName);
			System.out.println("Element Combat Value: " + Elements.get(key).elementValue);
			System.out.println("Element Effective Multiplier: " + Elements.get(key).posDmgMultiplier);
			System.out.println("Element Weakness Multiplier: " + Elements.get(key).negDmgMultiplier);
			
			for(String s : Elements.get(key).effective) {
				System.out.println("Effective list: " + s);
			}
			
			for(String s : Elements.get(key).weak) {
				System.out.println("Weakness list: " + s);
			}
		}
		
		System.out.println("-------------------------------");
		/*
		customItems.put(99, new ZItem(99));
		customItems.get(99).itemName = "Shaped use four slots test";
		customItems.get(99).setItem(Material.WOOD_SWORD, 1);
		customItems.get(99).zCrafting.put(0, new ZCrafting(0));
		//customItems.get(99).zCrafting.setFourSlots(true);
		customItems.get(99).zCrafting.get(0).setShapeless(false);
		customItems.get(99).zCrafting.get(0).AddIngredient(0, new ItemStack(Material.STICK));
		
		customItems.put(100, new ZItem(100));
		customItems.get(100).itemName = "Shaped, 9 Slot test";
		customItems.get(100).setItem(Material.WOOD_SWORD, 1);
		customItems.get(100).zCrafting.put(0, new ZCrafting(0));
		//customItems.get(100).zCrafting.setFourSlots(false);
		customItems.get(100).zCrafting.get(0).setShapeless(false);
		customItems.get(100).zCrafting.get(0).AddIngredient(0, new ItemStack(Material.STICK));
		customItems.get(100).zCrafting.get(0).AddIngredient(3, new ItemStack(Material.STICK));
		
		customItems.put(101, new ZItem(101));
		customItems.get(101).itemName = "Use Shapeless Test";
		customItems.get(101).setItem(Material.WOOD_SWORD, 1);
		customItems.get(101).zCrafting.put(0, new ZCrafting(0));
		//customItems.get(101).zCrafting.setFourSlots(false);
		customItems.get(101).zCrafting.get(0).setShapeless(true);
		customItems.get(101).zCrafting.get(0).AddIngredient(0, new ItemStack(Material.STICK));
		customItems.get(101).zCrafting.get(0).AddIngredient(3, new ItemStack(Material.STICK));
		customItems.get(101).zCrafting.get(0).AddIngredient(6, new ItemStack(Material.STICK));
		
		customItems.put(102, new ZItem(102));
		customItems.get(102).itemName = "Use Four Slots & Shapeless Test";
		customItems.get(102).setItem(Material.WOOD_SWORD, 1);
		customItems.get(102).zCrafting.put(0, new ZCrafting(0));
		//customItems.get(102).zCrafting.setFourSlots(true);
		customItems.get(102).zCrafting.get(0).setShapeless(true);
		customItems.get(102).zCrafting.get(0).AddIngredient(0, new ItemStack(Material.STICK));
		customItems.get(102).zCrafting.get(0).AddIngredient(1, new ItemStack(Material.STICK));
		customItems.get(102).zCrafting.get(0).AddIngredient(2, new ItemStack(Material.STICK));
		customItems.get(102).zCrafting.get(0).AddIngredient(3, new ItemStack(Material.STICK));
		
		customItems.put(103, new ZItem(103));
		customItems.get(103).itemName = "Use Four Slots & Shapeless Test & Different item";
		customItems.get(103).setItem(Material.IRON_AXE, 1);
		customItems.get(103).zCrafting.put(0, new ZCrafting(0));
		//customItems.get(103).zCrafting.setFourSlots(true);
		customItems.get(103).zCrafting.get(0).setShapeless(true);
		customItems.get(103).zCrafting.get(0).AddIngredient(0, new ItemStack(Material.STICK));
		customItems.get(103).zCrafting.get(0).AddIngredient(1, new ItemStack(Material.STICK));
		customItems.get(103).zCrafting.get(0).AddIngredient(2, new ItemStack(Material.STICK));
		customItems.get(103).zCrafting.get(0).AddIngredient(3, new ItemStack(Material.DIAMOND));
		/*
		/*
		NamespacedKey key = new NamespacedKey(this, "emerald_block");
		ShapedRecipe recipe = new ShapedRecipe(key, new ItemStack(Material.EMERALD_BLOCK, 1));
		
		recipe.shape("   ", " S ", "   ");
		recipe.setIngredient('S', Material.DIAMOND_SWORD);
		Bukkit.addRecipe(recipe);
		*/
		
		getServer().getPluginManager().registerEvents(new ArmorListener(armorBlockedMatsFile.config.getStringList("blocked")), this);
		getServer().getPluginManager().registerEvents(new PlayerEvents(this), this); // Initialize our event listener 
	}

	/*
	 * IMPORTANT!
	 * ON DISABLE METHOD
	 */
	@Override
	public void onDisable() {
	}

	/*
	 * IMPORTANT!
	 * DOES COMMAND STUFF!
	 */
	public boolean onCommand(CommandSender sender, Command cmd, String Label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("ZMenu")) { // Check the command name 
			CommandHandler.MenuCommands(this, sender, args);; // Edit this method later when we have the menu created
		}
		else if(cmd.getName().equalsIgnoreCase("ZItems")) {
			CommandHandler.ZItemCommands(this, sender, args);
		}
		
		return true;
	}
	
	/* 
	 * --- Config Functions ---
	 */
	
	// Save config data
	public void saveConfigData() {
		config.set("Default-Available-Slots", defaultSlots); // Set the default amount of slots
		config.set("Items-Have-Durability", hasItemDurability); // Save the variable to check if items have durability
		config.set("Can-debuff-mobs", canDebuffMobs);
		config.set("Add-Damage-From-Item-Material", addDamageFromItem);
		config.set("Add-Elemental-Damage-From-Item-Material", addElementalDamageFromItem);
		
		
		saveConfig();
	}
	
	// Load config data
	public void loadConfigData() {
		defaultSlots = (short)config.getInt("Default-Available-Slots"); // Set our default slot variable
		hasItemDurability = config.getBoolean("Items-Have-Durability"); // Set the variable that determines if we have item durability
		canDebuffMobs = config.getBoolean("Can-debuff-mobs");
		addDamageFromItem = config.getBoolean("Add-Damage-From-Item-Material");
		addElementalDamageFromItem = config.getBoolean("Add-Elemental-Damage-From-Item-Material");
	}
	
	/*
	 * --- Player Functions ---
	 */
	
	// Save player data
	public void savePlayerData(UUID playerUUID) {
		playerFile.config.set(playerUUID + ".Available-Slots", (int)playerData.get(playerUUID).getAvailableSlots()); // Save the player's available slots
	}	
	
	// Load player data
	public void loadPlayerData(UUID playerUUID) {
		playerData.get(playerUUID).setAvailableSlots((short)playerFile.config.getInt(playerUUID + ".Available-Slots")); // Load in the player's available slots
		
		
	}

	// Check if the target is online, using the player's name
	public boolean targetIsOnline(String target) {
		for(Player p : Bukkit.getOnlinePlayers()) // Loop through the online players.
			if(p.getName().equalsIgnoreCase(target)) // Get the target's name
				return true;

		return false;
	}
	
	// A function to get a target player via string
		public Player getTarget(String target) {
			if(targetIsOnline(target)) // Check if the target is online
				return Bukkit.getPlayer(target); // Return target's name
			else {
				System.out.println(ChatColor.RED + "That player is offline!");
				return null; // Return nothing.
			}
		}
	
	// A function to get a target player via string
	public Player getTarget(String target, CommandSender sender) {
		if(targetIsOnline(target)) // Check if the target is online
			return Bukkit.getPlayer(target); // Return target's name
		else {
			System.out.println(ChatColor.RED + "That player is offline!");
			return null; // Return nothing.
		}
	}
}
