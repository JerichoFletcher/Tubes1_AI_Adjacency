import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@SuppressWarnings("unchecked")
public class BotProvider {
    private static final Map<String, Class<? extends Bot>> bots = Stream.of(new Object[][]{
            {"Human", null},
            {"Minimax Bot", Bot.class},
            {"Local Search Bot", Bot.class},
            {"Genetic Algorithm Bot", Bot.class}
    }).collect(HashMap::new, (map, val) -> map.put((String)val[0], (Class<? extends Bot>)val[1]), HashMap::putAll);

    public static Bot getBot(String type){
        if(!bots.containsKey(type))throw new IllegalArgumentException(String.format("Unknown key '%s'", type));

        Class<? extends Bot> clazz = bots.get(type);
        if(clazz == null)return null;

        try{
            return clazz.getConstructor().newInstance();
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static Collection<String> getBotKeys(){
        return bots.keySet();
    }
}
