package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators.entity;

import com.nukkitx.protocol.bedrock.packet.TakeItemEntityPacket;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketTranslator;
import net.minecraft.network.packet.s2c.play.ItemPickupAnimationS2CPacket;

@PacketIdentifier(TakeItemEntityPacket.class)
public class TakeItemEntityTranslator extends PacketTranslator<TakeItemEntityPacket> {

	@Override
	public void translate(TakeItemEntityPacket packet, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
		int entityId = (int) packet.getItemRuntimeEntityId();
		int collectorId = (int) packet.getRuntimeEntityId();
		int stackAmount = 1; // TODO: This needs the correct value but we can probably get the value from the item entity in the world.

		ItemPickupAnimationS2CPacket itemPickupAnimationS2CPacket = new ItemPickupAnimationS2CPacket(entityId, collectorId, stackAmount);

		javaConnection.processJavaPacket(itemPickupAnimationS2CPacket);
	}
}
