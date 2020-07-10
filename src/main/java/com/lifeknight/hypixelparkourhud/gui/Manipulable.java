package com.lifeknight.hypixelparkourhud.gui;

import com.lifeknight.hypixelparkourhud.utilities.Miscellaneous;
import com.lifeknight.hypixelparkourhud.variables.LifeKnightNumber;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

public abstract class Manipulable {
    public static final List<Manipulable> manipulableComponents = new ArrayList<>();
    public final List<Object> connectedComponents = new ArrayList<>();
    public final LifeKnightNumber.LifeKnightInteger positionX;
    public final LifeKnightNumber.LifeKnightInteger positionY;

    public Manipulable(String name, int defaultX, int defaultY) {
        manipulableComponents.add(this);
        this.positionX = new LifeKnightNumber.LifeKnightInteger("Position X", name, defaultX, 0, 1920);
        this.positionY = new LifeKnightNumber.LifeKnightInteger("Position Y", name, defaultY, 0, 1080);
        positionX.setShowInLifeKnightGui(false);
        positionY.setShowInLifeKnightGui(false);
    }

    public void updatePosition(int x, int y) {
        positionX.setValue(Miscellaneous.scaleTo1080pWidth(x));
        positionY.setValue(Miscellaneous.scaleTo1080pHeight(y));
    }

    public abstract int getWidth();

    public abstract int getHeight();

    public int getXCoordinate() {
        int returnValue;
        if ((returnValue = Miscellaneous.scaleFrom1080pWidth(positionX.getValue())) < -3) {
            returnValue = -3;
            positionX.setValue(returnValue);
        } else if (returnValue + getWidth() > Miscellaneous.getGameWidth() + 3) {
            returnValue = Miscellaneous.getGameWidth() + 3 - getWidth();
            positionX.setValue(returnValue);
        }
        return returnValue;
    }

    public int getYCoordinate() {
        int returnValue;
        if ((returnValue = Miscellaneous.scaleFrom1080pHeight(positionY.getValue())) < -3) {
            returnValue = -3;
            positionY.setValue(returnValue);
        } else if (returnValue + getHeight() > Miscellaneous.getGameHeight() + 3) {
            returnValue = Miscellaneous.getGameHeight() + 3 - getHeight();
            positionY.setValue(returnValue);
        }
        return returnValue < 0 ? -3 : returnValue;
    }

    public void resetPosition() {
        positionX.reset();
        positionY.reset();
    }

    public String getDisplayText() {
        return "";
    }

    public abstract void drawButton(Minecraft minecraft, int mouseX, int mouseY, int xPosition, int yPosition, int width, int height, boolean isSelectedButton);
}
