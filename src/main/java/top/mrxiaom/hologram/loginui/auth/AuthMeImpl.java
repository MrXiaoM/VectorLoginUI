package top.mrxiaom.hologram.loginui.auth;

import fr.xephi.authme.AuthMe;
import fr.xephi.authme.api.v3.AuthMeApi;
import fr.xephi.authme.events.LoginEvent;
import fr.xephi.authme.events.RegisterEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.hologram.loginui.VectorLoginUI;
import top.mrxiaom.hologram.loginui.api.IAuth;

import java.util.function.Consumer;

public class AuthMeImpl implements IAuth, Listener {
    private final AuthMeApi api = AuthMeApi.getInstance();
    private Consumer<Player> registerAction, loginAction;
    public AuthMeImpl(VectorLoginUI plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRegister(RegisterEvent e) {
        if (registerAction != null) {
            registerAction.accept(e.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLogin(LoginEvent e) {
        if (loginAction != null) {
            loginAction.accept(e.getPlayer());
        }
    }

    @Override
    public String getName() {
        AuthMe plugin = api.getPlugin();
        return plugin.getDescription().getName() + " " + plugin.getDescription().getVersion();
    }

    @Override
    public boolean isRegistered(@NotNull Player player) {
        return api.isRegistered(player.getName());
    }

    @Override
    public boolean hasLogon(@NotNull Player player) {
        return api.isAuthenticated(player);
    }

    @Override
    public boolean doRegister(@NotNull Player player, @NotNull String password) {
        return api.registerPlayer(player.getName(), password);
    }

    @Override
    public boolean doLogin(@NotNull Player player, @NotNull String password) {
        if (api.checkPassword(player.getName(), password)) {
            api.forceLogin(player);
            return true;
        }
        return false;
    }

    @Override
    public void setOnRegister(@Nullable Consumer<Player> action) {
        this.registerAction = action;
    }

    @Override
    public void setOnLogin(@Nullable Consumer<Player> action) {
        this.loginAction = action;
    }
}
