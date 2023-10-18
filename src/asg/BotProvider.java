package asg;

import asg.bot.*;

import java.util.*;
import java.util.stream.Stream;

@SuppressWarnings("unchecked")
public class BotProvider {
    private static final List<String> keys = new ArrayList<>();
    private static final Map<String, Class<? extends BotBase>> bots = Stream.of(new Object[][]{
            /* TODO:
                Remove test bots from final build
             */
            {"Human", null},
            {"Random (test)", BotRandom.class},
            {"Greedy (test)", BotGreedy.class},
            {"Minimax Bot", BotMinimax.class},
            {"Local Search Bot", BotLocal.class},
            {"Genetic Algorithm Bot", BotGeneticAlgorithm.class}
    }).collect(HashMap::new, (map, val) -> {
        map.put((String) val[0], (Class<? extends BotBase>) val[1]);
        keys.add((String) val[0]);
    }, HashMap::putAll);

    public static BotBase getBot(String type) throws Exception {
        if (!bots.containsKey(type)) throw new IllegalArgumentException(String.format("Unknown key '%s'", type));

        Class<? extends BotBase> clazz = bots.get(type);
        if (clazz == null) return null;

        return clazz.getConstructor().newInstance();
    }

    public static Collection<String> getBotKeys() {
        return keys;
    }
}
