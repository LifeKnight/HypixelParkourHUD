package com.lifeknight.hypixelparkourhud.gui;

import com.lifeknight.hypixelparkourhud.gui.components.LifeKnightButton;
import com.lifeknight.hypixelparkourhud.mod.ParkourSession;
import com.lifeknight.hypixelparkourhud.utilities.Miscellaneous;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;

import static com.lifeknight.hypixelparkourhud.mod.Core.openGui;
import static com.lifeknight.hypixelparkourhud.mod.Core.timeDisplayType;
import static com.lifeknight.hypixelparkourhud.utilities.Miscellaneous.getScaledHeight;
import static net.minecraft.util.EnumChatFormatting.*;

public class ParkourSessionGui extends GuiScreen {
    private final ParkourSession parkourSession;
    private final GuiScreen lastGui;

    public ParkourSessionGui(ParkourSession parkourSession, GuiScreen lastGui) {
        this.parkourSession = parkourSession;
        this.lastGui = lastGui;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        drawCenteredString(fontRendererObj, parkourSession.getLocation() + WHITE + " - " + GREEN + parkourSession.getFormattedDate(), super.width / 2, getScaledHeight(100), 0xffffffff);
        drawCenteredString(fontRendererObj, "Total Time Elapsed: " + AQUA + Miscellaneous.formatTimeFromMilliseconds(parkourSession.getMillisecondsElapsed()), super.width / 2, getScaledHeight(150), 0xffffffff);

        for (int i = 0; i < parkourSession.getCheckpointTimes().size(); i++) {
            drawCenteredString(
                    fontRendererObj,
                    GOLD + "Checkpoint #" + (i + 1) + " - " + WHITE + Miscellaneous.formatTimeFromMilliseconds(timeDisplayType.getValue() == 0 ? parkourSession.getTimeUpToCheckpoint(i + 1) : parkourSession.getCheckpointTimes().get(i)),
                    super.width / 2,
                    getScaledHeight(200 + (i * 25)),
                    0xffffffff);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void initGui() {
        super.buttonList.add(new LifeKnightButton("Back", 0, 5, 5, 50) {
            @Override
            public void work() {
                openGui(lastGui);
            }
        });
        super.buttonList.add(new LifeKnightButton("", 1, super.width - 80, 5, 75) {
            boolean hasConfirmed = false;

            @Override
            public void work() {
                if (hasConfirmed) {
                    parkourSession.toggleDelete();
                    hasConfirmed = false;
                } else {
                    hasConfirmed = true;
                }
            }

            public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
                boolean returnValue = super.mousePressed(mc, mouseX, mouseY);
                if (!returnValue) {
                    hasConfirmed = false;
                }
                return returnValue;
            }

            @Override
            public void drawButton(Minecraft mc, int mouseX, int mouseY) {
                if (hasConfirmed) {
                    displayString = parkourSession.isDeleted() ? GREEN + "Restore" : RED + "Delete";
                } else {
                    displayString = parkourSession.isDeleted() ? "Restore" : "Delete";
                }
                super.drawButton(mc, mouseX, mouseY);
            }
        });
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        ((LifeKnightButton) button).work();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
