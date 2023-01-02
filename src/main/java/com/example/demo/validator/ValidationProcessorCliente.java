package com.example.demo.validator;

import com.example.demo.Pessoa;
import org.springframework.batch.item.ItemProcessor;

public class ValidationProcessorCliente implements ItemProcessor<Pessoa, Pessoa> {
    public Pessoa process(Pessoa employee) throws Exception
    {
        System.out.println("Inserindo "+employee.getId()+" - "+employee.getNome());
        if (employee.getNome().equalsIgnoreCase("LukeMartin")) {

            System.out.println("Gerou exception LukeMartin");
           Thread.sleep(5000);
            throw new Exception();

        }
        return employee;
    }
}
