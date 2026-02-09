package me.whizvox.wessentials.module;

public interface Module {

    String getName();

    boolean isEnabled();

    void setEnabled(boolean enabled);

    void load();

    void save();

}
