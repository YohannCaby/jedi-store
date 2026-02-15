package com.pokestore.shared;

import java.util.Comparator;

public enum AuthAccessLevel implements Comparator<AuthAccessLevel> {
    ALL(0),
    USER(10),
    ADMIN(100),
    SUPER_ADMIN(Integer.MAX_VALUE);
    final int accessLevel;
    AuthAccessLevel(int accessLevel) {
        this.accessLevel = accessLevel;
    }

    public int getLevel(){
        return this.accessLevel;
    }

    public static boolean isValid(String value){
        for(AuthAccessLevel o : AuthAccessLevel.values()) {
            if (value.toUpperCase().equals(o.name())) {
                return true;
            }
        }
        return false;
    }

    public String getName(){
        return this.name();
    }

    @Override
    public int compare(AuthAccessLevel o1, AuthAccessLevel o2) {
        return o1.getLevel() - o2.getLevel();
    }

}
