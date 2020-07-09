package com.lifeknight.hypixelparkourhud.mod;

import com.lifeknight.hypixelparkourhud.gui.LifeKnightGui;
import com.lifeknight.hypixelparkourhud.gui.ManipulableGui;
import com.lifeknight.hypixelparkourhud.gui.components.LifeKnightButton;
import com.lifeknight.hypixelparkourhud.utilities.Chat;
import com.lifeknight.hypixelparkourhud.utilities.Text;
import com.lifeknight.hypixelparkourhud.variables.LifeKnightVariable;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.lifeknight.hypixelparkourhud.mod.Mod.*;
import static net.minecraft.util.EnumChatFormatting.DARK_GREEN;

public class ModCommand extends CommandBase {
    private final List<String> aliases = Collections.singletonList("mb");
    private final String[] mainCommands = {};

    public String getCommandName() {
        return modId;
    }

    public String getCommandUsage(ICommandSender iCommandSender) {
        return modId;
    }

    public List<String> addTabCompletionOptions(ICommandSender iCommandSender, String[] arguments, BlockPos blockPosition) {

        if (arguments.length > 0) {
            return Text.returnStartingEntries(mainCommands, arguments[0], true);
        }

        return Arrays.asList(mainCommands);
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

    public void addMainCommandMessage() {
        StringBuilder result = new StringBuilder(DARK_GREEN + "/" + modId);

        for (String command : mainCommands) {
            result.append(" ").append(command).append(",");
        }

        Chat.addChatMessage(result.substring(0, result.length() - 1));
    }
}
