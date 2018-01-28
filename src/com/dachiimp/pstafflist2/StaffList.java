package com.dachiimp.pstafflist2;

import com.dachiimp.pstafflist2.Commands.CMD_Add;
import com.dachiimp.pstafflist2.Commands.CMD_Remove;
import com.dachiimp.pstafflist2.Commands.CMD_SaveReload;
import com.dachiimp.pstafflist2.Commands._CommandManager;
import com.dachiimp.pstafflist2.Config.SetupMessages;
import com.dachiimp.pstafflist2.Listeners.JoinEventListener;
import com.dachiimp.pstafflist2.Util.ClassSaving.StaffMember;
import com.dachiimp.pstafflist2.Util.Enum.InventorySize;
import com.dachiimp.pstafflist2.Util.Enum.SavingMethod;
import com.dachiimp.pstafflist2.Util.GeneralMethods;
import com.dachiimp.pstafflist2.Util.Logger;
import com.dachiimp.pstafflist2.Util.MySQL.MySQL;
import com.dachiimp.pstafflist2.Util.MySQL.SQLManager;
import com.dachiimp.pstafflist2.Util.Saving.SavingScheduler;
import com.dachiimp.pstafflist2.Util.Saving.Saving_Database;
import com.dachiimp.pstafflist2.Util.Saving.Saving_FileTree;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by George on 15/01/2017. For SL
 */
public class StaffList extends JavaPlugin {

    public Plugin _plugin = null;
    //<editor-fold desc="<Variables, Classes And Other Shit>">
    public String prefix = "&7[&aStaffList&7]&r";
    public HashMap<String, String> messages = new HashMap<>();

    public SavingMethod savingMethod = SavingMethod.FileTree;

    private boolean databaseErrors = false;

    /*
        Settings
     */
    public SimpleDateFormat set_simpleDateFormat = new SimpleDateFormat("EEE MMMMM d yyyy @ hh:mm aaa");
    public InventorySize set_inventorySize = InventorySize.SLOT54;
    public boolean set_multiplePages;
    public Integer set_nextPosition = 53;
    public Integer set_previousPosition = 45;

    /*
        Commands
     */
    public _CommandManager commandManager;
    public CMD_Add cmd_Add;

    /****
     Class loading
     ****/

    MySQL mySQL;
    SQLManager sqlManager;

    public GeneralMethods generalMethods = new GeneralMethods(this);
    public JoinEventListener joinEventListener = new JoinEventListener(this);
    public Saving_FileTree saving_FileTree = new Saving_FileTree(this);
    public Saving_Database saving_Database = new Saving_Database(this);
    public SavingScheduler savingScheduler = new SavingScheduler(this);
    public SetupMessages setupMessages = new SetupMessages(this);


    /*               index      Staff                                       */
    private HashMap<Integer, StaffMember> staffMemberHashMap = new HashMap<>();
    public List<String> removeFromDatabase = new ArrayList<>();
    public CMD_SaveReload cmd_SaveReload;
    public CMD_Remove cmd_Remove;
    //</editor-fold>


    public void onEnable() {
        new Logger(this);
        _plugin = this;
        Logger.print(ChatColor.RED + "===============================");
        Logger.print(ChatColor.GREEN + "            Enabled");
        Logger.log("       -----------------");
        Logger.log("       Initalizing Config");
        setupConfig();
        Logger.log("      Initializing Settings:");
        Logger.log("  Setting up database settings");
        // Database setup
        setupDatabase();
        //<editor-fold desc="< Setup Database Stuff >">
        if (savingMethod == SavingMethod.MySQL) {
            if (databaseErrors) {
                // handle
                Logger.severe("===============================");
                Logger.severe("           pStaffList");
                Logger.severe("Tried to use database, but errors");
                Logger.severe("were returned. Disabling Plugin");
                Logger.severe("===============================");
                getServer().getPluginManager().disablePlugin(this);
                return;
            } else {
                Logger.log("  Using MySQL for data saving");
                sqlManager.setDB();
            }
        } else {
            Logger.log("    Saving using file tree");
        }
        //</editor-fold>
        Logger.log("      Setting up commands");
        setupCommands();
        Logger.log("    Loading general settings");
        setupGenSettings();
        Logger.log("    Setting up save scheduler");
        savingScheduler.runTaskTimer(this, 12000L, 12000L);
        Logger.log("      Loading messages.yml");
        setupMessages.setupStrings();
        Logger.log("      Registering listeners");
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(joinEventListener, this);


        //Finally
        Logger.log("         Loading data...");
        if(savingMethod == SavingMethod.MySQL) {
            saving_Database.executeLoad();
        } else {
            saving_FileTree.executeLoad();
        }
        Logger.print(ChatColor.RED + "===============================");
    }

