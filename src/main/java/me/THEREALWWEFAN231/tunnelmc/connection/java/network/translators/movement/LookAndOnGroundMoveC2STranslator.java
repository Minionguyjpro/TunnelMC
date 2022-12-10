package me.THEREALWWEFAN231.tunnelmc.connection.java.network.translators.movement;

import com.nukkitx.protocol.bedrock.packet.MovePlayerPacket;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.connection.PacketTranslator;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.LookAndOnGround;

@PacketIdentifier(LookAndOnGround.class)
public class LookAndOnGroundMoveC2STranslator extends PacketTranslator<PlayerMoveC2SPacket.LookAndOnGround> {

	@Override
	public void translate(LookAndOnGround packet, BedrockConnection bedrockConnection) {
		PlayerMoveC2STranslator.translateMovementPacket(packet, MovePlayerPacket.Mode.HEAD_ROTATION, bedrockConnection);
	}
}
