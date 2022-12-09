package me.THEREALWWEFAN231.tunnelmc.connection.java.network.translators;

import com.nukkitx.protocol.bedrock.packet.ContainerClosePacket;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketTranslator;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.Client;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;

@PacketIdentifier(CloseHandledScreenC2SPacket.class)
public class CloseHandledScreenC2SPacketTranslator extends PacketTranslator<CloseHandledScreenC2SPacket> {

	@Override
	public void translate(CloseHandledScreenC2SPacket packet, Client client) {
		byte id = (byte) packet.getSyncId();
		if (id == 0) {
			// The main inventory being closed does not send a container close packet.
			// Sending this on PocketMine servers also crashes the client.
			return;
		}
		ContainerClosePacket containerClosePacket = new ContainerClosePacket();
		containerClosePacket.setId(id);
		
		client.sendPacket(containerClosePacket);
	}
}