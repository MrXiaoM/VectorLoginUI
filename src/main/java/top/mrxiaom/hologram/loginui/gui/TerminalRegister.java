package top.mrxiaom.hologram.loginui.gui;

import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.NotNull;
import top.mrxiaom.hologram.loginui.VectorLoginUI;
import top.mrxiaom.hologram.loginui.api.IAuth;
import top.mrxiaom.hologram.vector.displays.TerminalManager;
import top.mrxiaom.hologram.vector.displays.ui.EnumAlign;
import top.mrxiaom.hologram.vector.displays.ui.api.PageBuilder;
import top.mrxiaom.hologram.vector.displays.ui.widget.Button;
import top.mrxiaom.hologram.vector.displays.ui.widget.Label;

import java.util.function.Consumer;

import static top.mrxiaom.hologram.vector.displays.ui.event.HoverStateChange.hoverBg;

public class TerminalRegister extends AbstractKeyboardTerminal<TerminalRegister> {
    private String firstPassword = "";
    private String password = null;
    public TerminalRegister(VectorLoginUI plugin, @NotNull Player player) {
        super(plugin, player, "loginui_register_" + player.getName(), defineTerminalLoc(player), 15, 4);
    }

    @Override
    protected String getInputText() {
        return password == null ? firstPassword : password;
    }

    @Override
    protected void setInputText(String text) {
        if (password == null) {
            firstPassword = text;
        } else {
            password = text;
        }
    }

    @Override
    protected void refreshInput() {
        if (password == null) {
            labelInput.setText("<#FFFFFF>输入密码: <u>" + ("●".repeat(firstPassword.length())) + "</u>\n" + " ".repeat(43));
        } else {
            labelInput.setText("<#FFFFFF>重复密码: <u>" + ("●".repeat(password.length())) + "</u>\n" + " ".repeat(43));
        }
    }

    @Override
    protected void initPage(PageBuilder builder) {
        builder.addElement(new Label("title"), e -> {
            e.setScale(0.5f);
            e.setText("<#2287C4><b>注册服务器账号");
            e.setFullBrightness();
            e.setAlign(EnumAlign.CENTER);
            e.setPos(0, -18);
        });
        builder.addElement(labelInput, e -> {
            e.setScale(0.25f);
            e.setFullBrightness();
            e.setTextAlignment(TextDisplay.TextAlignment.LEFT);
            e.setAlign(EnumAlign.CENTER);
            e.setPos(0, -10);
            refreshInput();
        });
        int keyboardX = -28, keyboardY = -5;
        int normal = 0x802287C4, hover = 0xFF2287C4;
        Consumer<Button> keyboard = e -> {
            e.setScale(0.25f);
            e.setFullBrightness();
            e.setAlign(EnumAlign.CENTER);
            e.setZIndex(10);
            e.setOnHoverStateChange(hoverBg(hover, normal));
        };
        addKeyboard(builder, "`1234567890-=", "~!@#$%^&*()_+", keyboardX, keyboardY, keyboard);
        addKeyboard(builder, "qwertyuiop[]", "QWERTYUIOP{}", keyboardX + 6, keyboardY + 4, keyboard);
        addKeyboard(builder, "asdfghjkl;'\\", "ASDFGHJKL:\"|", keyboardX + 7, keyboardY + 8, keyboard);
        addKeyboard(builder, "zxcvbnm,./", "ZXCVBNM<>?", keyboardX + 9, keyboardY + 12, keyboard);
        addKey(builder, " ", " ", keyboardX + 25, keyboardY + 16, e -> { // 空格
            e.setText("                    ");
            keyboard.accept(e);
        });

        Consumer<Button> keyboardShift = e -> {
            keyboard.accept(e);
            e.setOnHoverStateChange((newState, element) -> {
                element.setBackgroundColor(newState || shift > 0 ? hover : normal);
                element.update();
            });
        };
        addShiftKey(builder, "LSHIFT", keyboardX + 2, keyboardY + 12, keyboardShift);
        addShiftKey(builder, "RSHIFT", keyboardX + 54, keyboardY + 12, keyboardShift);
        addKeyBackspace(builder, "<#FFFFFF> ←- ", keyboardX + 57, keyboardY, keyboard);
        builder.addElement(new Button("btn_confirm"), e -> {
            e.setPos(1, 18);
            e.setText("                   <b>确认</b>                   ");
            e.setScale(0.3f);
            e.setFullBrightness();
            e.setAlign(EnumAlign.CENTER);
            e.setZIndex(10);
            e.setOnHoverStateChange(hoverBg(hover, normal));
            e.setOnClick((whoClicked, action, element) -> onConfirm());
        });
    }

    private void onConfirm() {
        if (password == null) {
            int length = firstPassword.length();
            if (length < plugin.getMinPasswordLength() || length > plugin.getMaxPasswordLength()) {
                player.sendMessage("你输入的密码长度不正确");
                return;
            }
            password = "";
            refreshInput();
            return;
        }
        int length = password.length();
        if (length < plugin.getMinPasswordLength() || length > plugin.getMaxPasswordLength()) {
            player.sendMessage("你输入的密码长度不正确");
            return;
        }
        if (!password.equals(firstPassword)) {
            player.sendMessage("两次输入的密码不一致");
            return;
        }
        IAuth auth = plugin.getAuth();
        if (auth.doRegister(player, password)) {
            plugin.popTerminal(player.getName());
            TerminalManager.inst().destroy(this);
        }
    }

    @Override
    protected void onKeyDown(Button button, String key, String keyShift) {
        boolean repeat = password != null;
        int length = (repeat ? password : firstPassword).length();
        if (length < plugin.getMaxPasswordLength()) {
            if (repeat) {
                password += shift > 0 ? keyShift : key;
            } else {
                firstPassword += shift > 0 ? keyShift : key;
            }
        }
    }
}
