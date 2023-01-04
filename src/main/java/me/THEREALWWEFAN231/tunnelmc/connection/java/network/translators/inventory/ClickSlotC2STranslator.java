package me.THEREALWWEFAN231.tunnelmc.connection.java.network.translators.inventory;

import com.nukkitx.protocol.bedrock.data.inventory.InventoryActionData;
import com.nukkitx.protocol.bedrock.data.inventory.InventorySource;
import com.nukkitx.protocol.bedrock.data.inventory.ItemData;
import com.nukkitx.protocol.bedrock.data.inventory.TransactionType;
import com.nukkitx.protocol.bedrock.packet.InventoryTransactionPacket;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import lombok.extern.log4j.Log4j2;
import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.caches.container.BedrockContainer;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.caches.container.BedrockContainers;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.translator.container.screenhandler.ScreenHandlerTranslatorManager;
import me.THEREALWWEFAN231.tunnelmc.translator.item.ItemTranslator;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketTranslator;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.ScreenHandler;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@PacketIdentifier(ClickSlotC2SPacket.class)
public class ClickSlotC2STranslator extends PacketTranslator<ClickSlotC2SPacket> {

	@Override
	public void translate(ClickSlotC2SPacket packet, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
//		For debugging
//		try {
//			log.info(JSON_MAPPER.writeValueAsString(packet));
//		} catch (JsonProcessingException e) {
//			throw new RuntimeException(e);
//		}

		InventoryTransactionPacket pk = new InventoryTransactionPacket();
		pk.setTransactionType(TransactionType.NORMAL);

		Integer containerIdForClickedSlot = ScreenHandlerTranslatorManager.getBedrockContainerIdFromJava(TunnelMC.mc.player.currentScreenHandler, packet.getSlot());
		if (containerIdForClickedSlot == null) {
			return;
		}

		List<InventoryActionData> actions = switch (packet.getActionType()) {
			case PICKUP -> translatePickup(packet, containerIdForClickedSlot, bedrockConnection);
			case PICKUP_ALL -> null;
			case QUICK_MOVE -> translateQuickMove(packet, containerIdForClickedSlot, bedrockConnection);
			case SWAP -> translateSwap(packet, containerIdForClickedSlot, bedrockConnection);
			case THROW -> null;
			case CLONE -> null;
			case QUICK_CRAFT -> null;
		};
		if(actions == null) {
			return; // TODO: add reverting client-side
		}
		pk.getActions().addAll(actions);

		bedrockConnection.sendPacket(pk);
	}

	private List<InventoryActionData> translatePickup(ClickSlotC2SPacket packet, int containerId, BedrockConnection bedrockConnection) {
		List<InventoryActionData> actions = new ArrayList<>();
		BedrockContainer container = bedrockConnection.getWrappedContainers().getContainer(containerId);
		BedrockContainer cursorContainer = bedrockConnection.getWrappedContainers().getContainer(BedrockContainers.PLAYER_CONTAINER_CURSOR_COTNAINER_ID);

		for(Int2ObjectMap.Entry<ItemStack> entry : packet.getModifiedStacks().int2ObjectEntrySet()) {
			Integer slotId = ScreenHandlerTranslatorManager.getBedrockSlotFromJavaContainer(TunnelMC.mc.player.currentScreenHandler, entry.getIntKey());
			if (slotId == null) {
				continue;
			}
			ItemData fromItemData = container.getItemFromSlot(slotId);
			ItemData toItemData = ItemTranslator.itemStackToItemData(entry.getValue());

			actions.add(new InventoryActionData(InventorySource.fromContainerWindowId(containerId), slotId, fromItemData, toItemData));
			container.setItemBedrock(slotId, toItemData);
		}

		ItemData newCursorItem = ItemTranslator.itemStackToItemData(packet.getStack());
		actions.add(new InventoryActionData(InventorySource.fromContainerWindowId(BedrockContainers.PLAYER_CONTAINER_CURSOR_COTNAINER_ID),
				0, cursorContainer.getItemFromSlot(0), newCursorItem));
		cursorContainer.setItemBedrock(0, newCursorItem);

		return actions;
	}

