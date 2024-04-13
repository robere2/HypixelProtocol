package dev.ecr.hypixel.packets;

import net.hypixel.modapi.packet.impl.serverbound.ServerboundPlayerInfoPacket;

public class PlayerInfoPacketFactory implements IPacketFactory<ServerboundPlayerInfoPacket> {
    @Override
    public ServerboundPlayerInfoPacket build(String... args) {
        return new ServerboundPlayerInfoPacket();
    }
}
