package me.THEREALWWEFAN231.tunnelmc.mixins.interfaces;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientWorld.class)
public interface IMixinClientWorld {
    @Accessor("networkHandler")
    ClientPlayNetworkHandler getNetworkHandler();
}
