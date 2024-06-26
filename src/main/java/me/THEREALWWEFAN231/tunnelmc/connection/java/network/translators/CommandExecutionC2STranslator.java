package me.THEREALWWEFAN231.tunnelmc.connection.java.network.translators;

import com.nukkitx.protocol.bedrock.data.command.CommandOriginData;
import com.nukkitx.protocol.bedrock.data.command.CommandOriginType;
import com.nukkitx.protocol.bedrock.packet.CommandRequestPacket;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketTranslator;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;

@PacketIdentifier(CommandExecutionC2SPacket.class)
public class CommandExecutionC2STranslator extends PacketTranslator<CommandExecutionC2SPacket> {

	@Override
	public void translate(CommandExecutionC2SPacket packet, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
		CommandRequestPacket commandPacket = new CommandRequestPacket();
		commandPacket.setCommand("/" + packet.command());
		commandPacket.setCommandOriginData(new CommandOriginData(CommandOriginType.PLAYER, bedrockConnection.getAuthData().identity(), "", 0));

		bedrockConnection.sendPacket(commandPacket);
	}
}
