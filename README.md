# Hypixel Protocol Experimentation

Simple project I used to experiment with the new Hypixel modding protocol. I am sharing it publicly in case it may be a
valuable resource to anyone struggling to get it up and running in their own project. You can install this mod, but it
does not provide any useful features.

## Requirements

This mod is built for Fabric on Minecraft 1.20.4. You must also have the Fabric API installed. Refer to the 
[Fabric installation page](https://fabricmc.net/use/installer/) for more information.

## Usage

If you'd like to install the mod and test it yourself, use the command `/hyproto`. This mod supports Hypixel ModAPI
v0.3.1, which currently supports the following packets:

- `ping`
- `player_info`
- `party_info`
- `location`

Provide the name of the packet you wish to send as the first argument to the command (for example, `/hyproto ping`).
The response will be printed in chat and in your client logs in a human-readable format.

The `/hyproto` command has support for additional arguments for each packet, but none of the available packets currently
take any arguments.

This command will not do anything if ran in singleplayer or on a server other than Hypixel.