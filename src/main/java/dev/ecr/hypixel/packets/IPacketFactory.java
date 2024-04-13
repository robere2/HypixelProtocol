package dev.ecr.hypixel.packets;

import net.hypixel.modapi.packet.HypixelPacket;

public interface IPacketFactory<T extends HypixelPacket> {
    T build(String... args);
}
