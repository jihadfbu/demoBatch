package com.example.demo.validator;

import com.example.demo.Pessoa;
import org.springframework.batch.item.ItemProcessor;

public class ValidationProcessorFuncionario implements ItemProcessor<Pessoa, Pessoa> {
    public Pessoa process(Pessoa employee) throws Exception
    {
        System.out.println("Inserindo "+employee.getId()+" - "+employee.getNome());
        if (employee.getNome().equalsIgnoreCase("LeroyWaterson")) {

            System.out.println("Gerou exception LeroyWaterson");
           Thread.sleep(5000);
            throw new Exception();

        }
        return employee;
    }
}
