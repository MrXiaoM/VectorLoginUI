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

public class TerminalLogin extends AbstractKeyboardTerminal<TerminalLogin> {
    private String password = "";
    public TerminalLogin(VectorLoginUI plugin, @NotNull Player player) {
        super(plugin, player, "loginui_login_" + player.getName(), defineTerminalLoc(player), 15, 4);
        this.initPages();
    }

    @Override
    protected String getInputText() {
        return password;
    }

    @Override
    protected void setInputText(String text) {
        password = text;
    }

    @Override
    protected void refreshInput() {
        labelInput.setText("<#FFFFFF>密码: <u>" + ("●".repeat(password.length())) + "</u>\n" + " ".repeat(46));
    }

    @Override
    protected void initPage(PageBuilder builder) {
        builder.addElement(new Label("title"), e -> {
            e.setScale(0.5f);
            e.setText("<#2287C4><b>登录到服务器");
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
        int length = password.length();
        if (length < plugin.getMinPasswordLength() || length > plugin.getMaxPasswordLength()) {
            player.sendMessage("你输入的密码长度不正确");
            return;
        }
        IAuth auth = plugin.getAuth();
        if (auth.doLogin(player, password)) {
            plugin.popTerminal(player.getName());
            TerminalManager.inst().destroy(this);
        }
    }

    @Override
    protected void onKeyDown(Button button, String key, String keyShift) {
        if (password.length() < plugin.getMaxPasswordLength()) {
            password += shift > 0 ? keyShift : key;
        }
    }
}