	private List<InventoryActionData> translateQuickMove(ClickSlotC2SPacket packet, int containerId, BedrockConnection bedrockConnection) {
		List<InventoryActionData> actions = new ArrayList<>();
		BedrockContainer container = bedrockConnection.getWrappedContainers().getContainer(containerId);
		Integer slotId = ScreenHandlerTranslatorManager.getBedrockSlotFromJavaContainer(TunnelMC.mc.player.currentScreenHandler, packet.getSlot());
		if (slotId == null) {
			return null;
		}

		for(Int2ObjectMap.Entry<ItemStack> entry : packet.getModifiedStacks().int2ObjectEntrySet()) {
			Integer modifiedContainerId = ScreenHandlerTranslatorManager.getBedrockContainerIdFromJava(TunnelMC.mc.player.currentScreenHandler, entry.getIntKey());
			if (modifiedContainerId == null) {
				return null;
			}
			BedrockContainer modifiedContainer = bedrockConnection.getWrappedContainers().getContainer(modifiedContainerId);
			Integer modifiedSlotId = ScreenHandlerTranslatorManager.getBedrockSlotFromJavaContainer(TunnelMC.mc.player.currentScreenHandler, entry.getIntKey());
			if (modifiedSlotId == null) {
				continue;
			}

			ItemData fromItemData = modifiedContainer.getItemFromSlot(modifiedSlotId);
			ItemData toItemData = ItemTranslator.itemStackToItemData(entry.getValue());

			actions.add(new InventoryActionData(InventorySource.fromContainerWindowId(modifiedContainerId), modifiedSlotId, fromItemData, toItemData));
			modifiedContainer.setItemBedrock(modifiedSlotId, toItemData);
		}

		ItemData newFromItemData = ItemTranslator.itemStackToItemData(packet.getStack());
		actions.add(new InventoryActionData(InventorySource.fromContainerWindowId(containerId),
				slotId, container.getItemFromSlot(slotId), newFromItemData));
		container.setItemBedrock(slotId, newFromItemData);

		return actions;
	}

	private List<InventoryActionData> translateSwap(ClickSlotC2SPacket packet, int fromContainerId, BedrockConnection bedrockConnection) {
		List<InventoryActionData> actions = new ArrayList<>();
		BedrockContainer fromContainer = bedrockConnection.getWrappedContainers().getContainer(fromContainerId);
		Integer fromSlotId = ScreenHandlerTranslatorManager.getBedrockSlotFromJavaContainer(TunnelMC.mc.player.currentScreenHandler, packet.getSlot());

		Integer toContainerId = null;
		BedrockContainer toContainer = null;
		Integer toSlotId = null;

		if (packet.getModifiedStacks().size() != 2) {
			return null;
		}
		for (Int2ObjectMap.Entry<ItemStack> entry : packet.getModifiedStacks().int2ObjectEntrySet()) {
			if (packet.getSlot() == entry.getIntKey()) {
				continue;
			}

			toContainerId = ScreenHandlerTranslatorManager.getBedrockContainerIdFromJava(TunnelMC.mc.player.currentScreenHandler, entry.getIntKey());
			if (toContainerId == null) {
				return null;
			}
			toContainer = bedrockConnection.getWrappedContainers().getContainer(toContainerId);
			toSlotId = ScreenHandlerTranslatorManager.getBedrockSlotFromJavaContainer(TunnelMC.mc.player.currentScreenHandler, entry.getIntKey());
		}
		if (fromSlotId == null || toSlotId == null) {
			return null;
		}

		ItemData fromItemData = fromContainer.getItemFromSlot(fromSlotId);
		ItemData toItemData = toContainer.getItemFromSlot(toSlotId);

		actions.add(new InventoryActionData(InventorySource.fromContainerWindowId(fromContainerId),
				fromSlotId, fromItemData, toItemData));
		actions.add(new InventoryActionData(InventorySource.fromContainerWindowId(toContainerId),
				toSlotId, toItemData, fromItemData));
		fromContainer.setItemBedrock(fromSlotId, toItemData);
		toContainer.setItemBedrock(toSlotId, fromItemData);

		return actions;
	}

