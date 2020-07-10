package com.lifeknight.hypixelparkourhud.gui.hud;

import com.lifeknight.hypixelparkourhud.gui.Manipulable;
import com.lifeknight.hypixelparkourhud.gui.components.LifeKnightButton;
import com.lifeknight.hypixelparkourhud.utilities.Miscellaneous;
import com.lifeknight.hypixelparkourhud.utilities.Text;
import com.lifeknight.hypixelparkourhud.variables.LifeKnightBoolean;
import com.lifeknight.hypixelparkourhud.variables.LifeKnightCycle;
import com.lifeknight.hypixelparkourhud.variables.LifeKnightString;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import static com.lifeknight.hypixelparkourhud.mod.Core.hudTextShadow;
import static net.minecraft.util.EnumChatFormatting.*;

public abstract class EnhancedHudText extends Manipulable {
    public static final List<EnhancedHudText> textToRender = new ArrayList<>();
    private final String prefix;
    private final LifeKnightBoolean hudTextVisible;
    private final LifeKnightCycle separator;
    private final LifeKnightCycle prefixColor;
    private final LifeKnightCycle contentColor;
    private final LifeKnightCycle alignment;
    private final LifeKnightString lastString;
    public final ArrayList<LifeKnightButton> connectedButtons = new ArrayList<>();

    public EnhancedHudText(String name, int defaultX, int defaultY, String prefix, LifeKnightCycle separator, LifeKnightCycle prefixColor, LifeKnightCycle contentColor, LifeKnightCycle alignment, LifeKnightBoolean hudTextVisible) {
        super(name, defaultX, defaultY);
        this.prefix = prefix;
        this.hudTextVisible = hudTextVisible;
        this.separator = separator;
        this.prefixColor = prefixColor;
        this.contentColor = contentColor;
        this.alignment = alignment;
        lastString = new LifeKnightString("Last String", name + " HUD Text", "");
        lastString.setShowInLifeKnightGui(false);
        hudTextVisible.setShowInLifeKnightGui(false);
        separator.setShowInLifeKnightGui(false);
        prefixColor.setShowInLifeKnightGui(false);
        contentColor.setShowInLifeKnightGui(false);
        alignment.setShowInLifeKnightGui(false);

        connectedButtons.add(new LifeKnightButton("", 0, 0, 0, 100) {
            @Override
            public void work() {
                EnhancedHudText.this.hudTextVisible.toggle();
            }

            @Override
            public void drawButton(Minecraft mc, int mouseX, int mouseY) {
                this.displayString = EnhancedHudText.this.hudTextVisible.getValue() ? GREEN + "Shown" : RED + "Hidden";
                super.drawButton(mc, mouseX, mouseY);
            }
        });
        if (!prefix.isEmpty()) {
            connectedButtons.add(new LifeKnightButton("Separator: " + separator.getCurrentValueString(), 0, 0, 0, 100) {
                @Override
                public void work() {
                    separator.next();
                }

                @Override
                public void drawButton(Minecraft mc, int mouseX, int mouseY) {
                    this.displayString = "Separator: " + separator.getCurrentValueString().replace(" ", "");
                    super.drawButton(mc, mouseX, mouseY);
                }
            });

            connectedButtons.add(new LifeKnightButton("Prefix Color: " + getEnumChatFormatting(prefixColor) + prefixColor.getCurrentValueString(), 0, 0, 0, 100) {
                @Override
                public void work() {
                    prefixColor.next();
                }

                @Override
                public void drawButton(Minecraft mc, int mouseX, int mouseY) {
                    this.displayString = "Prefix Color: " + getEnumChatFormatting(prefixColor) + prefixColor.getCurrentValueString();
                    int i;
                    if (!((i = Minecraft.getMinecraft().fontRendererObj.getStringWidth(this.displayString) + 15) < 100)) {
                        this.width = i;
                    } else {
                        this.width = 100;
                    }
                    super.drawButton(mc, mouseX, mouseY);
                }
            });
        }

        connectedButtons.add(new LifeKnightButton("Content Color: " + getEnumChatFormatting(contentColor) + contentColor.getCurrentValueString(), 0, 0, 0, 100) {
            @Override
            public void work() {
                contentColor.next();
            }

            @Override
            public void drawButton(Minecraft mc, int mouseX, int mouseY) {
                this.displayString = "Content Color: " + getEnumChatFormatting(contentColor) + contentColor.getCurrentValueString();
                int i;
                if (!((i = Minecraft.getMinecraft().fontRendererObj.getStringWidth(this.displayString) + 15) < 100)) {
                    this.width = i;
                } else {
                    this.width = 100;
                }
                super.drawButton(mc, mouseX, mouseY);
            }

        });

        connectedButtons.add(new LifeKnightButton("", 0, 0, 0, 100) {
            @Override
            public void work() {
                alignment.next();
            }

            @Override
            public void drawButton(Minecraft mc, int mouseX, int mouseY) {
                this.displayString = "Alignment: " + alignment.getCurrentValueString();
                super.drawButton(mc, mouseX, mouseY);
            }
        });

        super.connectedComponents.addAll(connectedButtons);
        textToRender.add(this);
    }

