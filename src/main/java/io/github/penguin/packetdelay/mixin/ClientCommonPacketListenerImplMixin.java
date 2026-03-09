package io.github.penguin.packetdelay.mixin;

import io.github.penguin.packetdelay.PacketDelay;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.network.protocol.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

@Mixin(ClientCommonPacketListenerImpl.class)
public class ClientCommonPacketListenerImplMixin {
    @Inject(method = "send", at = @At("HEAD"), cancellable = true)
    private void delayPackets(Packet<?> packet, CallbackInfo ci) {
        if (PacketDelay.isDelayingPackets() && Arrays.stream(PacketDelay.blockedPackets).anyMatch(c -> c.isInstance(packet))) {
            PacketDelay.delayPacket(packet);
            ci.cancel();
        }
    }
}
