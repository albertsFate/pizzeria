package com.alberts.pizza.persistence.audit;

import com.alberts.pizza.persistence.entity.PizzaEntity;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PreRemove;
import org.springframework.util.SerializationUtils;

public class AuditPizzaListener {
    private PizzaEntity currentValue;
    @PostLoad
    public void postLoad(PizzaEntity entity){
        System.out.println("POST LOAD");
        this.currentValue = SerializationUtils.clone(entity);
    }

    @PostPersist //aplicada a un metodo
    @PostUpdate
    public void onPostPersist(PizzaEntity entity){
        System.out.println("POST PERSIST OR UPDATE");
        System.out.println("OLD VALUE "+ this.currentValue);
        System.out.println("NEW VALUE "+entity.toString());
    }

    @PreRemove//este metodo se ejecutara antes de realizar la ejecución en la bd
    public void onPreDelete(PizzaEntity entity){
        System.out.println(entity.toString());
    }
}
