package com.dachiimp.pstafflist2.Commands;

import com.dachiimp.pstafflist2.StaffList;
import com.dachiimp.pstafflist2.Util.Logger;
import com.dachiimp.pstafflist2.Util.MySQL.MySQL;
import com.dachiimp.pstafflist2.Util.MySQL.SQLManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Created by George on 15/01/2017.
 */
public class _CommandManager implements CommandExecutor {

    private StaffList staffList;

    public _CommandManager(StaffList staffList) {
        this.staffList = staffList;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (cmd.getName().equalsIgnoreCase("staff")) {
            Logger.log("staff");
            if (args.length == 0) {
                if (!(sender instanceof Player)) return true;

                Player player = (Player) sender;


            } else {
                // args > 0
                String subCmd = args[0];
                switch (subCmd.toLowerCase()) {
                    case "add": {
                        staffList.cmd_Add.execute(sender, args);
                        break;
                    }
                    case "remove": {
                        staffList.cmd_Remove.execute(sender, args);
                        break;
                    }
                    case "save": {
                        staffList.cmd_SaveReload.execute(sender, args);
                        break;
                    }
                    case "reload": {
                        staffList.cmd_SaveReload.execute(sender, args);
                        break;
                    }

                    default: {
                        staffList.generalMethods.message(sender, "unknownCommand");
                        break;
                    }
                }
            }
            return true;
        }

        return false;
    }

    void createPlayer(Player player) {
        try {
            Connection connection = MySQL.openConnection();
            if (connection == null || connection.isClosed()) {
                Logger.warn("Error opening connection to create player " + player.getName());
                player.sendMessage("Error creating you, please contact an admin.");
                return;
            }
            final String query = "INSERT INTO `staff` (`Player`, `uuid`, `position`, `Rank`) VALUES (?,?,?,?)";
            HashMap<Integer, Object> values = new HashMap<>();
            values.put(1, player.getName());
            values.put(2, player.getUniqueId().toString());
            values.put(3, 0);
            values.put(4, "Biggest Dig Owner");
            boolean executed = SQLManager.update(query, values,true);
            if (executed) {
                player.sendMessage("Created you");
            } else {
                player.sendMessage("Error creating you, please contact an admin");
            }
        } catch (SQLException e) {
            player.sendMessage("Error creating you, please contact an admin.");
            Logger.warn("Error creating player " + player.getName() + ". Stacktrace: ");
            e.printStackTrace();
        }
    }

}
