package me.whizvox.wessentials.module;

public class SimpleModule implements Module {

    private final String name;
    private boolean enabled;

    public SimpleModule(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void load() {
    }

    @Override
    public void save() {
    }

}
