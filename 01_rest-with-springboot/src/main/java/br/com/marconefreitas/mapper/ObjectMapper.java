package br.com.marconefreitas.mapper;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;

import java.util.ArrayList;
import java.util.List;

public class ObjectMapper {

    private static final Mapper mapper = DozerBeanMapperBuilder.buildDefault();

    public static <Orig, Dest> Dest parseObject(Orig origin, Class<Dest> destination){
        return mapper.map(origin, destination);
    }

    public static <Orig, Dest> List<Dest> parseListObjects(List<Orig> origin,
                                                           Class<Dest> destination){
        List<Dest> dests = new ArrayList<Dest>();
        for(Object o: origin){
         dests.add(mapper.map(o, destination));
        }
        return dests;
    }
}
