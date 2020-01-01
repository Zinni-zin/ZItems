package zinnia.zitems.utils;

import org.bukkit.configuration.ConfigurationSection;

public class SectionIDPair {

	public ConfigurationSection section;
	public int itemID = -1;
	public int miscID = -1;
	
	SectionIDPair(ConfigurationSection section, int itemID) {
		this.section = section;
		this.itemID = itemID;
	}
}
