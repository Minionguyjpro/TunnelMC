package me.THEREALWWEFAN231.tunnelmc.connection.bedrock.network.translators.world;

import com.nukkitx.protocol.bedrock.packet.LevelSoundEvent2Packet;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketIdentifier;
import me.THEREALWWEFAN231.tunnelmc.translator.packet.PacketTranslator;

@PacketIdentifier(LevelSoundEvent2Packet.class)
public class LevelSoundEvent2Translator extends PacketTranslator<LevelSoundEvent2Packet> { // TODO: remove this?

    @Override
    public void translate(LevelSoundEvent2Packet packet, BedrockConnection bedrockConnection, FakeJavaConnection javaConnection) {
        System.out.println(packet);
        // Make this JSON mappings for any non-extra-data
    }
}
