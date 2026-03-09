package com.packetdelay.mixin;

import com.packetdelay.PacketDelay;
import net.minecraft.network.Connection;
import net.minecraft.network.DisconnectionDetails;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Connection.class)
public class ConnectionMixin {
    @Inject(method = "disconnect(Lnet/minecraft/network/DisconnectionDetails;)V", at = @At("HEAD"))
    private void clearPackets(DisconnectionDetails disconnectionDetails, CallbackInfo ci) {
        PacketDelay.clearPackets();
    }
}
