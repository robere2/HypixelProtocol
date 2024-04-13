package dev.ecr.hypixel.packets;

import net.hypixel.modapi.packet.impl.serverbound.ServerboundLocationPacket;

public class LocationPacketFactory implements IPacketFactory<ServerboundLocationPacket> {
    @Override
    public ServerboundLocationPacket build(String... args) {
        return new ServerboundLocationPacket();
    }
}
