package com.lifeknight.hypixelparkourhud.gui.components;

import com.lifeknight.hypixelparkourhud.gui.Manipulable;
import com.lifeknight.hypixelparkourhud.gui.ManipulableGui;
import com.lifeknight.hypixelparkourhud.utilities.Miscellaneous;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;

public class ManipulableButton extends GuiButton {
    private final Manipulable manipulable;
    private boolean isSelectedButton = false;
    private boolean dragging = false;
    private int originalXPosition;
    private int originalYPosition;
    private int originalMouseXPosition;
    private int originalMouseYPosition;

    public ManipulableButton(Manipulable manipulable) {
        super(Manipulable.manipulableComponents.indexOf(manipulable),
                manipulable.getXCoordinate(),
                manipulable.getYCoordinate(),
                manipulable.getWidth(),
                manipulable.getHeight(),
                manipulable.getDisplayText());
        this.manipulable = manipulable;

        if (this.xPosition < -3) {
            this.xPosition = 0;
        }

        if (this.xPosition + this.width > Miscellaneous.getGameWidth() + 1) {
            this.xPosition = Miscellaneous.getGameWidth() - this.width;
        }

        if (this.yPosition < -3) {
            this.yPosition = 0;
        }

        if (this.yPosition + this.height > Miscellaneous.getGameHeight() + 1) {
            this.yPosition = Miscellaneous.getGameHeight() - this.height;
        }

        if (!canTranslateToX(this.xPosition)) {
            this.xPosition = 0;
            this.yPosition = 0;
            while (!canTranslateToX(this.xPosition)) {
                if (!(this.xPosition + this.width > Miscellaneous.getGameWidth() + 1)) {
                    this.xPosition++;
                } else if (!(this.yPosition + this.height >= Miscellaneous.getGameHeight())) {
                    this.yPosition++;
                } else {
                    break;
                }
            }
        }

        if (!canTranslateToY(this.yPosition)) {
            this.xPosition = 0;
            this.yPosition = 0;
            while (!canTranslateToY(this.yPosition)) {
                if (!(this.yPosition + this.height >= Miscellaneous.getGameHeight())) {
                    this.yPosition++;
                } else if (!(this.xPosition + this.width > Miscellaneous.getGameWidth() + 1)) {
                    this.xPosition++;
                } else {
                    break;
                }
            }
        }
        mouseReleased(0, 0);
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        this.displayString = manipulable.getDisplayText();
        if (this.visible) {
            drawEmptyBox(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, isSelectedButton ? 0xeaff0000 : 0xffffffff);
            manipulable.drawButton(mc, mouseX, mouseY, this.xPosition, this.yPosition, this.width, this.height, isSelectedButton);
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            this.mouseDragged(mc, mouseX, mouseY);
        }
    }

    @Override
    public boolean mousePressed(Minecraft par1Minecraft, int mouseX, int mouseY) {
        if (super.mousePressed(par1Minecraft, mouseX, mouseY)) {
            boolean oneOfExtrasClicked = false;
            for (ManipulableButton manipulableButton : ManipulableGui.manipulableButtons) {
                if (manipulableButton != this) {
                    for (Object component : manipulableButton.manipulable.connectedComponents) {
                        if (component instanceof GuiButton) {
                            if (((GuiButton) component).mousePressed(par1Minecraft, mouseX, mouseY)) {
                                oneOfExtrasClicked = true;
                                break;
                            }
                        }
                    }
                }
            }
            if (!oneOfExtrasClicked) {
                isSelectedButton = true;
                dragging = true;
                originalMouseXPosition = mouseX;
                originalMouseYPosition = mouseY;
                originalXPosition = this.xPosition;
                originalYPosition = this.yPosition;
                return true;
            }
        } else if (isSelectedButton) {
            boolean oneOfExtrasClicked = false;
            for (ManipulableButton manipulableButton : ManipulableGui.manipulableButtons) {
                if (manipulableButton == this) {
                    for (Object component : manipulableButton.manipulable.connectedComponents) {
                        if (component instanceof GuiButton) {
                            if (((GuiButton) component).mousePressed(par1Minecraft, mouseX, mouseY)) {
                                oneOfExtrasClicked = true;
                                break;
                            }
                        }
                    }
                    break;
                }
            }
            if (!oneOfExtrasClicked) {
                isSelectedButton = false;
            }
        }
        return false;
    }

