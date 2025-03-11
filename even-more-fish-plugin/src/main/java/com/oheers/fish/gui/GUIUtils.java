package com.oheers.fish.gui;

import com.oheers.fish.EvenMoreFish;
import com.oheers.fish.FishUtils;
import com.oheers.fish.config.GUIConfig;
import com.oheers.fish.gui.guis.*;
import com.oheers.fish.selling.SellHelper;
import com.oheers.fish.utils.ItemBuilder;
import com.oheers.fish.utils.ItemFactory;
import com.oheers.fish.utils.ItemUtils;
import de.themoep.inventorygui.*;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class GUIUtils {

    public static GuiPageElement getFirstPageButton() {
        YamlDocument config = GUIConfig.getInstance().getConfig();
        return new GuiPageElement('f',
                createItemStack(
                        config.getString("gui.global.first-page.material", "arrow"),
                        Material.ARROW,
                        config.getString("gui.global.first-page.name", "&bFirst Page"),
                        config.getStringList("gui.global.first-page.lore")
                ),
                GuiPageElement.PageAction.FIRST
        );
    }

    public static GuiPageElement getNextPageButton() {
        YamlDocument config = GUIConfig.getInstance().getConfig();
        return new GuiPageElement('n',
                createItemStack(
                        config.getString("gui.global.next-page.material", "paper"),
                        Material.PAPER,
                        config.getString("gui.global.next-page.name", "&bNext Page"),
                        config.getStringList("gui.global.next-page.lore")
                ),
                GuiPageElement.PageAction.NEXT
        );
    }

    public static GuiPageElement getPreviousPageButton() {
        YamlDocument config = GUIConfig.getInstance().getConfig();
        return new GuiPageElement('p',
                createItemStack(
                        config.getString("gui.global.previous-page.material", "paper"),
                        Material.PAPER,
                        config.getString("gui.global.previous-page.name", "&bPrevious Page"),
                        config.getStringList("gui.global.previous-page.lore")
                ),
                GuiPageElement.PageAction.PREVIOUS
        );
    }

    public static GuiPageElement getLastPageButton() {
        YamlDocument config = GUIConfig.getInstance().getConfig();
        return new GuiPageElement('l',
                createItemStack(
                        config.getString("gui.global.last-page.material", "arrow"),
                        Material.ARROW,
                        config.getString("gui.global.last-page.name", "&bLast Page"),
                        config.getStringList("gui.global.last-page.lore")
                ),
                GuiPageElement.PageAction.LAST
        );
    }

    public static GuiPageElement[] getPageElements() {
        return new GuiPageElement[]{
                getFirstPageButton(),
                getPreviousPageButton(),
                getNextPageButton(),
                getLastPageButton()
        };
    }

    public static ItemStack createItemStack(@NotNull String materialName, @NotNull Material defaultMaterial, @NotNull String display, @NotNull List<String> lore) {
        return new ItemBuilder(materialName, defaultMaterial)
                .withDisplay(display)
                .withLore(lore)
                .build();
    }

    public static InventoryGui createGUI(@Nullable Section section) {
        if (section == null) {
            return new InventoryGui(
                    EvenMoreFish.getInstance(),
                    EvenMoreFish.getAdapter().createMessage("&cBroken GUI! Please tell an admin!").getLegacyMessage(),
                    new String[0]
            );
        }
        return new InventoryGui(
                EvenMoreFish.getInstance(),
                EvenMoreFish.getAdapter().createMessage(section.getString("title", "EvenMoreFish Inventory")).getLegacyMessage(),
                section.getStringList("layout").toArray(new String[0])
        );
    }

    public static ItemStack getFillerItem(@Nullable String materialName, @NotNull Material defaultMaterial) {
        Material material = ItemUtils.getMaterial(materialName, defaultMaterial);
        ItemStack stack = new ItemStack(material);
        FishUtils.editMeta(stack, meta -> meta.setDisplayName(""));
        return stack;
    }

    public static GuiElement getElement(@Nullable String configLocation, @NotNull Section section, @Nullable EMFGUI gui, @Nullable Supplier<Map<String, String>> replacementSupplier) {
        // Get Character
        char character = FishUtils.getCharFromString(section.getString("character", "#"), '#');

        Map<ClickType, String> clickTypeStringMap = new HashMap<>();
        if (section.isSection("click-action")) {
            clickTypeStringMap.put(ClickType.LEFT, section.getString("click-action.left", "none"));
            clickTypeStringMap.put(ClickType.RIGHT, section.getString("click-action.right", "none"));
        } else {
            clickTypeStringMap.put(ClickType.LEFT, section.getString("click-action", "none"));
        }
        List<String> commands = section.getStringList("click-commands");

        ItemFactory factory = new ItemFactory(configLocation, section);
        factory.enableAllChecks();
        // Get ItemStack
        ItemStack item;
        if (replacementSupplier == null) {
            item = factory.createItem(null, -1, null);
        } else {
            item = factory.createItem(null, -1, replacementSupplier.get());
        }

        Section actionSection = section.getSection("click-action");
        if (actionSection != null) {
            return new StaticGuiElement(character, item, click -> {
                BiConsumer<ConfigGui, GuiElement.Click> action = switch (click.getType()) {
                    case LEFT -> getActionMap().get(actionSection.getString("left", ""));
                    case RIGHT -> getActionMap().get(actionSection.getString("right", ""));
                    case MIDDLE -> getActionMap().get(actionSection.getString("middle", ""));
                    case DROP -> getActionMap().get(actionSection.getString("drop", ""));
                    default -> null;
                };
                if (action != null) {
                    action.accept(null, click);
                }
                return true;
            });
        } else {
            return new StaticGuiElement(character, item);
        }
    }


    private static boolean eitherClickTypeMatchesString(@NotNull Map<ClickType, String> map, @NotNull String matcher) {
        boolean matches = true;
        for (String string : map.values()) {
            if (!string.equalsIgnoreCase(matcher)) {
                matches = false;
                break;
            }
        }
        return matches;
    }

    public static List<GuiElement> getElements(@NotNull Section section, @Nullable EMFGUI gui, @Nullable Supplier<Map<String, String>> replacementSupplier) {
        return section.getRoutesAsStrings(false)
                .stream()
                .map(section::getSection)
                .filter(Objects::nonNull)
                // Exclude non-item config sections, if there are any
                .filter(loopSection -> loopSection.getRoutesAsStrings(false).contains("item"))
                .map(loopSection -> GUIUtils.getElement(null, loopSection, gui, replacementSupplier))
                .collect(Collectors.toList());
    }

    public static Map<String, BiConsumer<ConfigGui, GuiElement.Click>> getActionMap() {
        Map<String, BiConsumer<ConfigGui, GuiElement.Click>> newActionMap = new HashMap<>();
        // Exiting the main menu should close the GUI
        newActionMap.put("full-exit", (gui, click) -> {
            if (gui != null) {
                gui.doRescue();
            }
            click.getGui().close();
        });
        // Exiting a sub-menu should open the main menu
        newActionMap.put("open-main-menu", (gui, click) -> {
            if (gui != null) {
                gui.doRescue();
            }
            new MainMenuGui(click.getWhoClicked()).open();
        });
        // Toggling custom fish should redraw the GUI and leave it at that
        newActionMap.put("fish-toggle", (gui, click) -> {
            if (click.getWhoClicked() instanceof Player player) {
                EvenMoreFish.getInstance().performFishToggle(player);
            }
            click.getGui().draw();
        });
        // The shop action should just open the shop menu
        newActionMap.put("open-shop", (gui, click) -> {

            if (gui != null) {
                gui.doRescue();
            }

            HumanEntity humanEntity = click.getWhoClicked();

            if (!(humanEntity instanceof Player player)) {
                return;
            }
            new SellGui(player, SellGui.SellState.NORMAL, null).open();
        });
        newActionMap.put("show-command-help", (gui, click) -> {
            Bukkit.dispatchCommand(click.getWhoClicked(), "emf help");
        });
        newActionMap.put("sell-inventory", (gui, click) -> {
            HumanEntity humanEntity = click.getWhoClicked();
            if (!(humanEntity instanceof Player player)) {
                return;
            }
            if (gui instanceof SellGui sellGUI) {
                new SellGui(player, SellGui.SellState.CONFIRM, sellGUI.getFishInventory()).open();
                return;
            }
            new SellHelper(click.getWhoClicked().getInventory(), player).sellFish();
            click.getGui().close();
        });
        newActionMap.put("sell-shop", (gui, click) -> {
            HumanEntity humanEntity = click.getWhoClicked();
            if (gui instanceof SellGui sellGUI && humanEntity instanceof Player player) {
                new SellGui(player, SellGui.SellState.CONFIRM, sellGUI.getFishInventory()).open();
                return;
            }
            SellHelper.sellInventoryGui(click.getGui(), click.getWhoClicked());
            click.getGui().close();
        });
        newActionMap.put("sell-inventory-confirm", (gui, click) -> {
            HumanEntity humanEntity = click.getWhoClicked();
            if (!(humanEntity instanceof Player player)) {
                return;
            }
            new SellHelper(click.getWhoClicked().getInventory(), player).sellFish();
            if (gui != null) {
                gui.doRescue();
            }
            click.getGui().close();
        });
        newActionMap.put("sell-shop-confirm", (gui, click) -> {
            SellHelper.sellInventoryGui(click.getGui(), click.getWhoClicked());
            click.getGui().close();
        });
        newActionMap.put("open-baits-menu", (gui, click) -> {
            if (gui != null) {
                gui.doRescue();
            }
            new BaitsGui(click.getWhoClicked()).open();
        });
        // Add page actions so third party plugins cannot register their own.
        newActionMap.put("first-page", (gui, click) -> {});
        newActionMap.put("previous-page", (gui, click) -> {});
        newActionMap.put("next-page", (gui, click) -> {});
        newActionMap.put("last-page", (gui, click) -> {});



        return newActionMap;
    }

}
