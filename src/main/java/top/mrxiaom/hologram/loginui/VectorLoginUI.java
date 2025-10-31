package top.mrxiaom.hologram.loginui;

import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.impl.PlatformScheduler;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import top.mrxiaom.hologram.loginui.api.IAuth;
import top.mrxiaom.hologram.loginui.auth.AuthMeImpl;
import top.mrxiaom.hologram.loginui.listeners.PlayerLoginListener;
import top.mrxiaom.hologram.vector.displays.TerminalManager;
import top.mrxiaom.hologram.vector.displays.ui.api.Terminal;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class VectorLoginUI extends JavaPlugin {
    private final FoliaLib foliaLib;
    private final Map<String, Terminal<?>> terminals = new HashMap<>();
    public VectorLoginUI() {
        this.foliaLib = new FoliaLib(this);
    }

    public PlatformScheduler getScheduler() {
        return foliaLib.getScheduler();
    }

    private IAuth auth;
    public IAuth getAuth() {
        return auth;
    }

    private int minPasswordLength, maxPasswordLength;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        PluginManager pm = Bukkit.getPluginManager();
        String apiClassName = getConfig().getString("auth-api", "auto");
        if (apiClassName.equalsIgnoreCase("auto")) {
            if (auth == null && pm.isPluginEnabled("AuthMe")) {
                auth = new AuthMeImpl(this);
            }
            // TODO: 支持更多登录插件
            // if (auth == null && pm.isPluginEnabled("ExamplePlugin")) {
            //     auth = new ExamplePluginImpl(this);
            // }
        } else {
            try {
                Class<?> apiClass = Class.forName(apiClassName);
                Constructor<?> constructor = apiClass.getDeclaredConstructor(VectorLoginUI.class);
                Object instance = constructor.newInstance(this);
                if (instance instanceof IAuth) {
                    auth = (IAuth) instance;
                }
            } catch (ReflectiveOperationException | ClassCastException e) {
                getLogger().log(Level.WARNING, "在加载自定义登录插件适配器 " + apiClassName + " 时出现异常", e);
            }
        }
        if (auth == null) {
            getLogger().warning("找不到受支持的登录插件，卸载插件");
            pm.disablePlugin(this);
            return;
        }
        getLogger().info("登录插件: " + auth.getName());

        // 注册监听器
        new PlayerLoginListener(this);

        reloadConfig();

        getLogger().info("VectorLoginUI 已启用");
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        FileConfiguration config = getConfig();

        minPasswordLength = config.getInt("password-length.min", 3);
        minPasswordLength = config.getInt("password-length.max", 20);
    }

    public int getMinPasswordLength() {
        return minPasswordLength;
    }

    public int getMaxPasswordLength() {
        return maxPasswordLength;
    }

    public void spawn(Terminal<?> terminal) {
        Player player = terminal.getViewers().get(0);
        terminals.put(player.getName(), terminal);
        TerminalManager.inst().spawn(terminal);
    }

    public Terminal<?> popTerminal(String playerName) {
        return terminals.remove(playerName);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length > 0) {
            if ("reload".equalsIgnoreCase(args[0]) && sender.isOp()) {
                reloadConfig();
                sender.sendMessage("配置文件已重载");
                return true;
            }
        }
        return true;
    }

    @Override
    public void onDisable() {
        for (Terminal<?> terminal : terminals.values()) {
            TerminalManager.inst().destroy(terminal);
        }
        terminals.clear();
    }
}