	public void onCursorStackAddToStack(ScreenHandler screenHandler, int clickedSlotId) {//for example the user has 64 oak planks in the cursor, and they right-click a slot with oak planks(not an empty slot)
		/*InventoryTransactionPacket inventoryTransactionPacket = new InventoryTransactionPacket();
		
		inventoryTransactionPacket.setTransactionType(TransactionType.NORMAL);
		inventoryTransactionPacket.setActionType(0);//I have no idea
		inventoryTransactionPacket.setRuntimeEntityId(TunnelMC.mc.player.getEntityId());
		
		BedrockContainer containerForClickedSlot = JavaContainerFinder.getContainerFromJava(screenHandler, clickedSlotId);
		
		if (containerForClickedSlot == null) {
			System.out.println("FIX THIS, unknown slot clicked " + clickedSlotId);
			return;
		}
		
		int bedrockSlotId = containerForClickedSlot.convertJavaSlotIdToBedrockSlotId(clickedSlotId);
		bedrockSlotId = JavaContainerFinder.getBedrockSlotId(screenHandler, clickedSlotId, containerForClickedSlot);
		
		ItemData droppedSlotItemData = containerForClickedSlot.getItemFromSlot(bedrockSlotId);
		ItemData afterDropSlotItemData = null;
		if (clickData == 0) {//1 item is dropped
			afterDropSlotItemData = ItemDataUtils.copyWithCount(droppedSlotItemData, droppedSlotItemData.getCount() - 1);
		} else {//all items
			afterDropSlotItemData = ItemData.AIR;
		}
		
		{
			InventoryActionData decreaseClickedStack = new InventoryActionData(InventorySource.fromContainerWindowId(containerForClickedSlot.getId()), bedrockSlotId, droppedSlotItemData, afterDropSlotItemData);
			inventoryTransactionPacket.getActions().add(decreaseClickedStack);
		}
		
		{
			int droppedItemCount = clickData == 0 ? 1 : droppedSlotItemData.getCount();
			ItemData itemDroppedInTheWorld = ItemDataUtils.copyWithCount(droppedSlotItemData, droppedItemCount);
		
			InventoryActionData dropItemInWorld = new InventoryActionData(InventorySource.fromWorldInteraction(Flag.DROP_ITEM), 0, ItemData.AIR, itemDroppedInTheWorld);
			inventoryTransactionPacket.getActions().add(dropItemInWorld);
		}
		
		containerForClickedSlot.setItemBedrock(bedrockSlotId, afterDropSlotItemData);
		
		client.sendPacket(inventoryTransactionPacket);*/
	}

//	@Listener
//	public void onEvent(PlaceStackOnEmptySlotEvent event) {
//		if (TunnelMC.mc.player == null) {
//			return;
//		}
//		BedrockConnection bedrockConnection = BedrockConnectionAccessor.getCurrentConnection();
//		int clickedSlotId = event.getSlotIndex();
//
//		InventoryTransactionPacket inventoryTransactionPacket = new InventoryTransactionPacket();
//		inventoryTransactionPacket.setTransactionType(TransactionType.NORMAL);
//		inventoryTransactionPacket.setActionType(0);
//		inventoryTransactionPacket.setRuntimeEntityId(TunnelMC.mc.player.getId());
//
//		BedrockContainer cursorContainer = bedrockConnection.getWrappedContainers().getPlayerContainerCursorContainer();
//		Integer containerIdForClickedSlot = ScreenHandlerTranslatorManager.getBedrockContainerIdFromJava(event.getScreenHandler(), clickedSlotId);
//		if (containerIdForClickedSlot == null) {
//			return;
//		}
//		BedrockContainer containerForClickedSlot = bedrockConnection.getWrappedContainers().getContainer(containerIdForClickedSlot);
//		Integer bedrockSlotId = ScreenHandlerTranslatorManager.getBedrockSlotFromJavaContainer(event.getScreenHandler(), clickedSlotId);
//		if (bedrockSlotId == null) {
//			return;
//		}
//		ItemData cursorItemData = cursorContainer.getItemFromSlot(0);
//
//		// Decrease if the user right-clicked a slot, change to air if they left-clicked a slot.
//		ItemData decreasedCursorStack = ItemDataUtils.copyWithCount(cursorItemData, cursorItemData.getCount() - event.getCount());
//		if (decreasedCursorStack.getCount() == 0) {
//			decreasedCursorStack = ItemData.AIR;
//		}
//
//		InventoryActionData decreaseCursorStack = new InventoryActionData(InventorySource.fromContainerWindowId(BedrockContainers.PLAYER_CONTAINER_CURSOR_COTNAINER_ID), 0, cursorItemData, decreasedCursorStack);
//		inventoryTransactionPacket.getActions().add(decreaseCursorStack);
//		cursorContainer.setItemBedrock(0, decreasedCursorStack);
//
//		ItemData clickedSlotNewItemData = ItemDataUtils.copyWithCount(cursorItemData, event.getCount());
//
//		// Changes it to the cursor slot stack.
//		InventoryActionData incrementClickedSlotWithCursorStack = new InventoryActionData(InventorySource.fromContainerWindowId(containerIdForClickedSlot), bedrockSlotId, ItemData.AIR, clickedSlotNewItemData);
//		inventoryTransactionPacket.getActions().add(incrementClickedSlotWithCursorStack);
//		containerForClickedSlot.setItemBedrock(bedrockSlotId, clickedSlotNewItemData);
//
//		bedrockConnection.sendPacket(inventoryTransactionPacket);
//	}
//
//	@Listener
//	public void onEvent(TakeSlotEvent event) {
//		if (TunnelMC.mc.player == null) {
//			return;
//		}
//		BedrockConnection bedrockConnection = BedrockConnectionAccessor.getCurrentConnection();
//		int clickedSlotId = event.getSlotIndex();
//
//		InventoryTransactionPacket inventoryTransactionPacket = new InventoryTransactionPacket();
//		inventoryTransactionPacket.setTransactionType(TransactionType.NORMAL);
//		inventoryTransactionPacket.setActionType(0);//I have no idea
//		inventoryTransactionPacket.setRuntimeEntityId(TunnelMC.mc.player.getId());
//
//		BedrockContainer cursorContainer = bedrockConnection.getWrappedContainers().getPlayerContainerCursorContainer();
//		Integer containerIdForClickedSlot = ScreenHandlerTranslatorManager.getBedrockContainerIdFromJava(event.getScreenHandler(), clickedSlotId);
//		if (containerIdForClickedSlot == null) {
//			return;
//		}
//		BedrockContainer containerForClickedSlot = bedrockConnection.getWrappedContainers().getContainer(containerIdForClickedSlot);
//		Integer bedrockSlotId = ScreenHandlerTranslatorManager.getBedrockSlotFromJavaContainer(event.getScreenHandler(), clickedSlotId);
//		if (bedrockSlotId == null) {
//			return;
//		}
//		ItemData clickedSlotItemData = containerForClickedSlot.getItemFromSlot(bedrockSlotId);
//
//		InventoryActionData changeClickedStackToAir = new InventoryActionData(InventorySource.fromContainerWindowId(containerIdForClickedSlot), bedrockSlotId, clickedSlotItemData, ItemData.AIR);
//		inventoryTransactionPacket.getActions().add(changeClickedStackToAir);
//		containerForClickedSlot.setItemBedrock(bedrockSlotId, ItemData.AIR);
//
//		InventoryActionData moveClickedStackToCursorContainer = new InventoryActionData(InventorySource.fromContainerWindowId(BedrockContainers.PLAYER_CONTAINER_CURSOR_COTNAINER_ID), 0, ItemData.AIR, clickedSlotItemData);
//		inventoryTransactionPacket.getActions().add(moveClickedStackToCursorContainer);
//		cursorContainer.setItemBedrock(0, clickedSlotItemData);
//
//		bedrockConnection.sendPacket(inventoryTransactionPacket);
//	}
//
//	@Listener
//	public void onEvent(DropSlotEvent event) {
//		if (TunnelMC.mc.player == null) {
//			return;
//		}
//		BedrockConnection bedrockConnection = BedrockConnectionAccessor.getCurrentConnection();
//		int clickedSlotId = event.getSlotIndex();
//
//		InventoryTransactionPacket inventoryTransactionPacket = new InventoryTransactionPacket();
//		inventoryTransactionPacket.setTransactionType(TransactionType.NORMAL);
//		inventoryTransactionPacket.setActionType(0);//I have no idea
//		inventoryTransactionPacket.setRuntimeEntityId(TunnelMC.mc.player.getId());
//
//		Integer containerIdForClickedSlot = ScreenHandlerTranslatorManager.getBedrockContainerIdFromJava(event.getScreenHandler(), clickedSlotId);
//		if (containerIdForClickedSlot == null) {
//			return;
//		}
//		BedrockContainer containerForClickedSlot = bedrockConnection.getWrappedContainers().getContainer(containerIdForClickedSlot);
//
//		Integer bedrockSlotId = ScreenHandlerTranslatorManager.getBedrockSlotFromJavaContainer(event.getScreenHandler(), clickedSlotId);
//		if (bedrockSlotId == null) {
//			return;
//		}
//
//		ItemData droppedSlotItemData = containerForClickedSlot.getItemFromSlot(bedrockSlotId);
//		ItemData afterDropSlotItemData;
//		if (event.getButton() == 0) {//1 item is dropped
//			afterDropSlotItemData = ItemDataUtils.copyWithCount(droppedSlotItemData, droppedSlotItemData.getCount() - 1);
//		} else {//all items
//			afterDropSlotItemData = ItemData.AIR;
//		}
//		InventoryActionData decreaseClickedStack = new InventoryActionData(InventorySource.fromContainerWindowId(containerIdForClickedSlot), bedrockSlotId, droppedSlotItemData, afterDropSlotItemData);
//		inventoryTransactionPacket.getActions().add(decreaseClickedStack);
//
//		int droppedItemCount = event.getButton() == 0 ? 1 : droppedSlotItemData.getCount();
//		ItemData itemDroppedInTheWorld = ItemDataUtils.copyWithCount(droppedSlotItemData, droppedItemCount);
//		InventoryActionData dropItemInWorld = new InventoryActionData(InventorySource.fromWorldInteraction(Flag.DROP_ITEM), 0, ItemData.AIR, itemDroppedInTheWorld);
//		inventoryTransactionPacket.getActions().add(dropItemInWorld);
//		bedrockConnection.sendPacket(inventoryTransactionPacket);
//
//		containerForClickedSlot.setItemBedrock(bedrockSlotId, afterDropSlotItemData);
//	}
}