package me.whizvox.wessentials;

import org.bukkit.configuration.Configuration;

public class DefaultMessages extends ConfigurationGenerator {

    public DefaultMessages(Configuration config) {
        super(config);
    }

    @Override
    public void generateDefaults() {
        set("locale", "en-US",
            "Locale to use when translating.",
            "Should keep as default en-US unless you really know what you're doing.");
        // back
        set("message.back.noLocation", "<gold>There is nowhere to teleport back to.</gold>");
        set("message.back.fail", "<gold>Could not teleport to previous location. Do you have any passengers?</gold>");
        // custom text
        set("message.customtext.none", "<red>No custom text found for <key>.</red>");
        set("message.customtext.exampleExists", "<gold>Custom text file already exists.</gold>");
        set("message.customtext.example", "<green>Custom text file generated at <yellow><file></yellow>.</green><newline><gold>Note that any new commands will not be registered until the server restarts.</gold>");
        set("message.customtext.list", "<aqua>Custom text commands: </aqua><commands>");
        set("message.customtext.listNone", "<gold>No custom text messages found.</gold>");
        set("message.customtext.updated", "<green>Custom text successfully updated.</green>");
        // home
        set("message.home.notSet", "<red>Home is not set.</red>");
        set("message.home.set", "<green>Home location has been set.</green>");
        set("message.home.setOther", "<green>Home location has been set for <player>.</green>");
        set("message.home.clear", "<green>Home location has been cleared.</green>");
        set("message.home.clearOther", "<green>Home location has been cleared for <player>.</green>");
        set("message.home.teleport", "<green>Teleporting to your home...</green>");
        set("message.home.teleportOther", "<green>Teleporting to <player>'s home...</green>");
        // kit
        set("message.kit.conflict", "<red>There already exists a kit named <kit>.</red>");
        set("message.kit.create", "<green>Kit <reset><kit><green> successfully created.</green>");
        set("message.kit.cooldown", "<gold>You must wait <cooldown> seconds until you can get that kit again.</gold>");
        set("message.kit.cooldownOther", "<gold>You must wait <cooldown> seconds until you can give <player> that kit.</gold>");
        set("message.kit.give", "<green>You have been given the <reset><kit><green> kit.</green>");
        set("message.kit.giveOther", "<green>You have given <player> the <reset><kit><green> kit.</green>");
        set("message.kit.update", "<green>Kit has been updated.</green>");
        set("message.kit.clearCooldown.none", "<gold>No cooldown to remove.</gold>");
        set("message.kit.clearCooldown.all.receiver", "<green>All kit cooldowns have been cleared.</green>");
        set("message.kit.clearCooldown.all.sender", "<green>All kit cooldowns for <player> have been cleared.</green>");
        set("message.kit.clearCooldown.self", "<green>Cooldown for <kit> has been cleared.</green>");
        set("message.kit.clearCooldown.other", "<green>Cleared <player>'s cooldown for <kit>.</green>");
        set("message.kit.delete.success", "<green>Kit deleted.</green>");
        set("message.kit.delete.fail", "<gold>Could not delete kit. The caches might be out of sync. Try running <yellow>/wessentials reload</yellow>.</gold>");
        // nick
        set("message.nick.set", "<green>Nickname has been changed to <reset><nick><green>.</green>");
        set("message.nick.setOther", "<green>Nickname for <yellow><player></yellow> has been set to <reset><nick><green>.</green>");
        set("message.nick.clear", "<aqua>Nickname has been cleared.</aqua>");
        set("message.nick.clearOther", "<green><player>'s nickname has been cleared.</green>");
        set("message.nick.notSet", "<red>You do not have a nickname.</red>");
        set("message.nick.notSetOther", "<yellow><player></yellow> <red>does not have a nickname.</red>");
        // teleport
        set("message.teleport.expired", "<red>Teleport request with <player> has expired.</red>");
        set("message.teleport.offline", "<red>Could not complete teleport request, <player> is offline.</red>");
        set("message.teleport.noRequestPlayer", "<red>No teleport request found with <player>.</red>");
        set("message.teleport.noRequest", "<red>No teleport request found.</red>");
        set("message.teleport.hasRequest", "<red>You already have a teleport request with <player>.</red>");
        set("message.teleport.notSelf", "<red>Cannot teleport to yourself.</red>");
        set("message.teleport.cannotSend", "<red>Cannot send a request to <player>.</red>");
        set("message.teleport.success.from", "<green><player> is teleporting to you now.</green>");
        set("message.teleport.success.to", "<green>Successfully teleported to <player>.</green>");
        set("message.teleport.deny.receiver", "<green>Denied teleport request from <player>.</green>");
        set("message.teleport.deny.sender", "<gold>Teleport request to <player> has been denied.</gold>");
        set("message.teleport.cancel.receiver", "<gold>Teleport request from <player> has been cancelled.</gold>");
        set("message.teleport.cancel.sender", "<gold>Cancelled teleport request to <player>.</gold>");
        set("message.teleport.cancel.offline", "<gold>Teleport request cancelled, <player> is offline.</gold>");
        set("message.teleport.request.receiver.toSender", "<aqua><player> is requesting you to teleport to them. <remaining></aqua>");
        set("message.teleport.request.receiver.toReceiver", "<aqua><player> is requesting to teleport to you. <remaining></aqua>");
        set("message.teleport.request.receiver.remaining", "Type <bold><green>/tpaccept</green></bold> to accept, or <bold><red>/tpdeny</red></bold> to deny. You have <gold>60 seconds</gold> to respond.");
        set("message.teleport.request.sender", "<green>Successfully sent a teleport request to <player>.</green>");
        // top
        set("message.top.success", "<green>Woosh!</green>");
        set("message.top.fail", "<gold>Could not find any solid blocks to teleport to.</gold>");
        // warp
        set("message.warp.nameConflict", "<red>There already exists a warp named <yellow><warp></yellow>.</red>");
        set("message.warp.set", "<green>Warp <yellow><warp></yellow> successfully created.</green>");
        set("message.warp.teleport", "<green>Successfully warped to <yellow><warp></yellow>.</green>");
        set("message.warp.delete", "<green>Warp <yellow><warp></yellow> has been deleted.</green>");
        // misc
        set("message.pluginName", "<bold><light_purple>[</light_purple></bold><dark_purple>w</dark_purple><light_purple>Essentials</light_purple><bold><dark_purple>]</dark_purple></bold>");
        set("message.reload", "<plugin_name> Finished reloading configurations and messages.");
        set("message.version", "<plugin_name> Version: <yellow><version></yellow>");
        setComments("message", "All plugin messages, which support MiniMessage syntax: https://webui.advntr.dev/");
        // exceptions
        set("exception.kit.invalidName", "Invalid kit name: %s");
        set("exception.kit.unknown", "Unknown kit: %s");
        set("exception.warp.notFound", "Warp not found: %s");
        set("exception.warp.invalidName", "Invalid warp name: %s");
        set("exception.invalidFace", "Invalid face: %s. Must be one of [north, south, east, west, up, down].");
        set("exception.unknownWorld", "Unknown world: %s");
        set("exception.unknownCustomTextKey", "Unknown custom text key: %s");
        setComments("exception", "Plain text exception messages. These do NOT support MiniMessage syntax.");
    }

}