    public EnhancedHudText(String name, int defaultX, int defaultY, String prefix) {
        this(name, defaultX, defaultY, prefix, new LifeKnightCycle(name + " Prefix Color", name + " HUD Text", new ArrayList<>(Arrays.asList(" > ", ": ", " | ", " - "))),
                new LifeKnightCycle("Color", name + " HUD Text", new ArrayList<>(Arrays.asList(
                        "Red",
                        "Gold",
                        "Yellow",
                        "Green",
                        "Aqua",
                        "Blue",
                        "Light Purple",
                        "Dark Red",
                        "Dark Green",
                        "Dark Aqua",
                        "Dark Blue",
                        "Dark Purple",
                        "White",
                        "Gray",
                        "Dark Gray",
                        "Black"
                )), 12), new LifeKnightCycle("ContentColor", name + " HUD Text", new ArrayList<>(Arrays.asList(
                        "Red",
                        "Gold",
                        "Yellow",
                        "Green",
                        "Aqua",
                        "Blue",
                        "Light Purple",
                        "Dark Red",
                        "Dark Green",
                        "Dark Aqua",
                        "Dark Blue",
                        "Dark Purple",
                        "White",
                        "Gray",
                        "Dark Gray",
                        "Black"
                )), 12), new LifeKnightCycle("Alignment", name + " HUD Text", new ArrayList<>(Arrays.asList(
                        "Left",
                        "Center",
                        "Right"
                ))), new LifeKnightBoolean("Visible", name + " HUD Text", true));
    }

    public EnhancedHudText(String name, int defaultX, int defaultY) {
        this(name, defaultX, defaultY, "");
    }

    public EnhancedHudText(String name, int defaultX, int defaultY, String prefix, int defaultPrefixColor) {
        this(name, defaultX, defaultY, prefix);
        prefixColor.setCurrentValue(defaultPrefixColor);
    }

    public EnhancedHudText(String name) {
        this(name, 0, 0);
    }

    public abstract String getTextToDisplay();

    @Override
    public String getDisplayText() {
        if (prefix.isEmpty()) {
            return getEnumChatFormatting(contentColor) + getTextToDisplay();
        } else {
            return getEnumChatFormatting(prefixColor) + prefix + separator.getCurrentValueString() + getEnumChatFormatting(contentColor) + getTextToDisplay();
        }
    }