    public void onDisable() {
        //Finally
        Logger.log("         Saving data...");
        if(savingMethod == SavingMethod.MySQL) {
            saving_Database.executeSave();
        } else {
            saving_FileTree.executeSave();
        }
        Logger.log("Disabled");

        mySQL = null;
        sqlManager = null;
        commandManager = null;
        generalMethods = null;
        joinEventListener = null;
        saving_FileTree = null;
        saving_Database = null;
        savingScheduler = null;
        setupMessages = null;

        _plugin = null;
    }

    public void setupConfig() {
        setupDataFolder();
        File file = new File(_plugin.getDataFolder(), "config.yml");
        File file2 = new File(_plugin.getDataFolder(), "messages.yml");

        if (!file.exists()) {
            Logger.log("Created config as one didn't exist");
            _plugin.saveDefaultConfig();
        }

        if (!file2.exists()) {
            Logger.log("Created messages.yml as one didn't exist");
            _plugin.saveResource("messages.yml", false);
        }
        reloadConfig();
    }

    private void setupDataFolder() {
        File dir = new File(getDataFolder(), "");
        if (!dir.exists()) {
            boolean d = dir.mkdirs();
            if (d) {
                Logger.log("Created data folder as one didn't exist");
            } else {
                Logger.log("Error creating data folder");
            }
        }
    }

    private void setupDatabase() {
        databaseErrors = false;
        String useMySQL = getConfig().getString("db_useMySQL");
        if (useMySQL != null && useMySQL.equalsIgnoreCase("true")) {
            // using mysql
            savingMethod = SavingMethod.MySQL;
            String host = getConfig().getString("db_Host");
            String port = getConfig().getString("db_Port");
            String database = getConfig().getString("db_Database");
            String username = getConfig().getString("db_Username");
            String password = getConfig().getString("db_Password");
            List<String> checkSettings = Arrays.asList(host, port, database, username, password);
            List<String> errors = new ArrayList<>();
            for (String setting : checkSettings) {
                if (setting == null || setting.length() <= 0) {
                    errors.add("Error with value '" + setting.toString() + "'.");
                }
            }
            if (errors.size() == 0) {
                // no errors
                MySQL mySql = new MySQL(host, port, database, username, password);
                try {
                    Connection connection = mySql.openConnection();
                    if (connection != null && !connection.isClosed()) {
                        Logger.log("Connected to database.");
                    } else {
                        databaseErrors = true;
                        Logger.severe("Error connecting to database.");
                    }
                } catch (SQLException e) {
                    databaseErrors = true;
                    Logger.severe("Error connecting to database. StackTrace:");
                    e.printStackTrace();
                }
            } else {
                databaseErrors = true;
                Logger.severe("Error with database settings. Following errors were thrown: ");
                Logger.severe(StringUtils.join(", ", errors));
            }
        } else {
            databaseErrors = true;
            savingMethod = SavingMethod.FileTree;
        }
    }

    private void setupCommands() {
        commandManager = new _CommandManager(this);
        cmd_Add = new CMD_Add(this);
        cmd_SaveReload = new CMD_SaveReload(this);
        cmd_Remove = new CMD_Remove(this);
        getCommand("staff").setExecutor(commandManager);
    }

