package org.example.lab1new;

public class ActionMsg {
    ActionType type;
    int id = 0;
    ActionMsg(ActionType type){
        this.type=type;
        this.id=0;
    }
    ActionMsg(ActionType type, int idx){
        this.type=type;
        this.id=idx;
    }
    ActionType getType() {
        return type;
    }
    int getId(){
        return id;
    }

    public void setType(ActionType type) {
        this.type = type;
    }
}
