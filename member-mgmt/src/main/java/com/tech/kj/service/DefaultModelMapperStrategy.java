package com.tech.kj.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultModelMapperStrategy implements ModelMapperStrategy {
    
    @Autowired
    private ModelMapper modelMapper;
    
    @Override
    public <D, S> D convert(S source, Class<D> destinationType) {
        return modelMapper.map(source, destinationType); 
    }
}
