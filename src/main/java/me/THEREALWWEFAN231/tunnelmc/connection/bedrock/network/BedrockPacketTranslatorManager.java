package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network;

import com.nukkitx.protocol.bedrock.BedrockPacket;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators.*;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators.entity.*;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators.inventory.ContainerCloseTranslator;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators.inventory.ContainerOpenTranslator;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators.inventory.InventoryContentTranslator;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators.inventory.InventorySlotTranslator;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators.world.*;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketTranslatorManager;

public class BedrockPacketTranslatorManager extends PacketTranslatorManager<BedrockPacket> {

	public BedrockPacketTranslatorManager(BedrockConnection bedrockConnection) {
		super(bedrockConnection);
		this.addTranslator(new AddEntityTranslator());
		this.addTranslator(new AddItemEntityTranslator());
		this.addTranslator(new AddPlayerTranslator());
		this.addTranslator(new AnimateTranslator());
		this.addTranslator(new BlockEntityDataTranslator());
		this.addTranslator(new ChunkRadiusUpdatedTranslator());
		this.addTranslator(new ContainerCloseTranslator());
		this.addTranslator(new ContainerOpenTranslator());
		this.addTranslator(new DisconnectTranslator());
		this.addTranslator(new EntityEventTranslator());
		this.addTranslator(new GameRulesChangedTranslator());
		this.addTranslator(new InventoryContentTranslator());
		this.addTranslator(new InventorySlotTranslator());
		this.addTranslator(new LevelChunkTranslator());
		this.addTranslator(new LevelEventTranslator());
		this.addTranslator(new LevelSoundEvent2Translator());
		this.addTranslator(new LevelSoundEventTranslator());
		this.addTranslator(new MobArmorEquipmentTranslator());
		this.addTranslator(new MobEffectTranslator());
		this.addTranslator(new MobEquipmentTranslator());
		this.addTranslator(new MoveEntityAbsoluteTranslator());
		this.addTranslator(new MovePlayerTranslator());
		this.addTranslator(new NetworkChunkPublisherUpdateTranslator());
		this.addTranslator(new NetworkSettingsTranslator());
		this.addTranslator(new NetworkStackLatencyTranslator());
		this.addTranslator(new PlayStatusTranslator());
		this.addTranslator(new PlayerListTranslator());
		this.addTranslator(new PlayerSkinTranslator());
		this.addTranslator(new RemoveEntityTranslator());
		this.addTranslator(new ResourcePackStackTranslator());
		this.addTranslator(new ResourcePacksInfoTranslator());
		this.addTranslator(new RespawnTranslator());
		this.addTranslator(new ServerToClientHandshakeTranslator());
		this.addTranslator(new SetEntityDataTranslator());
		this.addTranslator(new SetEntityMotionTranslator());
		this.addTranslator(new SetPlayerGameTypeTranslator());
		this.addTranslator(new SetTimeTranslator());
		this.addTranslator(new StartGameTranslator());
		this.addTranslator(new TakeItemEntityTranslator());
		this.addTranslator(new TextTranslator());
		this.addTranslator(new UpdateAbilitiesTranslator());
		this.addTranslator(new UpdateAttributesTranslator());
		this.addTranslator(new UpdateBlockTranslator());
		this.addTranslator(new UpdatePlayerGameTypeTranslator());
	}
}