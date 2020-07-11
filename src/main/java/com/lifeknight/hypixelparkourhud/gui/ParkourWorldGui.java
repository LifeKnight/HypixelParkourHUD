package com.lifeknight.hypixelparkourhud.gui;

import com.lifeknight.hypixelparkourhud.gui.components.*;
import com.lifeknight.hypixelparkourhud.mod.ParkourSession;
import com.lifeknight.hypixelparkourhud.mod.ParkourWorld;
import com.lifeknight.hypixelparkourhud.utilities.Miscellaneous;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.lifeknight.hypixelparkourhud.mod.Core.openGui;
import static com.lifeknight.hypixelparkourhud.utilities.Miscellaneous.*;
import static net.minecraft.util.EnumChatFormatting.*;

public class ParkourWorldGui extends GuiScreen {
    private final List<ListItemButton> listItemButtons = new ArrayList<>();
    private final List<LifeKnightButton> openButtons = new ArrayList<>();
    private final ParkourWorld parkourWorld;
    private ConfirmButton clearButton;
    private ScrollBar scrollBar;
    private LifeKnightTextField searchField;
    public ListItemButton selectedItem;
    public LifeKnightButton workWithSelectedbuttonButton;
    private String searchInput = "", listMessage = "";
    public GuiScreen lastGui;
    private boolean showType = true;
    private boolean orderType = true;

    public ParkourWorldGui(ParkourWorld parkourWorld, GuiScreen lastGui) {
        this.parkourWorld = parkourWorld;
        this.lastGui = lastGui;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        drawCenteredString(fontRendererObj, listMessage, get2ndPanelCenter(), super.height / 2, 0xffffffff);
        drawCenteredString(fontRendererObj, parkourWorld.getLocation(), getScaledWidth(150), getScaledHeight(60), 0xffffffff);
        drawVerticalLine(getScaledWidth(300), 0, super.height, 0xffffffff);
        searchField.drawTextBoxAndName();

        if (listItemButtons.size() != 0) {
            int panelHeight = listItemButtons.size() * 30;

            scrollBar.height = (int) (super.height * (super.height / (double) panelHeight));
            int j = Mouse.getDWheel() / 7;
            scrollBar.visible = !(scrollBar.height >= super.height);
            while (j > 0 && listItemButtons.get(0).yPosition + j > 10) {
                j--;
            }

            while (j < 0 && listItemButtons.get(listItemButtons.size() - 1).yPosition + 30 + j < super.height - 10) {
                j++;
            }
            for (ListItemButton listItemButton : listItemButtons) {
                listItemButton.yPosition += j;
            }
            for (LifeKnightButton lifeKnightButton : openButtons) {
                lifeKnightButton.yPosition += j;
            }

            scrollBar.yPosition = (int) ((super.height * (-listItemButtons.get(0).yPosition - 10) / (double) (panelHeight - super.height)) * ((super.height - scrollBar.height) / (double) super.height)) + 8;
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

        super.buttonList.add(new LifeKnightButton("Order: Date", 20, getScaledWidth(75), getScaledHeight(135), getScaledWidth(150)) {
            @Override
            public void work() {
                orderType = !orderType;
                this.displayString = "Order: " + (orderType ? "Date" : "Time");
                listItems();
            }
        });

        super.buttonList.add(new LifeKnightButton("", 20, getScaledWidth(75), getScaledHeight(195), getScaledWidth(150)) {
            @Override
            public void work() {
                showType = !showType;
                listItems();
            }

            @Override
            public void drawButton(Minecraft mc, int mouseX, int mouseY) {
                displayString = showType ? GREEN + "Visible" : RED + "Deleted";
                super.drawButton(mc, mouseX, mouseY);
            }
        });

        super.buttonList.add(workWithSelectedbuttonButton = new LifeKnightButton("", 3, getScaledWidth(75), getScaledHeight(260), getScaledWidth(150)) {
            @Override
            public void work() {
                workWithSelectedButton();
            }

            @Override
            public void drawButton(Minecraft mc, int mouseX, int mouseY) {
                displayString = showType ? "Delete" : "Restore";
                super.drawButton(mc, mouseX, mouseY);
            }
        });
        workWithSelectedbuttonButton.visible = false;

        super.buttonList.add(clearButton = new ConfirmButton(4, getScaledWidth(75), getScaledHeight(325), getScaledWidth(150), "Clear", RED + "Confirm") {
            @Override
            public void onConfirm() {
                parkourWorld.clearSessions();
                listItems();
            }
        });
        clearButton.visible = false;

        super.buttonList.add(scrollBar = new ScrollBar() {
            @Override
            public void onDrag(int scroll) {
                scroll = -scroll;
                int scaledScroll = (int) (scroll * (listItemButtons.size() * 30) / (double) ParkourWorldGui.super.height);
                while (scaledScroll > 0 && listItemButtons.get(0).originalYPosition + scaledScroll > 10) {
                    scaledScroll--;
                }
                while (scaledScroll < 0 && listItemButtons.get(listItemButtons.size() - 1).originalYPosition + 30 + scaledScroll < ParkourWorldGui.super.height - 10) {
                    scaledScroll++;
                }
                for (ListItemButton listItemButton : listItemButtons) {
                    listItemButton.yPosition = listItemButton.originalYPosition + scaledScroll;
                }
                for (LifeKnightButton openButton : openButtons) {
                    openButton.yPosition = openButton.originalYPosition + scaledScroll;
                }
            }

            @Override
            public void onMousePress() {
                for (ListItemButton listItemButton : listItemButtons) {
                    listItemButton.updateOriginalYPosition();
                }
                for (LifeKnightButton openButton : openButtons) {
                    openButton.updateOriginalYPosition();
                }
            }
        });
        super.buttonList.add(new LifeKnightButton("Back", 5, 5, 5, 50) {
            @Override
            public void work() {
                openGui(lastGui);
            }
        });
        listItems();
    }

    public boolean doesGuiPauseGame() {
        return false;
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        if (button instanceof ListItemButton) {
            ((ListItemButton) button).work();
        } else if (button instanceof LifeKnightButton) {
            ((LifeKnightButton) button).work();
        }
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 0xD3 && selectedItem != null) {
            workWithSelectedButton();
        } else {
            searchField.textboxKeyTyped(typedChar, keyCode);
            super.keyTyped(typedChar, keyCode);
        }
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        searchField.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
        boolean aButtonHasBeenSelected = false;
        for (ListItemButton listItemButton : listItemButtons) {
            if (listItemButton.isSelectedButton) {
                aButtonHasBeenSelected = true;
                break;
            }
        }
        workWithSelectedbuttonButton.visible = aButtonHasBeenSelected;
    }

