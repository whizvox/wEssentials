package me.whizvox.wessentials;

import io.papermc.paper.command.brigadier.Commands;
import me.whizvox.wessentials.command.BackCommand;
import me.whizvox.wessentials.command.WEssentialsCommand;
import me.whizvox.wessentials.command.customtext.CustomTextCommand;
import me.whizvox.wessentials.command.extra.CenterCommand;
import me.whizvox.wessentials.command.extra.TopCommand;
import me.whizvox.wessentials.command.home.ClearHomeCommand;
import me.whizvox.wessentials.command.home.SetHomeCommand;
import me.whizvox.wessentials.command.home.TeleportHomeCommand;
import me.whizvox.wessentials.command.kit.*;
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
        // plugin command
        new WEssentialsCommand(),
        // back command
        new BackCommand(),
        // custom text command
        new CustomTextCommand(),
        // home commands
        new TeleportHomeCommand(),
        new SetHomeCommand(),
        new ClearHomeCommand(),
        // extra commands
        new CenterCommand(),
        new TopCommand(),
        // kit commands
        new GiveKitCommand(),
        new CreateKitCommand(),
        new EditKitCommand(),
        new DeleteKitCommand(),
        new ClearKitCooldownCommand(),
        // nickname commands
        new SetOwnNicknameCommand(),
        new SetOtherNicknameCommand(),
        new ClearNicknameCommand(),
        // teleport request commands
        new TeleportAskCommand(),
        new TeleportHereCommand(),
        new TeleportAcceptCommand(),
        new TeleportDenyCommand(),
        new TeleportCancelCommand(),
        // warp commands
        new SetWarpCommand(),
        new WarpCommand(),
        new DeleteWarpCommand()
    );

    public static void registerAll(Commands registrar) {
        commands.forEach(cmd -> cmd.register(registrar));
    }

}
