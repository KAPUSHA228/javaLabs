package org.example.lab1new;

public class ActionMsg {
    ActionType type;
    ActionMsg(ActionType type){
        this.type=type;
    }
    ActionType getType() {
        return type;
    }

    public void setType(ActionType type) {
        this.type = type;
    }
}
