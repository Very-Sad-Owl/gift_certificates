package ru.clevertec.ecl.dto;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Min;
import java.io.Serializable;

@Data
@SuperBuilder
public abstract class AbstractModel {
    private long id;

    public AbstractModel(){}

    public AbstractModel(long id){
        this.id = id;
    }

}
