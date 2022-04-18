package ru.clevertec.ecl.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public abstract class AbstractModel implements Serializable {
    private static final long serialVersionUID = 1L;
    private long id;

    public AbstractModel(){}

    public AbstractModel(long id){
        this.id = id;
    }

}
