package me.whizvox.wessentials.exception;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.whizvox.wessentials.WEssentials;

public record TranslatableCommandException(String key) implements CommandExceptionType {

    public CommandSyntaxException create(Object... args) {
        return new CommandSyntaxException(this, new LiteralMessage(WEssentials.inst().getMessages().translatePlain(key, args)));
    }

}
