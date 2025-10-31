package top.mrxiaom.hologram.loginui.gui;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import top.mrxiaom.hologram.loginui.VectorLoginUI;
import top.mrxiaom.hologram.vector.displays.hologram.RenderMode;
import top.mrxiaom.hologram.vector.displays.ui.api.Element;
import top.mrxiaom.hologram.vector.displays.ui.api.PageBuilder;
import top.mrxiaom.hologram.vector.displays.ui.api.Terminal;
import top.mrxiaom.hologram.vector.displays.ui.event.ClickEvent;
import top.mrxiaom.hologram.vector.displays.ui.widget.Button;
import top.mrxiaom.hologram.vector.displays.ui.widget.Label;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractKeyboardTerminal<This extends AbstractKeyboardTerminal<This>> extends Terminal<This> {
    protected final VectorLoginUI plugin;
    protected final Player player;
    protected final Label labelInput;
    private final List<Button> shiftKeys = new ArrayList<>();
    // Shift 键状态
    // 0 - 未按下 Shift
    // 1 - 下一次按键按下 Shift
    // 2 - 按住 Shift
    protected int shift = 0;
    public AbstractKeyboardTerminal(@NotNull VectorLoginUI plugin, @NotNull Player player, String id, Location location, int widthSpace, int heightLines) {
        super(RenderMode.VIEWER_LIST, id, location, widthSpace, heightLines);
        Location eyeLocation = player.getEyeLocation().clone(); eyeLocation.setPitch(0);
        setRotation(180.0f - eyeLocation.getYaw(), -15.0f);

        setBackgroundColor(0x30000000);

        this.plugin = plugin;
        this.labelInput = new Label("label_input");

        addViewer(this.player = player);
    }

    protected void initPages() {
        addPage("keyboard", this::initPage);
        applyPage("keyboard");
    }

    protected static Location defineTerminalLoc(Player player) {
        Location eyeLocation = player.getEyeLocation().clone(); eyeLocation.setPitch(0);
        Location loc = player.getLocation().clone();
        loc.setY(loc.getY() + 1);
        return loc.add(eyeLocation.getDirection().multiply(0.8));
    }

    protected abstract String getInputText();
    protected abstract void setInputText(String text);

    protected abstract void refreshInput();

    protected void refreshKeys() {
        for (Button element : shiftKeys) {
            element.tryUpdateHoverState(shift > 0);
            element.update();
        }
        for (Element<?, ?> element : getElements()) {
            if (element instanceof Button && element.getId().startsWith("keyboard_")) {
                String[] split = element.getId().split("_", 3);
                if (split.length == 3) {
                    refreshKey((Button) element, split[1], split[2]);
                    element.update();
                }
            }
        }
    }

    protected void refreshKey(Button btn, String key, String keyShift) {
        if (key.equals(" ")) return;
        if (key.equals("f") || key.equals("j")) {
            btn.setText("<#FFFFFF><u>" + (shift > 0 ? keyShift : key) + "</u> ");
        } else {
            btn.setText("<#FFFFFF>" + (shift > 0 ? keyShift : key) + " ");
        }
    }

    protected abstract void initPage(PageBuilder builder);

    protected void addKeyboard(PageBuilder builder, String keys, String keysShift, int x, int y, Consumer<Button> consumer) {
        char[] chars = keys.toCharArray();
        char[] charsShift = keysShift.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            String key = String.valueOf(chars[i]);
            String keyShift = String.valueOf(charsShift[i]);
            double keyX = x + i * 4.2;
            addKey(builder, key, keyShift, keyX, y, consumer);
        }
    }

    protected void addKey(PageBuilder builder, String key, String keyShift, double x, double y, Consumer<Button> consumer) {
        builder.addElement(new Button("keyboard_" + key + "_" + keyShift), e -> {
            refreshKey(e, key, keyShift);
            e.setPos(x, y);
            e.setOnClick(clickKeyboard(key, keyShift));
            consumer.accept(e);
        });
    }

    protected void addKeyBackspace(PageBuilder builder, String text, double x, double y, Consumer<Button> consumer) {
        builder.addElement(new Button("keyboard_backspace"), e -> {
            e.setPos(x, y);
            e.setText(text);
            e.setOnClick((player, action, element) -> {
                String inputText = getInputText();
                if (!inputText.isEmpty()) {
                    setInputText(inputText.length() == 1 ? "" : inputText.substring(0, inputText.length() - 1));
                    refreshInput();
                    labelInput.update();
                }
            });
            consumer.accept(e);
        });
    }

    protected void addShiftKey(PageBuilder builder, String id, int x, int y, Consumer<Button> consumer) {
        builder.addElement(new Button("keyboard_" + id), e -> {
            e.setPos(x, y);
            e.setText("<#FFFFFF>Shift");
            e.setOnClick((player, action, element) -> {
                shift = shift == 2 ? 0 : (shift + 1);
                refreshKeys();
            });
            consumer.accept(e);
            shiftKeys.add(e);
        });
    }

    protected abstract void onKeyDown(Button button, String key, String keyShift);

    protected ClickEvent<Button> clickKeyboard(String key, String keyShift) {
        return (player, action, element) -> onKeyDown(player, element, key, keyShift);
    }

    private void onKeyDown(Player player, Button button, String key, String keyShift) {
        onKeyDown(button, key, keyShift);
        if (shift == 1) {
            shift = 0;
            refreshKeys();
        }
        refreshInput();
        labelInput.update();
    }
}
