package net.nub31.nubsqol.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;
import net.nub31.nubsqol.NubsQol;
import net.nub31.nubsqol.networking.packet.AckServerSupportS2CPacket;
import net.nub31.nubsqol.networking.packet.SynServerSupportC2SPacket;

public class ModMessages {
	public static final Identifier ACK_SERVER_SUPPORT_PACKET_ID = new Identifier(NubsQol.MOD_ID, "ack-server-support");
	public static final Identifier SYN_SERVER_SUPPORT_PACKET_ID = new Identifier(NubsQol.MOD_ID, "syn-server-support");

	public static void registerC2SPackets() {
		ServerPlayNetworking.registerGlobalReceiver(SYN_SERVER_SUPPORT_PACKET_ID, SynServerSupportC2SPacket::receive);
	}

	public static void registerS2CPackets() {
		ClientPlayNetworking.registerGlobalReceiver(ACK_SERVER_SUPPORT_PACKET_ID, AckServerSupportS2CPacket::receive);
	}
}
