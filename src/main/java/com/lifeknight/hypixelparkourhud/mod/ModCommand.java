package com.lifeknight.hypixelparkourhud.mod;

import com.lifeknight.hypixelparkourhud.gui.LifeKnightGui;
import com.lifeknight.hypixelparkourhud.gui.ManipulableGui;
import com.lifeknight.hypixelparkourhud.gui.components.LifeKnightButton;
import com.lifeknight.hypixelparkourhud.variables.LifeKnightVariable;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;

import java.util.Collections;
import java.util.List;

import static com.lifeknight.hypixelparkourhud.mod.Mod.*;

public class ModCommand extends CommandBase {
    private final List<String> aliases = Collections.singletonList("hph");

    public String getCommandName() {
        return modId;
    }

    public String getCommandUsage(ICommandSender iCommandSender) {
        return modId;
    }

    public boolean canCommandSenderUseCommand(ICommandSender arg0) {
        return true;
    }

    public List<String> getCommandAliases() {
        return aliases;
    }

    public boolean isUsernameIndex(String[] arguments, int argument1) {
        return false;
    }

    public int compareTo(ICommand o) {
        return 0;
    }

    public void processCommand(ICommandSender iCommandSender, String[] arguments) throws CommandException {
        openGui(new LifeKnightGui("[" + modVersion + "] " + modName, LifeKnightVariable.getVariables(), Collections.singletonList(
                new LifeKnightButton("Edit HUD") {
                    @Override
                    public void work() {
                        openGui(new ManipulableGui());
                    }
                })));
    }

}
