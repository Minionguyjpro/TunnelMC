package me.THEREALWWEFAN231.tunnelmc.connection.java.network.translators;

import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.protocol.bedrock.data.AuthoritativeMovementMode;
import com.nukkitx.protocol.bedrock.data.PlayerActionType;
import com.nukkitx.protocol.bedrock.data.PlayerAuthInputData;
import com.nukkitx.protocol.bedrock.packet.PlayerActionPacket;
import me.THEREALWWEFAN231.tunnelmc.TunnelMC;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketTranslator;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;

@PacketIdentifier(ClientCommandC2SPacket.class)
public class ClientCommandC2STranslator extends PacketTranslator<ClientCommandC2SPacket> {

	@Override
	public void translate(ClientCommandC2SPacket packet, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
		if (TunnelMC.mc.player == null) {
			return;
		}

		PlayerActionType actionType = switch (packet.getMode()) {
			case PRESS_SHIFT_KEY -> PlayerActionType.START_SNEAK;
			case RELEASE_SHIFT_KEY -> PlayerActionType.STOP_SNEAK;
			case STOP_SLEEPING -> null;
			case START_SPRINTING -> PlayerActionType.START_SPRINT;
			case STOP_SPRINTING -> PlayerActionType.STOP_SPRINT;
			case START_RIDING_JUMP -> null;
			case STOP_RIDING_JUMP -> null;
			case OPEN_INVENTORY -> null;
			case START_FALL_FLYING -> null;
		};

		if(bedrockConnection.movementMode != AuthoritativeMovementMode.CLIENT) {
			PlayerAuthInputData data = null;
			switch (packet.getMode()) {
				case PRESS_SHIFT_KEY -> data = PlayerAuthInputData.START_SNEAKING;
				case RELEASE_SHIFT_KEY -> data = PlayerAuthInputData.STOP_SNEAKING;
				case START_SPRINTING -> data = PlayerAuthInputData.START_SPRINTING;
				case STOP_SPRINTING -> data = PlayerAuthInputData.STOP_SPRINTING;
			}
			if(data != null) {
				bedrockConnection.authInputData.add(data);
				return;
			}
		}

		if(actionType == null) {
			return;
		}

		PlayerActionPacket playerActionPacket = new PlayerActionPacket();
		playerActionPacket.setRuntimeEntityId(TunnelMC.mc.player.getId());
		playerActionPacket.setAction(actionType);
		playerActionPacket.setBlockPosition(Vector3i.ZERO);

		bedrockConnection.sendPacket(playerActionPacket);
	}
}
