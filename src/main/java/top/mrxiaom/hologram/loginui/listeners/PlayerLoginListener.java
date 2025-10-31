package top.mrxiaom.hologram.loginui.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import top.mrxiaom.hologram.loginui.VectorLoginUI;
import top.mrxiaom.hologram.loginui.api.IAuth;
import top.mrxiaom.hologram.loginui.gui.TerminalLogin;
import top.mrxiaom.hologram.loginui.gui.TerminalRegister;
import top.mrxiaom.hologram.vector.displays.TerminalManager;
import top.mrxiaom.hologram.vector.displays.ui.api.Terminal;

public class PlayerLoginListener implements Listener {
    private final VectorLoginUI plugin;
    public PlayerLoginListener(VectorLoginUI plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        plugin.getAuth().setOnRegister(this::onRegister);
        plugin.getAuth().setOnLogin(this::onLogin);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Terminal<?> terminal = plugin.popTerminal(e.getPlayer().getName());
        TerminalManager.inst().destroy(terminal);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        long delay = 20L; // 延迟 1s，兼容自动登录
        plugin.getScheduler().runLater(() -> {
            IAuth auth = plugin.getAuth();
            if (auth.hasLogon(player)) return;
            if (auth.isRegistered(player)) {
                plugin.spawn(new TerminalLogin(plugin, player));
            } else {
                plugin.spawn(new TerminalRegister(plugin, player));
            }
        }, delay);
    }

    public void onRegister(Player player) {
        Terminal<?> terminal = plugin.popTerminal(player.getName());
        TerminalManager.inst().destroy(terminal);
    }

    public void onLogin(Player player) {
        Terminal<?> terminal = plugin.popTerminal(player.getName());
        TerminalManager.inst().destroy(terminal);
    }
}
