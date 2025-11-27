package com.tech.kj.service;

public interface ModelMapperStrategy {
     <D, S> D convert(S source, Class<D> destinationType);
}