    @Override
    public void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
        if (super.visible && this.dragging) {
            int newXPosition = originalXPosition + mouseX - originalMouseXPosition;
            int newYPosition = originalYPosition + mouseY - originalMouseYPosition;
            if (newXPosition != this.xPosition) {
                int toAddX = newXPosition > this.xPosition ? -1 : 1;
                while (newXPosition < 0 || newXPosition + this.width > Miscellaneous.getGameWidth() + 1 || !canTranslateToX(newXPosition)) {
                    if (newXPosition < 0) {
                        newXPosition++;
                    } else if (newXPosition + this.width > Miscellaneous.getGameWidth() + 1) {
                        newXPosition--;
                    } else if (newXPosition + this.width + toAddX > Miscellaneous.getGameWidth() + 1 && !(newXPosition + toAddX < 0)) {
                        newXPosition += toAddX;
                    } else {
                        break;
                    }
                }
                this.xPosition = newXPosition;
            }

            if (newYPosition != this.yPosition) {
                int toAddY = newYPosition > this.yPosition ? -1 : 1;
                while ((newYPosition < 0) || (newYPosition + this.height > Miscellaneous.getGameHeight() + 1) || !canTranslateToY(newYPosition)) {
                    if (newYPosition < 0) {
                        newYPosition++;
                    } else if (newYPosition + this.height > Miscellaneous.getGameHeight() + 1) {
                        newYPosition--;
                    } else if (!(newYPosition + this.height + toAddY > Miscellaneous.getGameHeight() + 1) && !(newYPosition + toAddY < 0)) {
                        newYPosition += toAddY;
                    } else {
                        break;
                    }
                }
                this.yPosition = newYPosition;
            }
        }
    }

    private boolean canTranslateToX(int newXPosition) {
        for (ManipulableButton manipulableButton : ManipulableGui.manipulableButtons) {
            if (manipulableButton != this) {
                if (((this.yPosition >= manipulableButton.yPosition && this.yPosition <= manipulableButton.yPosition + manipulableButton.height) ||
                        (this.yPosition + this.height >= manipulableButton.yPosition && this.yPosition + this.height <= manipulableButton.yPosition + manipulableButton.height)) &&
                        !((this.xPosition >= manipulableButton.xPosition && this.xPosition <= manipulableButton.xPosition + manipulableButton.width) ||
                                (this.xPosition + this.width >= manipulableButton.xPosition && this.xPosition + this.width <= manipulableButton.xPosition + manipulableButton.width))) {
                    if ((newXPosition >= manipulableButton.xPosition && newXPosition <= manipulableButton.xPosition + manipulableButton.width) || (newXPosition <= manipulableButton.xPosition && newXPosition + this.width >= manipulableButton.xPosition)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean canTranslateToY(int newYPosition) {
        for (ManipulableButton manipulableButton : ManipulableGui.manipulableButtons) {
            if (manipulableButton != this) {
                if ((this.xPosition >= manipulableButton.xPosition && this.xPosition <= manipulableButton.xPosition + manipulableButton.width) ||
                        (this.xPosition + this.width >= manipulableButton.xPosition && this.xPosition + this.width <= manipulableButton.xPosition + manipulableButton.width) ||
                        (manipulableButton.xPosition >= this.xPosition && manipulableButton.xPosition <= this.xPosition + this.width) ||
                        (manipulableButton.xPosition + manipulableButton.width >= this.xPosition && manipulableButton.xPosition + manipulableButton.width <= this.xPosition + this.width) && !((this.yPosition >= manipulableButton.yPosition && this.yPosition <= manipulableButton.yPosition + manipulableButton.height) ||
                                (this.yPosition + this.height >= manipulableButton.yPosition && this.yPosition + this.height <= manipulableButton.yPosition + manipulableButton.height))) {
                    if ((newYPosition >= manipulableButton.yPosition && newYPosition <= manipulableButton.yPosition + manipulableButton.height) || (newYPosition <= manipulableButton.yPosition && newYPosition + this.height >= manipulableButton.yPosition)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        dragging = false;
        manipulable.updatePosition(this.xPosition, this.yPosition);
    }

    @Override
    public void playPressSound(SoundHandler soundHandlerIn) {}

    public void drawEmptyBox(int left, int top, int right, int bottom, int color) {
        drawHorizontalLine(left, right, top, color);
        drawHorizontalLine(left, right, bottom, color);

        drawVerticalLine(left, top, bottom, color);
        drawVerticalLine(right, top, bottom, color);
    }

    public void resetPosition() {
        manipulable.resetPosition();
        this.xPosition = manipulable.getXCoordinate();
        this.yPosition = manipulable.getYCoordinate();

        if (this.xPosition < 0) {
            this.xPosition = 0;
        }

        if (this.xPosition + this.width > Miscellaneous.getGameWidth() + 1) {
            this.xPosition = Miscellaneous.getGameWidth() - this.width;
        }

        if (this.yPosition < 0) {
            this.yPosition = 0;
        }

        if (this.yPosition + this.height > Miscellaneous.getGameHeight() + 1) {
            this.yPosition = Miscellaneous.getGameHeight() - this.height;
        }
        mouseReleased(0, 0);
    }
}