    public EnumChatFormatting getEnumChatFormatting(LifeKnightCycle colorCycle) {
        switch (colorCycle.getCurrentValueString()) {
            case "Red":
                return RED;
            case "Gold":
                return GOLD;
            case "Yellow":
                return YELLOW;
            case "Green":
                return GREEN;
            case "Aqua":
                return AQUA;
            case "Blue":
                return BLUE;
            case "Light Purple":
                return LIGHT_PURPLE;
            case "Dark Red":
                return DARK_RED;
            case "Dark Green":
                return DARK_GREEN;
            case "Dark Aqua":
                return DARK_AQUA;
            case "Dark Blue":
                return DARK_BLUE;
            case "Dark Purple":
                return DARK_PURPLE;
            case "White":
                return WHITE;
            case "Gray":
                return GRAY;
            case "Dark Gray":
                return DARK_GRAY;
        }
        return BLACK;
    }

    public abstract boolean isVisible();

    public void render() {
        if (this.isVisible() && hudTextVisible.getValue()) {
            Minecraft.getMinecraft().fontRendererObj.drawString(getDisplayText(), getXCoordinate(), getYCoordinate() + 1, 0xffffffff, hudTextShadow.getValue());
        }
    }

    @Override
    public void updatePosition(int x, int y) {
        updateString(getDisplayText());
        super.updatePosition(x, y);
    }

    @Override
    public void drawButton(Minecraft minecraft, int mouseX, int mouseY, int xPosition, int yPosition, int width, int height, boolean isSelectedButton) {
        Minecraft.getMinecraft().fontRendererObj.drawString(getDisplayText(), xPosition + width / 2F - (Minecraft.getMinecraft().fontRendererObj.getStringWidth(getDisplayText())) / 2F, yPosition + 1, 0xffffffff, hudTextShadow.getValue());

        for (LifeKnightButton lifeKnightButton : connectedButtons) {
            lifeKnightButton.visible = isSelectedButton;
            lifeKnightButton.xPosition = xPosition - 120 < 0 ? xPosition + width + 20 : xPosition - 120;
            lifeKnightButton.yPosition = yPosition + connectedButtons.size() * 30 + 5 > Miscellaneous.getGameHeight() ?
                    yPosition - 30 * connectedButtons.indexOf(lifeKnightButton) - 2 :
                    yPosition + connectedButtons.indexOf(lifeKnightButton) * 30 - 2;
        }
    }

    @Override
    public int getXCoordinate() {
        int xCoordinate = super.positionX.getValue();
        int toAddX;
        switch (alignment.getValue()) {
            case 0:
                toAddX = 0;
                break;
            case 1:
                toAddX = (int) ((- Minecraft.getMinecraft().fontRendererObj.getStringWidth(getDisplayText()) / 2F) + Minecraft.getMinecraft().fontRendererObj.getStringWidth(lastString.getValue()) / 2F);
                break;
            default:
                toAddX = Minecraft.getMinecraft().fontRendererObj.getStringWidth(lastString.getValue()) - (Minecraft.getMinecraft().fontRendererObj.getStringWidth(getDisplayText()));
                break;
        }
        xCoordinate += toAddX;

        while (xCoordinate + this.getWidth() > Miscellaneous.getGameWidth()) {
            xCoordinate--;
        }
        return Math.max(xCoordinate, 0);
    }

    public void updateString(String newString) {
        lastString.setValue(Text.removeFormattingCodes(newString));
    }

    public static void doRender() {
        for (EnhancedHudText hudText : textToRender) {
            hudText.render();
        }
    }

    @Override
    public int getWidth() {
        return Minecraft.getMinecraft().fontRendererObj.getStringWidth(getDisplayText());
    }

    @Override
    public int getHeight() {
        return 10;
    }

    public void setVisibility(boolean newVisibility) {
        hudTextVisible.setValue(newVisibility);
    }

    public void setSeparator(int newSeparatorId) {
        separator.setCurrentValue(newSeparatorId);
    }

    public void setPrefixColor(int newPrefixColorId) {
        prefixColor.setCurrentValue(newPrefixColorId);
    }

    public void setContentColor(int newContentColorId) {
        contentColor.setCurrentValue(newContentColorId);
    }

    public boolean hudTextVisible() {
        return hudTextVisible.getValue();
    }
}
