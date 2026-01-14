package me.whizvox.wessentials.exception;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.whizvox.wessentials.WEssentials;

public class TranslationCommandException implements CommandExceptionType {

    private final String key;

    public TranslationCommandException(String key) {
        this.key = key;
    }

    public CommandSyntaxException create(Object... args) {
        return new CommandSyntaxException(this, new LiteralMessage(WEssentials.inst().getMessages().translatePlain(key, args)));
    }

}
