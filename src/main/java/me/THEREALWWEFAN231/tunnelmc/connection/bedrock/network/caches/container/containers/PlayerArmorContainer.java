package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.caches.container.containers;

import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.translator.item.ItemTranslator;
import net.minecraft.item.ItemStack;

public class PlayerArmorContainer extends GenericContainer {
	private static final int SIZE = 4;

	public PlayerArmorContainer() {
		super(PlayerArmorContainer.SIZE);
	}

	@Override
	public boolean isStatic() {
		return true;
	}

	@Override
	public int getJavaSlotId(int bedrockSlotId) {
		return bedrockSlotId + 5;
	}

	@Override
	public int getBedrockSlotId(int javaSlotId) {
		return javaSlotId - 5;
	}

	@Override
	public void updateInventory() {
		for (int i = 0; i < this.getSize(); i++) {
			ItemStack stack = ItemTranslator.itemDataToItemStack(this.getItemFromSlot(i));
			TunnelMC.mc.player.playerScreenHandler.getSlot(this.getJavaSlotId(i)).setStack(stack);
		}
	}
}
