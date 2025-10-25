package top.mrxiaom.hologram.loginui;

import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.impl.PlatformScheduler;
import org.bukkit.plugin.java.JavaPlugin;

public class VectorLoginUI extends JavaPlugin {
    private final FoliaLib foliaLib;
    public VectorLoginUI() {
        this.foliaLib = new FoliaLib(this);
    }

    public PlatformScheduler getScheduler() {
        return foliaLib.getScheduler();
    }

    @Override
    public void onEnable() {
        getLogger().info("VectorLoginUI 已启用");
    }
}