    public void setupGenSettings() {
        List<String> genSettings = Arrays.asList("inventorySize", "multiplePages", "dateFormat", "nextPosition", "previousPosition");
        for (String key : genSettings) {
            String str = getConfig().getString("set_" + key);
            if (str != null && str.length() > 0) {
                switch (key.toLowerCase()) {
                    case "inventorysize": {
                        //<editor-fold desc="<Switch Statement>">
                        switch (str.toLowerCase()) {
                            case "9": {
                                set_inventorySize = InventorySize.SLOT9;
                                break;
                            }
                            case "18": {
                                set_inventorySize = InventorySize.SLOT18;
                                break;
                            }
                            case "27": {
                                set_inventorySize = InventorySize.SLOT27;
                                break;
                            }
                            case "36": {
                                set_inventorySize = InventorySize.SLOT36;
                                break;
                            }
                            case "45": {
                                set_inventorySize = InventorySize.SLOT45;
                                break;
                            }
                            case "54": {
                                set_inventorySize = InventorySize.SLOT54;
                                break;
                            }
                            default: {
                                set_inventorySize = InventorySize.SLOT54;
                                Logger.warn("Error parsing set_inventorySize. Setting to default of 54");
                                break;
                            }
                        }
                        break;
                        //</editor-fold>
                    }
                    case "multiplePages": {
                        set_multiplePages = str.equalsIgnoreCase("true");
                        break;
                    }
                    case "dateformat": {
                        set_simpleDateFormat = new SimpleDateFormat(str);
                        break;
                    }
                    case "nextposition": {
                        //<editor-fold desc="<Parsing Int>">
                        try {
                            int i = Integer.parseInt(str);
                            if (i >= 0 && i <= 53) {
                                set_nextPosition = i;
                            } else {
                                Logger.warn("set_nextPosition is > 53 or < 0");
                            }
                        } catch (NumberFormatException e) {
                            Logger.warn("Error parsing int for set_nextPosition");
                        }
                        break;
                        //</editor-fold>
                    }
                    case "previousposition": {
                        //<editor-fold desc="<Parsing Int>">
                        try {
                            int i = Integer.parseInt(str);
                            if (i >= 0 && i <= 53) {
                                set_previousPosition = i;
                            } else {
                                Logger.warn("set_previousPosition is > 53 or < 0");
                            }
                        } catch (NumberFormatException e) {
                            Logger.warn("Error parsing int for set_previousPosition");
                        }
                        break;
                        //</editor-fold>
                    }
                }
            } else {
                Logger.warn("Error getting setting 'set_" + key + "' from config. Setting to default value");
            }
        }
    }

    public HashMap<Integer, StaffMember> getStaffMembersMap() {
        return staffMemberHashMap;
    }

    public boolean addStaffMemberToMap(Integer position, StaffMember staffMember) {
        if (staffMemberHashMap.containsKey(position)) return false;

        staffMemberHashMap.put(position, staffMember);

        return true;
    }

    public boolean removeStaffMemberFromMap(Integer index) {
        if (!staffMemberHashMap.containsKey(index)) return false;

        staffMemberHashMap.remove(index);

        return true;
    }

    @Deprecated
    public List<String> getStaffMembersMapAsStringList(boolean lowerCase) {
        List<String> list = new ArrayList<>();
        HashMap<Integer,StaffMember> map = getStaffMembersMap();
        for(int pos : map.keySet()) {
            StaffMember member = map.get(pos);
            String name = member.getName();
            if(lowerCase)
                name = name.toLowerCase();
            list.add(name);
        }

        return list;
    }

    public StaffMember getStaffMemberFromName(String name) {
        HashMap<Integer, StaffMember> map = getStaffMembersMap();
        for (int pos : map.keySet()) {
            StaffMember member = map.get(pos);
            if (name.equalsIgnoreCase(member.getName()))
                return member;
        }

        return null;
    }

    public int getStaffIndexFromName(String name) {
        HashMap<Integer, StaffMember> map = getStaffMembersMap();
        for (int pos : map.keySet()) {
            StaffMember member = map.get(pos);
            if (name.equalsIgnoreCase(member.getName()))
                return pos;
        }

        return -1;
    }
}
