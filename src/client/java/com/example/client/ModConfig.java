package com.example.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ModConfig {
    public boolean hideNonGroupNicks = false;
    public boolean prioritizeServerPlayersInTab = false;
    public boolean onlyGroupsInLocator = false;
    
    @Deprecated public boolean hideNonGroupInTab = false;
    public boolean keepTeamPrefixes = true;
    public List<GroupStyle> groups = new ArrayList<>();

    @Deprecated public NicknameStyle ownStyle;
    @Deprecated public Map<String, NicknameStyle> friendStyles;

    public static class GroupStyle {
        public String name = "Group";
        public List<String> players = new ArrayList<>();
        public NicknameStyle style = new NicknameStyle();
    }
}
