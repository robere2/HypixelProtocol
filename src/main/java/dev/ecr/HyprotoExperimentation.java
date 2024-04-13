package dev.ecr;

import com.mojang.brigadier.arguments.StringArgumentType;
import dev.ecr.commands.hyproto.HyprotoBaseCommandRunner;
import dev.ecr.commands.hyproto.HyprotoPacketArgumentRunner;
import dev.ecr.commands.hyproto.HyprotoPacketArgumentSuggestionProvider;
import dev.ecr.hypixel.handlers.ChatPacketHandler;
import dev.ecr.hypixel.packets.IPacketFactory;
import dev.ecr.hypixel.handlers.LoggingPacketHandler;
import dev.ecr.hypixel.packets.LocationPacketFactory;
import dev.ecr.hypixel.packets.PartyInfoPacketFactory;
import dev.ecr.hypixel.packets.PingPacketFactory;
import dev.ecr.hypixel.packets.PlayerInfoPacketFactory;
import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.hypixel.modapi.HypixelModAPI;
import net.hypixel.modapi.packet.HypixelPacket;
import net.hypixel.modapi.serializer.PacketSerializer;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class HyprotoExperimentation implements ClientModInitializer {

	public static final int CHAT_COLOR = 14405424;
	public static final Logger LOGGER = LoggerFactory.getLogger("Hypixel Protocol Experimentation");
	private static boolean isConnectedToHypixel = false;
	private static final Map<String, IPacketFactory<? extends HypixelPacket>> packetFactories = new HashMap<>(){{
		put("hypixel:ping", new PingPacketFactory());
		put("hypixel:location", new LocationPacketFactory());
		put("hypixel:player_info", new PlayerInfoPacketFactory());
		put("hypixel:party_info", new PartyInfoPacketFactory());

		// Verify that all known Hypixel packets have factories within our packetFactories map
		for(final String identifier : HypixelModAPI.getInstance().getRegistry().getIdentifiers()) {
			if(!containsKey(identifier)) {
				throw new IllegalStateException("Packet %s does not have a defined factory".formatted(identifier));
			}
		}
	}};

	@Override
	public void onInitializeClient() {
		HypixelModAPI.getInstance().registerHandler(new LoggingPacketHandler());
		HypixelModAPI.getInstance().registerHandler(new ChatPacketHandler());

		this.registerHypixelPackets();
		this.initializeHypixelJoinTracking();
		this.registerHyprotoCommand();
	}

	/**
	 * Get the state of whether this mod considers the client to be connected to Hypixel. The criteria this is
	 * relatively loose, see {@link #isServerHypixel(ServerInfo)} for more info. This value is updated shortly
	 * after receiving the relevant JOIN and DISCONNECT events from the Fabric API.
	 * @return True if connected to Hypixel, False if not.
	 */
	public static boolean isConnectedToHypixel() {
		return HyprotoExperimentation.isConnectedToHypixel;
	}

	public static IPacketFactory<? extends HypixelPacket> getPacketFactory(String identifier) {
		return packetFactories.get(identifier);
	}

	/**
	 * Utility for checking whether a ServerInfo object likely matches that of Hypixel. We accomplish this by checking
	 * if the domain is "hypixel.net", with or without subdomain or specified port. The solution is not fool-proof
	 * (for example, "hypixel.net.example.com" is still considered valid). As a backdoor for those who insist on not
	 * connecting through an official Hypixel address, servers containing "hypixel proxy" in their server list name
	 * will also be considered valid.
	 *
	 * @param serverInfo Minecraft ServerInfo object, obtained from the server list or during server connection. This
	 *                   may be null, but the return value will always be false.
	 * @return True if the server is determined to be Hypixel, or false if not. If ServerInfo is null then false is
	 * returned.
	 */
	private boolean isServerHypixel(ServerInfo serverInfo) {
		if(serverInfo == null) {
			return false;
		}
		if(serverInfo.address.toLowerCase().startsWith("hypixel.net") || serverInfo.address.toLowerCase().contains(".hypixel.net")) {
			return true;
		}
        return serverInfo.name.toLowerCase().contains("hypixel proxy");
    }

	/**
	 * Add event listeners for when the client connects and disconnects from servers. These event listeners then keep
	 * track of whether the player is connected to Hypixel, accessible via {@link this#isConnectedToHypixel()}. This
	 * method should only be called once during client initialization.
	 */
	private void initializeHypixelJoinTracking() {
		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
			client.execute(() -> {
				final ServerInfo serverInfo = handler.getServerInfo();
				if(!isServerHypixel(serverInfo)) {
					return;
				}

				// Execute on main thread instead of net thread
				client.execute(() -> {
					if(!HyprotoExperimentation.isConnectedToHypixel()) {
						this.onHypixelConnection();
					}
				});
			});
		});
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
			// Execute on main thread instead of net thread
			client.execute(() -> {
				if(HyprotoExperimentation.isConnectedToHypixel()) {
					this.onHypixelDisconnection();
				}
			});
		});
	}

	/**
	 * Listen for all incoming known Hypixel packets and send them to be handled by the Hypixel Mod API. This should
	 * be called once during client initialization.
	 */
	private void registerHypixelPackets() {
		for(String identifier : HypixelModAPI.getInstance().getRegistry().getIdentifiers()) {
			ClientPlayNetworking.registerGlobalReceiver(new Identifier(identifier), (client, handler, buf, responseSender) -> {
				// Execute on main thread instead of net thread
				client.execute(() -> {
					HypixelModAPI.getInstance().handle(identifier, new PacketSerializer(buf));
				});
			});
		}
	}

	/**
	 * Register the /hyproto command. The syntax of this command is "/hyproto [packet] [args]". Args are optional, and
	 * in fact, no current packet takes any arguments. This command will not work when not connected to Hypixel.
	 */
	private void registerHyprotoCommand() {
		// Register a command "hyproto" which takes in any packet ID, without the "hypixel" namespace.
		// The Hypixel ModAPI lib will deserialize it and send it to our HypixelPacketHandler.
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			dispatcher.register(
					ClientCommandManager
							.literal("hyproto")
							.then(
									ClientCommandManager.argument("packet", StringArgumentType.word())
											.executes(new HyprotoPacketArgumentRunner())
											.suggests(new HyprotoPacketArgumentSuggestionProvider())
											.then(
													ClientCommandManager.argument("args", StringArgumentType.greedyString())
															.executes(new HyprotoPacketArgumentRunner())
											)
							)
							.executes(new HyprotoBaseCommandRunner())
			);
		});
	}

	/**
	 * Event handler that is triggered when the client connects to Hypixel.
	 */
	private void onHypixelConnection() {
		HyprotoExperimentation.isConnectedToHypixel = true;
	}

	/**
	 * Event handler that is triggered when the client disconnects from Hypixel.
	 */
	private void onHypixelDisconnection() {
		HyprotoExperimentation.isConnectedToHypixel = false;
	}
}