package com.lifeknight.hypixelparkourhud.gui;

import com.lifeknight.hypixelparkourhud.gui.components.LifeKnightButton;
import com.lifeknight.hypixelparkourhud.gui.components.LifeKnightTextField;
import com.lifeknight.hypixelparkourhud.gui.components.ManipulableButton;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;
import java.util.ArrayList;

import static com.lifeknight.hypixelparkourhud.gui.Manipulable.manipulableComponents;

public class ManipulableGui extends GuiScreen {
    public static final ArrayList<ManipulableButton> manipulableButtons = new ArrayList<>();
    public static final ArrayList<LifeKnightTextField> lifeKnightTextFields = new ArrayList<>();

    @Override
    public void initGui() {
        manipulableButtons.clear();
        for (Manipulable manipulable : manipulableComponents) {
            manipulableButtons.add(new ManipulableButton(manipulable));
        }
        super.buttonList.addAll(manipulableButtons);

        for (Manipulable manipulable : manipulableComponents) {
            for (Object component : manipulable.connectedComponents) {
                if (component instanceof GuiButton) {
                    super.buttonList.add((GuiButton) component);
                } else if (component instanceof LifeKnightTextField) {
                    lifeKnightTextFields.add((LifeKnightTextField) component);
                }
            }
        }

        super.buttonList.add(new LifeKnightButton(super.buttonList.size(), super.width / 2 - 50, super.height - 30, 100, 20, "Reset") {
            @Override
            public void work() {
                for (GuiButton guiButton : ManipulableGui.super.buttonList) {
                    if (guiButton instanceof ManipulableButton) {
                        ((ManipulableButton) guiButton).resetPosition();
                    }
                }
            }
        });
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        for (LifeKnightTextField lifeKnightTextField : lifeKnightTextFields) {
            lifeKnightTextField.drawTextBoxAndName();
            lifeKnightTextField.drawStringBelowBox();
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button instanceof LifeKnightButton) {
            ((LifeKnightButton) button).work();
        }
    }
}
