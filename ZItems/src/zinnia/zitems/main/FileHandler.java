package zinnia.zitems.main;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class FileHandler {
	
	protected ZItemMain plugin;
	public File file;
	public FileConfiguration config;
	
	private String fileName;
	
	// Try to create a file
	public FileHandler(ZItemMain plugin, String fileName) {
		this.plugin = plugin;
		this.file = new File(plugin.getDataFolder(), fileName);
		
		this.fileName = fileName;
		
		if(!file.exists())
			try{
				file.createNewFile();
			} catch(IOException e){
				e.printStackTrace();
			}
		this.config = YamlConfiguration.loadConfiguration(file);
	}
	
	// Try to save things to the file
	public void Save() {
		try{
			config.save(file);
		} catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public String GetFileName() {
		return fileName;
	}
}
