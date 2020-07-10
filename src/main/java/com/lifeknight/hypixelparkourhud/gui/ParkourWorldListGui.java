package com.lifeknight.hypixelparkourhud.gui;

import com.lifeknight.hypixelparkourhud.gui.components.*;
import com.lifeknight.hypixelparkourhud.mod.ParkourWorld;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.lifeknight.hypixelparkourhud.mod.Core.*;
import static com.lifeknight.hypixelparkourhud.utilities.Miscellaneous.*;
import static net.minecraft.util.EnumChatFormatting.GRAY;

public class ParkourWorldListGui extends GuiScreen {
    private final List<LifeKnightButton> lifeKnightButtons = new ArrayList<>();
    private ScrollBar scrollBar;
    private LifeKnightTextField searchField;
    private String searchInput = "", listMessage = "";

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        drawCenteredString(fontRendererObj, listMessage, get2ndPanelCenter(), super.height / 2, 0xffffffff);
        drawCenteredString(fontRendererObj, "Parkour Worlds", getScaledWidth(150), getScaledHeight(60), 0xffffffff);
        drawVerticalLine(getScaledWidth(300), 0, super.height, 0xffffffff);
        searchField.drawTextBoxAndName();

        if (lifeKnightButtons.size() != 0) {
            int panelHeight = lifeKnightButtons.size() * 30;

            scrollBar.height = (int) (super.height * (super.height / (double) panelHeight));
            int j = Mouse.getDWheel() / 7;
            scrollBar.visible = !(scrollBar.height >= super.height);
            while (j > 0 && lifeKnightButtons.get(0).yPosition + j > 10) {
                j--;
            }

            while (j < 0 && lifeKnightButtons.get(lifeKnightButtons.size() - 1).yPosition + 30 + j < super.height - 10) {
                j++;
            }
            for (LifeKnightButton lifeknightButton : lifeKnightButtons) {
                lifeknightButton.yPosition += j;
            }
            scrollBar.yPosition = (int) ((super.height * (-lifeKnightButtons.get(0).yPosition - 10) / (double) (panelHeight - super.height)) * ((super.height - scrollBar.height) / (double) super.height)) + 8;
        } else {
            scrollBar.visible = false;
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public void initGui() {
        searchField = new LifeKnightTextField(0, getScaledWidth(75), this.height - 40, getScaledWidth(150), 20, "Search") {

            @Override
            public boolean textboxKeyTyped(char p_146201_1_, int p_146201_2_) {
                if (super.textboxKeyTyped(p_146201_1_, p_146201_2_)) {
                    this.handleInput();
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public void handleInput() {
                searchInput = this.getText();
                listItems();
            }
        };
        super.buttonList.add(scrollBar = new ScrollBar() {
            @Override
            public void onDrag(int scroll) {
                scroll = -scroll;
                int scaledScroll = (int) (scroll * (lifeKnightButtons.size() * 30) / (double) ParkourWorldListGui.super.height);
                while (scaledScroll > 0 && lifeKnightButtons.get(0).originalYPosition + scaledScroll > 10) {
                    scaledScroll--;
                }
                while (scaledScroll < 0 && lifeKnightButtons.get(lifeKnightButtons.size() - 1).originalYPosition + 30 + scaledScroll < ParkourWorldListGui.super.height - 10) {
                    scaledScroll++;
                }
                for (LifeKnightButton lifeKnightButton : lifeKnightButtons) {
                    lifeKnightButton.yPosition = lifeKnightButton.originalYPosition + scaledScroll;
                }
            }

            @Override
            public void onMousePress() {
                for (LifeKnightButton lifeKnightButton : lifeKnightButtons) {
                    lifeKnightButton.updateOriginalYPosition();
                }
            }
        });
        super.buttonList.add(new LifeKnightButton("Back", 5, 5, 5, 50) {
            @Override
            public void work() {
                openGui(defaultGui);
            }
        });
        listItems();
    }

    public boolean doesGuiPauseGame() {
        return false;
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        if (button instanceof LifeKnightButton) {
            ((LifeKnightButton) button).work();
        }
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        searchField.textboxKeyTyped(typedChar, keyCode);
        super.keyTyped(typedChar, keyCode);
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        searchField.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    private void listItems() {
        lifeKnightButtons.clear();
        this.buttonList.removeIf(guiButton -> guiButton instanceof LifeKnightButton);

        for (ParkourWorld parkourWorld : ParkourWorld.getParkourWorlds()) {
            if (searchInput.isEmpty() || parkourWorld.getLocation().toLowerCase().contains(searchInput.toLowerCase()) || (parkourWorld.isType() ? "housing" : "lobby").contains(searchInput.toLowerCase())) {
                LifeKnightButton lifeKnightButton = new LifeKnightButton(lifeKnightButtons.size(), parkourWorld.getLocation()) {
                    @Override
                    public void work() {
                        openGui(new ParkourWorldGui(parkourWorld, ParkourWorldListGui.this));
                    }
                };
                lifeKnightButtons.add(lifeKnightButton);
            }
        }
        listMessage = lifeKnightButtons.size() == 0 ? GRAY + "No items found" : "";

        this.buttonList.addAll(lifeKnightButtons);
    }
}
