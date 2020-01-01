package zinnia.zitems.player;

public class PlayerData {
	
	private short availableSlots = 5; // A way to determine the amount of slots the player can have
	
	// Available Slots getter and setter
	public void setAvailableSlots(short slots) {
		availableSlots = slots;
	}
	
	public short getAvailableSlots() {
		return availableSlots;
	}
}
