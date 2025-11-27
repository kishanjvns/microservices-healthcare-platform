from abc import ABC, abstractmethod
from typing import Type, TypeVar

S = TypeVar('S')
D = TypeVar('D')

class ModelMapperStrategy(ABC):
    @abstractmethod
    def convert(self, source: S, destination: Type[D]) -> D:
        pass

class DefaultModelMapperStrategy(ModelMapperStrategy):
    def convert(self, source: S, destination: Type[D]) -> D:
        # if source is a pydantic v1 model, then use .dict()
        if hasattr(source, 'dict') :
            source_dict = source.dict()
        elif hasattr(source, 'model_dump'):
            source_dict = source.model_dump()
        elif isinstance(source, dict):
            source_dict = source
        else:
            raise ValueError("Source model must be a Pydantic model ")
        
        if destination is dict:
            return source_dict  
        return destination(**source_dict)  
