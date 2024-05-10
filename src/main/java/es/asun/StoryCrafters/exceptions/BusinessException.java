package es.asun.StoryCrafters.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusinessException extends Exception{

    private String message;

    public BusinessException(){
        super();
    }

    public BusinessException(final String message){
        this.message = message;
    }

}
