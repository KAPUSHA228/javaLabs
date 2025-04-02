package org.example.lab1new;


import java.util.ArrayList;

public class Model {
    ArrayList<IObserver> allSevers = new ArrayList<>();
    public void event(){
        allSevers.forEach(IObserver::event);
    }
    public void addServers(IObserver c){
        allSevers.add(c);
        event();
    }
}
