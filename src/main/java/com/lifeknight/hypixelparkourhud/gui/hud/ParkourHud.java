package com.lifeknight.hypixelparkourhud.gui.hud;

import com.lifeknight.hypixelparkourhud.gui.Manipulable;
import com.lifeknight.hypixelparkourhud.mod.ParkourSession;
import com.lifeknight.hypixelparkourhud.mod.ParkourWorld;
import com.lifeknight.hypixelparkourhud.utilities.Miscellaneous;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

import static net.minecraft.util.EnumChatFormatting.*;

import static com.lifeknight.hypixelparkourhud.mod.Mod.*;

public class ParkourHud extends Manipulable {
    final float[] light = {
            66, 66, 66
    };
    final float[] dark = {
            33, 33, 33
    };
    final float[] selected = {
            89, 126, 247
    };

    public ParkourHud(String name, int defaultX, int defaultY) {
        super(name, defaultX, defaultY);
    }

    @Override
    public int getWidth() {
        return 200;
    }

    @Override
    public int getHeight() {
        ParkourSession parkourSession;
        if ((parkourSession = ParkourSession.getCurrentParkourSession()) != null) {
            ParkourWorld parkourWorld = ParkourWorld.getCurrentParkourWorld();
            if (parkourWorld != null && parkourWorld.getSessions().size() != 0) {
                ParkourSession previousParkourSession = parkourWorld.getSessions().get(parkourWorld.getSessions().size() - (sessionIsRunning ? 1 : 2));
                return previousParkourSession.getCheckpointTimes().size() * 12 + 12;
            }
            return parkourSession.getCheckpointTimes().size() * 12 + 12;
        }
        return 12;
    }

    @Override
    public void drawButton(Minecraft minecraft, int mouseX, int mouseY, int xPosition, int yPosition, int width, int height, boolean isSelectedButton) {
        ParkourWorld parkourWorld = ParkourWorld.getCurrentParkourWorld();
        ParkourSession parkourSession = sessionIsRunning || parkourWorld == null || parkourWorld.getSessions().size() == 0 ? ParkourSession.getCurrentParkourSession() : parkourWorld.getLatestSession();

        if (parkourWorld != null && parkourWorld.getSessions().size() != 0) {
            ParkourSession previousParkourSession = parkourWorld.getSessions().get(parkourWorld.getSessions().size() - (sessionIsRunning ? 1 : 2));
            drawRectangleWithStrings(xPosition, yPosition, xPosition + getWidth(), yPosition + 12, dark, 180F, parkourWorld.getLocation(), parkourSession == null ? "" : (!sessionIsRunning ? ((previousParkourSession.getMillisecondsElapsed() > parkourSession.getMillisecondsElapsed() ? (GREEN + "-") : (RED + "+")) + Math.abs((previousParkourSession.getMillisecondsElapsed() - parkourSession.getMillisecondsElapsed())) / 1000.F + " ") : "") + WHITE + Miscellaneous.formatTimeFromMilliseconds(parkourSession.getMillisecondsElapsed()));
        } else {
            drawRectangleWithStrings(xPosition, yPosition, xPosition + getWidth(), yPosition + 12, dark, 180F, parkourWorld == null ? "NULL" : parkourWorld.getLocation(), parkourSession == null ? "" : Miscellaneous.formatTimeFromMilliseconds(parkourSession.getMillisecondsElapsed()));
        }
        if (parkourSession != null) {
            if (parkourWorld != null && parkourWorld.getSessions().size() != 0) {
                ParkourSession previousParkourSession = parkourWorld.getSessions().get(parkourWorld.getSessions().size() - (sessionIsRunning ? 1 : 2));
                for (int i = 0; i < previousParkourSession.getCheckpointTimes().size(); i++) {
                    float[] color = i % 2 == 0 ? light : dark;
                    if (parkourSession.getCheckpointTimes().size() > i) {
                        long checkpointTime = parkourSession.getCheckpointTimes().get(i);
                        long previousCheckpointTime = previousParkourSession.getCheckpointTimes().get(i);
                        drawRectangleWithStrings(xPosition, yPosition + (12 * (i + 1)), xPosition + getWidth(), yPosition + (12 * (i + 1)) + 12, color, 180F, "Checkpoint #" + (i + 1),
                                (previousCheckpointTime > checkpointTime ? (GREEN + "-") : (RED + "+")) + Math.abs(previousCheckpointTime - checkpointTime) / 1000.F + " " + WHITE + Miscellaneous.formatTimeFromMilliseconds(timeDisplayType.getValue() == 0 ? parkourSession.getTimeUpToCheckpoint(i + 1) : checkpointTime));
                    } else {
                        long checkpointTime = previousParkourSession.getCheckpointTimes().get(i);
                        drawRectangleWithStrings(xPosition, yPosition + (12 * (i + 1)), xPosition + getWidth(), yPosition + (12 * (i + 1)) + 12, i == parkourSession.getCheckpointTimes().size() ? selected : color, 180F, "Checkpoint #" + (i + 1), (i == parkourSession.getCheckpointTimes().size() - 1 && sessionIsRunning) ?
                                Miscellaneous.formatTimeFromMilliseconds(parkourSession.getMillisecondsElapsed() - parkourSession.getLastCheckpointTime()) :
                                YELLOW + Miscellaneous.formatTimeFromMilliseconds(timeDisplayType.getValue() == 0 ? previousParkourSession.getTimeUpToCheckpoint(i + 1) : checkpointTime));
                    }
                }
            } else {
                for (int i = 0; i < parkourSession.getCheckpointTimes().size(); i++) {
                    float[] color = i % 2 == 0 ? light : dark;
                    long checkpointTime = parkourSession.getCheckpointTimes().get(i);
                    drawRectangleWithStrings(xPosition, yPosition + (12 * (i + 1)), xPosition + getWidth(), yPosition + (12 * (i + 1)) + 12, color, 180F, "Checkpoint #" + (i + 1), (i == parkourSession.getCheckpointTimes().size() - 1 && sessionIsRunning) ?
                            Miscellaneous.formatTimeFromMilliseconds(parkourSession.getMillisecondsElapsed() - parkourSession.getLastCheckpointTime()) :
                            Miscellaneous.formatTimeFromMilliseconds(timeDisplayType.getValue() == 0 ? parkourSession.getTimeUpToCheckpoint(i + 1) : checkpointTime));
                }
            }
        }
    }

