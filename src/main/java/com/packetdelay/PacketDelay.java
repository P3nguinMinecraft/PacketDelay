package com.packetdelay;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.KeyMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.Packet;

import 	net.minecraft.network.protocol.game.*;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;

public class PacketDelay implements ClientModInitializer {
	public static Logger LOGGER = LoggerFactory.getLogger("PacketDelay");
    public static KeyMapping.Category CATEGORY = KeyMapping.Category.register(ResourceLocation.parse("packetdelay"));
    private static KeyMapping activateKey;
    @Override
    public void onInitializeClient() {
        activateKey = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.packetdelay", InputConstants.Type.KEYSYM, InputConstants.UNKNOWN.getValue(), CATEGORY));
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (!isDelayingPackets()) releasePackets(client);
        });
        HudElementRegistry.attachElementAfter(ResourceLocation.parse("subtitles"), ResourceLocation.fromNamespaceAndPath("packetdelay", "packet-delay-text-layer"), (guiGraphics, deltaTicks) -> {
            if (isDelayingPackets()) {
                guiGraphics.drawString(Minecraft.getInstance().font, "Delaying Packets", 4, guiGraphics.guiWidth() - 4 - Minecraft.getInstance().font.lineHeight, 0xffffffff, false);
            }
        });
    }

    private static final ArrayList<Packet<?>> delayedPackets = new ArrayList<>();

    public static final Class[] blockedPackets = {
            ServerboundPlayerActionPacket.class,
            ServerboundPlayerInputPacket.class,
            ServerboundUseItemOnPacket.class,
            ServerboundUseItemPacket.class,
            ClientboundSetHeldSlotPacket.class,
            ServerboundInteractPacket.class
    };

    public static boolean isDelayingPackets() {
        return activateKey.isDown();
    }

    public static void delayPacket(Packet<?> p) {
        delayedPackets.add(p);
    }

    public static void clearPackets() {
        delayedPackets.clear();
    }

    private static void releasePackets(Minecraft client) {
        for (Packet<?> packet : delayedPackets) {
            client.getConnection().send(packet);
        }
        delayedPackets.clear();
    }
}