    protected void workWithSelectedButton() {
        parkourWorld.toggleByLocation(selectedItem.displayString, orderType);
        selectedItem.visible = false;
        workWithSelectedbuttonButton.visible = false;
        selectedItem = null;
        listItems();
    }

    private void listItems() {
        listItemButtons.clear();
        openButtons.clear();
        this.buttonList.removeIf(guiButton -> guiButton instanceof ListItemButton || guiButton.displayString.equals(">"));

        for (ParkourSession parkourSession : parkourWorld.getSessionsOrdered(orderType)) {
            if ((searchInput.isEmpty() || parkourSession.getFormattedDate().toLowerCase().contains(searchInput.toLowerCase())) && ((showType && !parkourSession.isDeleted()) || (!showType && parkourSession.isDeleted()))) {
                ListItemButton listItemButton = new ListItemButton(listItemButtons.size() + 6, orderType ? parkourSession.getFormattedDate() : Miscellaneous.formatTimeFromMilliseconds(parkourSession.getMillisecondsElapsed())) {
                    @Override
                    public void work() {
                        if (this.isSelectedButton) {
                            this.isSelectedButton = false;
                            selectedItem = null;
                        } else {
                            this.isSelectedButton = true;
                            selectedItem = this;
                        }
                    }
                };
                LifeKnightButton lifeKnightButton = new LifeKnightButton(listItemButtons.size() + 1000, listItemButton.xPosition + listItemButton.width + 10,
                        10 + listItemButtons.size() * 30,
                        20,
                        20, ">") {
                    @Override
                    public void work() {
                        openGui(new ParkourSessionGui(parkourSession, ParkourWorldGui.this));
                    }
                };
                listItemButtons.add(listItemButton);
                openButtons.add(lifeKnightButton);
            }
        }

        listMessage = listItemButtons.size() == 0 ? GRAY + "No items found" : "";

        clearButton.visible = listItemButtons.size() > 1;

        this.buttonList.addAll(listItemButtons);

        this.buttonList.addAll(openButtons);
    }
}
