package me.whizvox.wessentials.exception;

public class WECommandExceptions {

    public static final TranslatableCommandException
            WARP_NOT_FOUND = new TranslatableCommandException("exception.warp.notFound"),
            INVALID_WARP_NAME = new TranslatableCommandException("exception.warp.invalidName"),
            UNKNOWN_WORLD = new TranslatableCommandException("exception.unknownWorld");

}
