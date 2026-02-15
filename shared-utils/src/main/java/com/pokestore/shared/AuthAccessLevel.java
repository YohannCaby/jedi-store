package com.pokestore.shared;

import java.util.Comparator;

public enum AuthAccessLevel implements Comparator {
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

    @Override
    public int compare(Object o1, Object o2) {
        return ((AuthAccessLevel) o1).getLevel() - ((AuthAccessLevel) o2).getLevel();
    }

    public String getName(){
        return this.name();
    }
}
