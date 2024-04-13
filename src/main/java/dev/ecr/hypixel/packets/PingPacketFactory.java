package dev.ecr.hypixel.packets;

import net.hypixel.modapi.packet.impl.serverbound.ServerboundPingPacket;

public class PingPacketFactory implements IPacketFactory<ServerboundPingPacket> {
    @Override
    public ServerboundPingPacket build(String... args) {
        return new ServerboundPingPacket();
    }
}
