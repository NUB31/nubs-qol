package net.nub31.nubsqol.networking.packet;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.nub31.nubsqol.NubsQol;
import net.nub31.nubsqol.constant.RunningConfig;

public class PingS2CPacket {
	public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
		NubsQol.RUNNING_CONFIG = RunningConfig.CLIENT_AND_SERVER;
	}
}
