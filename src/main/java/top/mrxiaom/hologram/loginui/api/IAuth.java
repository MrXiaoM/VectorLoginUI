package top.mrxiaom.hologram.loginui.api;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface IAuth {
    /**
     * 获取登录插件适配器名称
     */
    String getName();
    /**
     * 获取玩家是否已注册
     */
    boolean isRegistered(@NotNull Player player);

    /**
     * 获取玩家是否已登录
     */
    boolean hasLogon(@NotNull Player player);

    /**
     * 执行注册操作
     * @param player 玩家
     * @param password 密码
     * @return 是否注册成功
     */
    boolean doRegister(@NotNull Player player, @NotNull String password);

    /**
     * 执行登录操作
     * @param player 玩家
     * @param password 密码
     * @return 是否登录成功
     */
    boolean doLogin(@NotNull Player player, @NotNull String password);

    /**
     * 设置玩家注册后执行的操作
     */
    void setOnRegister(@Nullable Consumer<Player> action);

    /**
     * 设置玩家登录后执行的操作
     */
    void setOnLogin(@Nullable Consumer<Player> action);
}