    public void render() {
        ParkourWorld parkourWorld = ParkourWorld.getCurrentParkourWorld();
        int xPosition = super.getXCoordinate();
        int yPosition = super.getYCoordinate();

        ParkourSession parkourSession = sessionIsRunning || parkourWorld == null || parkourWorld.getSessions().size() == 0 ? ParkourSession.getCurrentParkourSession() : parkourWorld.getLatestSession();

        if (parkourWorld != null && parkourWorld.getSessions().size() != 0) {
            ParkourSession previousParkourSession = parkourWorld.getSessions().get(parkourWorld.getSessions().size() - (sessionIsRunning ? 1 : 2));
            drawRectangleWithStrings(xPosition, yPosition, xPosition + getWidth(), yPosition + 12, dark, 180F, parkourWorld.getLocation(), parkourSession == null ? "" : (!sessionIsRunning ? ((previousParkourSession.getMillisecondsElapsed() > parkourSession.getMillisecondsElapsed() ? (GREEN + "-") : (RED + "+")) + Math.abs((previousParkourSession.getMillisecondsElapsed() - parkourSession.getMillisecondsElapsed())) / 1000.F + " ") : "") + WHITE + Miscellaneous.formatTimeFromMilliseconds(parkourSession.getMillisecondsElapsed()));
        } else {
            drawRectangleWithStrings(xPosition, yPosition, xPosition + getWidth(), yPosition + 12, dark, 180F, parkourWorld == null ? "NULL" : parkourWorld.getLocation(), parkourSession == null ? "" : Miscellaneous.formatTimeFromMilliseconds(parkourSession.getMillisecondsElapsed()));
        }
        if (parkourSession != null) {
            if (parkourWorld != null && parkourWorld.getSessions().size() != 0) {
                ParkourSession previousParkourSession = parkourWorld.getSessions().get(parkourWorld.getSessions().size() - (sessionIsRunning ? 1 : 2));
                for (int i = 0; i < previousParkourSession.getCheckpointTimes().size(); i++) {
                    float[] color = i % 2 == 0 ? light : dark;
                    if (parkourSession.getCheckpointTimes().size() > i) {
                        long checkpointTime = parkourSession.getCheckpointTimes().get(i);
                        long previousCheckpointTime = previousParkourSession.getCheckpointTimes().get(i);
                        drawRectangleWithStrings(xPosition, yPosition + (12 * (i + 1)), xPosition + getWidth(), yPosition + (12 * (i + 1)) + 12, color, 180F, "Checkpoint #" + (i + 1),
                                (previousCheckpointTime > checkpointTime ? (GREEN + "-") : (RED + "+")) + Math.abs(previousCheckpointTime - checkpointTime) / 1000.F + " " + WHITE + Miscellaneous.formatTimeFromMilliseconds(timeDisplayType.getValue() == 0 ? parkourSession.getTimeUpToCheckpoint(i + 1) : checkpointTime));
                    } else {
                        long checkpointTime = previousParkourSession.getCheckpointTimes().get(i);
                        drawRectangleWithStrings(xPosition, yPosition + (12 * (i + 1)), xPosition + getWidth(), yPosition + (12 * (i + 1)) + 12, i == parkourSession.getCheckpointTimes().size() ? selected : color, 180F, "Checkpoint #" + (i + 1), (i == parkourSession.getCheckpointTimes().size() - 1 && sessionIsRunning) ?
                                Miscellaneous.formatTimeFromMilliseconds(parkourSession.getMillisecondsElapsed() - parkourSession.getLastCheckpointTime()) :
                                YELLOW + Miscellaneous.formatTimeFromMilliseconds(timeDisplayType.getValue() == 0 ? previousParkourSession.getTimeUpToCheckpoint(i + 1) : checkpointTime));
                    }
                }
            } else {
                for (int i = 0; i < parkourSession.getCheckpointTimes().size(); i++) {
                    float[] color = i % 2 == 0 ? light : dark;
                    long checkpointTime = parkourSession.getCheckpointTimes().get(i);
                    drawRectangleWithStrings(xPosition, yPosition + (12 * (i + 1)), xPosition + getWidth(), yPosition + (12 * (i + 1)) + 12, color, 180F, "Checkpoint #" + (i + 1), (i == parkourSession.getCheckpointTimes().size() - 1 && sessionIsRunning) ?
                            Miscellaneous.formatTimeFromMilliseconds(parkourSession.getMillisecondsElapsed() - parkourSession.getLastCheckpointTime()) :
                            Miscellaneous.formatTimeFromMilliseconds(timeDisplayType.getValue() == 0 ? parkourSession.getTimeUpToCheckpoint(i + 1) : checkpointTime));
                }
            }
        }
        drawEmptyBox(xPosition, yPosition, xPosition + getWidth(), yPosition + getHeight(), new float[]{28, 28, 28}, 200);
    }

