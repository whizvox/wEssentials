package me.whizvox.wessentials.exception;

public class WECommandExceptions {

    public static final TranslatableCommandException
            INVALID_KIT_NAME = new TranslatableCommandException("exception.kit.invalidName"),
            UNKNOWN_KIT = new TranslatableCommandException("exception.kit.unknown"),
            WARP_NOT_FOUND = new TranslatableCommandException("exception.warp.notFound"),
            INVALID_WARP_NAME = new TranslatableCommandException("exception.warp.invalidName"),
            UNKNOWN_WORLD = new TranslatableCommandException("exception.unknownWorld");

}
