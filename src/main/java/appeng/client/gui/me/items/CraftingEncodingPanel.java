package appeng.client.gui.me.items;

import java.util.List;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;

import appeng.api.config.ActionItems;
import appeng.client.Point;
import appeng.client.gui.Icon;
import appeng.client.gui.WidgetContainer;
import appeng.client.gui.style.Blitter;
import appeng.client.gui.widgets.ActionButton;
import appeng.client.gui.widgets.ToggleButton;
import appeng.core.localization.ButtonToolTips;
import appeng.core.localization.GuiText;
import appeng.menu.SlotSemantics;

public class CraftingEncodingPanel extends EncodingModePanel {
    private static final Blitter BG = Blitter.texture("guis/pattern_modes.png").src(0, 0, 124, 66);

    private final ActionButton clearBtn;
    private final ToggleButton substitutionsBtn;
    private final ToggleButton fluidSubstitutionsBtn;

    public CraftingEncodingPanel(PatternEncodingTermScreen<?> screen, WidgetContainer widgets) {
        super(screen, widgets);

        // Add buttons for the crafting mode
        clearBtn = new ActionButton(ActionItems.S_CLOSE, act -> menu.clear());
        clearBtn.setHalfSize(true);
        clearBtn.setDisableBackground(true);
        widgets.add("craftingClearPattern", clearBtn);

        this.substitutionsBtn = createCraftingSubstitutionButton(widgets);
        this.fluidSubstitutionsBtn = createCraftingFluidSubstitutionButton(widgets);
    }

    @Override
    ResourceLocation getIcon() {
        return Icon.TAB_CRAFTING;
    }

    @Override
    public Component getTabTooltip() {
        return GuiText.CraftingPattern.text();
    }

    private ToggleButton createCraftingSubstitutionButton(WidgetContainer widgets) {
        var button = new ToggleButton(
                Icon.S_SUBSTITUTION_ENABLED,
                Icon.S_SUBSTITUTION_DISABLED,
                menu::setSubstitute);
        button.setHalfSize(true);
        button.setDisableBackground(true);
        button.setTooltipOn(List.of(
                ButtonToolTips.SubstitutionsOn.text(),
                ButtonToolTips.SubstitutionsDescEnabled.text()));
        button.setTooltipOff(List.of(
                ButtonToolTips.SubstitutionsOff.text(),
                ButtonToolTips.SubstitutionsDescDisabled.text()));
        widgets.add("craftingSubstitutions", button);
        return button;
    }

    private ToggleButton createCraftingFluidSubstitutionButton(WidgetContainer widgets) {
        var button = new ToggleButton(
                Icon.S_FLUID_SUBSTITUTION_ENABLED,
                Icon.S_FLUID_SUBSTITUTION_DISABLED,
                menu::setSubstituteFluids);
        button.setHalfSize(true);
        button.setDisableBackground(true);
        button.setTooltipOn(List.of(
                ButtonToolTips.FluidSubstitutions.text(),
                ButtonToolTips.FluidSubstitutionsDescEnabled.text()));
        button.setTooltipOff(List.of(
                ButtonToolTips.FluidSubstitutions.text(),
                ButtonToolTips.FluidSubstitutionsDescDisabled.text()));
        widgets.add("craftingFluidSubstitutions", button);
        return button;
    }

    @Override
    public void drawBackgroundLayer(GuiGraphics guiGraphics, Rect2i bounds, Point mouse) {
        BG.dest(bounds.getX() + 8, bounds.getY() + bounds.getHeight() - 165).blit(guiGraphics);

        var absMouseX = bounds.getX() + mouse.getX();
        var absMouseY = bounds.getY() + mouse.getY();
        if (menu.substituteFluids && fluidSubstitutionsBtn.isMouseOver(absMouseX, absMouseY)) {
            for (var slotIndex : menu.slotsSupportingFluidSubstitution) {
                drawSlotGreenBG(bounds, guiGraphics, menu.getCraftingGridSlots()[slotIndex]);
            }
        }
    }

    private void drawSlotGreenBG(Rect2i bounds, GuiGraphics guiGraphics, Slot slot) {
        int x = bounds.getX() + slot.x;
        int y = bounds.getY() + slot.y;
        guiGraphics.fill(x, y, x + 16, y + 16, 0xff7ac25f);
    }

    @Override
    public void updateBeforeRender() {
        this.substitutionsBtn.setState(this.menu.substitute);
        this.fluidSubstitutionsBtn.setState(this.menu.substituteFluids);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        clearBtn.setVisibility(visible);
        substitutionsBtn.setVisibility(visible);
        fluidSubstitutionsBtn.setVisibility(visible);

        screen.setSlotsHidden(SlotSemantics.CRAFTING_GRID, !visible);
        screen.setSlotsHidden(SlotSemantics.CRAFTING_RESULT, !visible);
    }
}