    public void drawRectangleWithStrings(int left, int top, int right, int bottom, float[] colors, float alpha, String leftText, String rightText) {
        drawRectangle(left, top, right, bottom, colors, alpha);
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;
        fontRenderer.drawString(leftText, left + 1, top + 2, 0xffffffff, hudTextShadow.getValue());
        fontRenderer.drawString(rightText, right - fontRenderer.getStringWidth(rightText), top + 2, 0xffffffff, hudTextShadow.getValue());
    }

    public void drawEmptyBox(int left, int top, int right, int bottom, float[] colors, float alpha) {
        drawHorizontalLine(left, right, top, colors, alpha);
        drawHorizontalLine(left, right, bottom, colors, alpha);

        drawVerticalLine(left, top, bottom, colors, alpha);
        drawVerticalLine(right, top, bottom, colors, alpha);
    }

    protected void drawHorizontalLine(int startX, int endX, int y, float[] colors, float alpha) {
        if (endX < startX) {
            int i = startX;
            startX = endX;
            endX = i;
        }

        drawRectangle(startX, y, endX + 1, y + 1, colors, alpha);
    }

    /**
     * Draw a 1 pixel wide vertical line. Args : x, y1, y2, color
     */
    protected void drawVerticalLine(int x, int startY, int endY, float[] colors, float alpha) {
        if (endY < startY) {
            int i = startY;
            startY = endY;
            endY = i;
        }

        drawRectangle(x, startY + 1, x + 1, endY, colors, alpha);
    }

    public static void drawRectangle(int left, int top, int right, int bottom, float[] colors, float alpha) {
        if (left < right) {
            int i = left;
            left = right;
            right = i;
        }

        if (top < bottom) {
            int j = top;
            top = bottom;
            bottom = j;
        }

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(colors[0] / 255F, colors[1] / 255F, colors[2] / 255F, alpha / 255F);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(left, bottom, 0.0D).endVertex();
        worldrenderer.pos(right, bottom, 0.0D).endVertex();
        worldrenderer.pos(right, top, 0.0D).endVertex();
        worldrenderer.pos(left, top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
}