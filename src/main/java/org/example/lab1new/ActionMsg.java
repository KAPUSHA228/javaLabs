package org.example.lab1new;

public class ActionMsg {
    ActionType type;
    int id;
    ActionMsg(ActionType type, int id){
        this.type=type;
        this.id=id;
    }
    ActionMsg(ActionType type){
        this.type=type;
    }
    ActionType getType() {
        return type;
    }
    int getID(){
        return id;
    }
    public void setType(ActionType type) {
        this.type = type;
    }
}
