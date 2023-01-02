package com.example.demo.validator;

import com.example.demo.Fornecedor;
import org.springframework.batch.item.ItemProcessor;

public class ValidationProcessorFornecedor implements ItemProcessor<Fornecedor, Fornecedor> {
    public Fornecedor process(Fornecedor employee) throws Exception
    {
        System.out.println("Inserindo "+employee.getId()+" - "+employee.getNome());
        if (employee.getId()==7) {

            System.out.println("Gerou exception 7");
            Thread.sleep(5000);
            throw new Exception();

        }
        return employee;
    }
}
