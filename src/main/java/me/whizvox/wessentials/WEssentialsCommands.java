package me.whizvox.wessentials;

import io.papermc.paper.command.brigadier.Commands;
import me.whizvox.wessentials.command.WEssentialsCommand;
import me.whizvox.wessentials.command.home.ClearHomeCommand;
import me.whizvox.wessentials.command.home.SetHomeCommand;
import me.whizvox.wessentials.command.home.TeleportHomeCommand;
import me.whizvox.wessentials.command.nick.ClearNicknameCommand;
import me.whizvox.wessentials.command.nick.SetOtherNicknameCommand;
import me.whizvox.wessentials.command.nick.SetOwnNicknameCommand;
import me.whizvox.wessentials.command.teleport.*;
import me.whizvox.wessentials.command.warp.DeleteWarpCommand;
import me.whizvox.wessentials.command.warp.SetWarpCommand;
import me.whizvox.wessentials.command.warp.WarpCommand;
import me.whizvox.wessentials.core.ModuleCommand;

import java.util.List;

public class WEssentialsCommands {

    private static final List<ModuleCommand> commands = List.of(
        new WEssentialsCommand(),
        new TeleportHomeCommand(),
        new SetHomeCommand(),
        new ClearHomeCommand(),
        new SetOwnNicknameCommand(),
        new SetOtherNicknameCommand(),
        new ClearNicknameCommand(),
        new TeleportAskCommand(),
        new TeleportHereCommand(),
        new TeleportAcceptCommand(),
        new TeleportDenyCommand(),
        new TeleportCancelCommand(),
        new SetWarpCommand(),
        new WarpCommand(),
        new DeleteWarpCommand()
    );

    public static void registerAll(Commands registrar) {
        commands.forEach(cmd -> cmd.register(registrar));
    }

}
