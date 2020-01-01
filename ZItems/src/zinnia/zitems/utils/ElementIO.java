package zinnia.zitems.utils;

import java.io.File;
import java.io.FileInputStream;
import java.util.LinkedList;
import java.util.Scanner;

import org.bukkit.configuration.ConfigurationSection;

import zinnia.zitems.main.ElementFile;
import zinnia.zitems.main.ZItemMain;

public class ElementIO {
	
	LinkedList<SectionIDPair> elementSection = new LinkedList<SectionIDPair>();
	LinkedList<SectionIDPair> effectiveSection = new LinkedList<SectionIDPair>();
	LinkedList<SectionIDPair> weaknessSection = new LinkedList<SectionIDPair>();
	
	ZItemMain plugin;
	
	private ElementFile elementFile; // Of course this is our element file
		
	public ElementIO(ZItemMain plugin) {
		this.plugin = plugin;
		
		elementFile = new ElementFile(plugin); // Create the file
	}
	
	public void Save() {
		for(Integer key : plugin.Elements.keySet()) {
			elementFile.config.set("Element-" + key + ".Name", plugin.Elements.get(key).elementName);
			elementFile.config.set("Element-" + key + ".Combat-Value", plugin.Elements.get(key).elementValue);
			elementFile.config.set("Element-" + key + ".Effective-Type-Multiplier", plugin.Elements.get(key).posDmgMultiplier);
			elementFile.config.set("Element-" + key + ".Non-Effective-Type-Multiplier", plugin.Elements.get(key).negDmgMultiplier);
			
			for(int i = 0; i < plugin.Elements.get(key).effective.size(); i++) {
				elementFile.config.set("Element-" + plugin.Elements.get(key).id + ".effective.line-" + Integer.toString(i), plugin.Elements.get(key).effective.get(i));
			}
			
			for(int i = 0; i < plugin.Elements.get(key).weak.size(); i++) {
				elementFile.config.set("Element-" + plugin.Elements.get(key).id + ".weakness.line-" + Integer.toString(i), plugin.Elements.get(key).weak.get(i));
			}
		}
		
		elementFile.Save();
	}
	
	public void Load() {
		// Oh no here we go again
		int id = -1;
		
		try {
			FileInputStream fs = new FileInputStream(plugin.getDataFolder() + File.separator + elementFile.GetFileName());
			Scanner s = new Scanner(fs);
			
			while(s.hasNextLine()) { 
				String line = s.nextLine();
								
				// If the line is a custom item
				if((line.contains("Element-") || line.contains("Element-")) && !line.contains("combat")) 
				{
					// Remove the colon, it'll cause problems with the config section
					line = line.substring(0, line.indexOf(':')); 
					
					id = Integer.parseInt(line.substring(line.indexOf('-') + 1)); // Get the element id out of the current line
					elementSection.add(new SectionIDPair(elementFile.config.getConfigurationSection(line), id)); // Add the config section with the id attached to it
										
					plugin.Elements.put(id, new Element(id)); // add the element to the element hashmap
				}
				
				// Add the the elements this element is effective to
				if(line.contains("effective")) {
					line = line.trim(); // Make sure there's no trailing or leading spaces
					line = "Element-" + id + "." + line.substring(0, line.indexOf(':')); // Remove the colon
					effectiveSection.add(new SectionIDPair(elementFile.config.getConfigurationSection(line), id)); // add the config section with an element id
				}
				
				// Add the weakness to the element
				if(line.contains("weakness")) {
					line = line.trim(); // Make sure there's no trailing or leading spaces
					line = "Element-" + id + "." + line.substring(0, line.indexOf(':')); // Remove the colon
					weaknessSection.add(new SectionIDPair(elementFile.config.getConfigurationSection(line), id)); // add the config section with an element id
				}
			}
			
			s.close(); // Close the scanner
			fs.close(); // close the file stream
		} catch (Exception e) {
			System.out.println("Error reading file!");
			e.printStackTrace();
		}
		
		// Fill in the element variables
		for(SectionIDPair section : elementSection) {
			elementData(section.section, section.itemID); // Do all the easy element stuff

			addElementEffectiveness(section.itemID); // add the element effectiveness
		}
	}
	
	private void elementData(ConfigurationSection config, int elementID) {	
		if(config != null) {
			// Load in all the basic data
			plugin.Elements.get(elementID).elementName = config.getString("Name");
			plugin.Elements.get(elementID).elementValue = config.getInt("Combat-Value");
			plugin.Elements.get(elementID).posDmgMultiplier = config.getDouble("Effective-Type-Multiplier");
			plugin.Elements.get(elementID).negDmgMultiplier = config.getDouble("Non-Effective-Type-Multiplier");
		}
	}
	
	private void addElementEffectiveness(int elementID) {
		
		// Loop through all the effective element config sections, if they're not empty
		if(!effectiveSection.isEmpty()) {
			for(SectionIDPair section : effectiveSection) {
				if(elementID == section.itemID) { // Make sure the section's element id and the current one are the same
					if(section.section != null) { // make sure the section exists
						for(String key : section.section.getValues(false).keySet()) { // Add every effective element within the section
							plugin.Elements.get(elementID).effective.add((String) section.section.getValues(false).get(key)); 
						}
					}
				}
			}
		}
		
		if(!weaknessSection.isEmpty()) {
			for(SectionIDPair section : weaknessSection) {
				if(elementID == section.itemID) { // Make sure the section's element id and the current one are the same
					if(section.section != null) { // make sure the section exists
						for(String key : section.section.getValues(false).keySet()) { // Add every weakness element within the section
							plugin.Elements.get(elementID).weak.add((String) section.section.getValues(false).get(key)); 
						}
					}
				}
			}
		}
	}
	
	public void RemoveElement(int elemID) {
		elementFile.config.set("Element-" + Integer.toString(elemID), null);
	}
	
	public void RemoveElementStrength(int elemID, int line) {
		elementFile.config.set("Element-" + Integer.toString(elemID) + ".effective.line-" + Integer.toString(line), null);
	}
	
	public void RemoveElementWeakness(int elemID, int line) {
		elementFile.config.set("Element-" + Integer.toString(elemID) + ".weakness.line-" + Integer.toString(line), null);
	}
	
}
