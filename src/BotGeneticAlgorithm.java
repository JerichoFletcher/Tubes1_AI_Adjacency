import java.util.*;

public class BotGeneticAlgorithm extends BotBase {
    private static final int n = 1500;
    private static final int k = 50;
    private static final double mutationRates = 0.033;

    protected List<Individual> generateNewGeneration(Board board){
        //generate generasi: for k individu:, for depth: getallemptysquares --> ambil random dari sini, masukin ke arr aksi,
        List<Individual> generation = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            // generate k individuals
            List<Byte> emptySquares = board.getEmptySquares();
            Individual individual = new Individual(new Byte[board.getPliesLeft()], new Tree<>(new ReservationNode(null, null)));
            for (int j = 0; j < board.getPliesLeft(); j++) {
                // generate random actions (individual)
                int emptySquareIdx = (int) (Math.random() * emptySquares.size());
                individual.setAction(j, emptySquares.get(emptySquareIdx));
                emptySquares.remove(emptySquareIdx);
            }
//            System.out.printf("Individual action is %s\n", String.join(", ", Arrays.stream(individual.actions).map(Object::toString).toArray(String[]::new)));
            generation.add(individual);
        }
        return generation;
    }

    protected void reserve (Tree<ReservationNode> reservationTree, Individual individual){
        Tree<ReservationNode> currentTree = reservationTree;
        for (Byte action : individual.actions) {
            ReservationNode actionNode = new ReservationNode(null, action);
            if (!currentTree.hasChildTValue(actionNode)){
                // if ga ada child actionnya
                currentTree.addChild(new Tree<>(actionNode));
            }
            currentTree = currentTree.getChild(actionNode);
        }
        individual.leaf = currentTree;

    }

    protected Individual crossoverMutate(Board board, Individual parent1, Individual parent2){
        //crossover mutate untuk 2 individu
        /*
         * tentuin titik crossover random
         * crossover
         * hash set semua angka di papan (getemptysquares), terus loop, tiap koordinat remove dari hash set
         * hash map, default -1. loop. assign ke hash map. cek ada angka yang sama apa ngga. kalo sama, randomize dari hash set nya, remove dari hash set
         * mutate: if math random < laju mutasi[0..1]
         * return 1 doang
         * */
        Individual child = new Individual(new Byte[parent1.actions.length], new Tree<>(new ReservationNode(null, null)));

        // crossover
        int crossoverPoint = (int) (Math.random() * parent1.actions.length);
        System.arraycopy(parent1.actions, 0, child.actions, 0, parent1.actions.length);
        System.arraycopy(parent2.actions, crossoverPoint, child.actions, crossoverPoint, parent2.actions.length - crossoverPoint);

        // mutation
        List<Byte> actionOptions = new ArrayList<Byte>(board.getEmptySquares()); // yang mungkin diambil dari randomizer
        // remove semua angka yang ada di child pada actionOptions, jadi ga bakal keambil lagi
        for (Byte action : child.actions ) {
            actionOptions.remove(action);
        }

        // mapping koordinat yang ga bisa dipilih (jadi ga ada yang ngedouble gitu)
        HashMap<Byte, Integer> actionMap = new HashMap<Byte, Integer>();
        for (int i = 0; i < child.actions.length; i++) {
            if (!actionMap.containsKey(child.actions[i])){
                actionMap.put(child.actions[i], i);
            } else {
                int mutationActionIdx = (int) (Math.random() * actionOptions.size());
                child.actions[i] = actionOptions.get(mutationActionIdx);
                actionOptions.remove(mutationActionIdx);
            }
        }

        // mutation swap dua digit actions
        if ((double) (Math.random()) < mutationRates) {
            int mutationIdx1 = (int) (Math.random() * child.actions.length);
            int mutationIdx2 = (int) (Math.random() * child.actions.length);
            Byte temp = child.actions[mutationIdx1];
            child.actions[mutationIdx1] = child.actions[mutationIdx2];
            child.actions[mutationIdx2] = temp;
        }

        return child;
    }

    @Override
    protected byte searchMove(Board board) {
        Tree<ReservationNode> reservationTree = new Tree<>(new ReservationNode(null, null));
        List<Individual> generation = generateNewGeneration(board);
        for (int i = 0; i < n; i++) {
            if (isStopped()){
                break;
            }

            for (Individual individual : generation) {
                reserve(reservationTree, individual);
            }
            /*System.out.printf("=== STARTING ITERATION %s ===\n", i);
            printTree(reservationTree, 0);*/

            // call minimax
            Minimax.evaluateTree(reservationTree, board);

            // generate new generation
            if (i==n-1){
                break;
            }
            // fitness function
            Integer totalFitnessValue = 0;
            for (Individual individual : generation) {
                individual.calcFitnessValue();
                totalFitnessValue += individual.fitnessValue;
            }

            // roulette
            List<Individual> newGeneration = new ArrayList<>();
            for (int j = 0; j < 2*k; j++) {
                //waited random
                int rouletteValue =  (int) (Math.random() * totalFitnessValue);
                for (Individual individual : generation ) {
                    if (rouletteValue <= individual.fitnessValue){
                        if (j % 2 == 0) {
                            // kalo belum kepilih dua parent, simpen dulu
                            newGeneration.add(individual); // simpen parent pertamanya
                        } else {
                            // kalo udah kepilih dua parent, crossover and mutate
                            Individual prevParent = newGeneration.get(newGeneration.size()-1); // ambil parent pertamanya
                            newGeneration.add(crossoverMutate(board, prevParent, individual)); // bangkitkan child hasil crossover & mutate
                            newGeneration.remove(newGeneration.size()-2); // remove prevParent
                        }
                        break;
                    } else {
                        totalFitnessValue -= individual.fitnessValue;
                    }
                }
            }

            generation.clear();
            generation.addAll(newGeneration);
            newGeneration.clear();
        }

        /*System.out.println("=== FINAL TREE ===");
        printTree(reservationTree, 0);*/

        return reservationTree.getChild(
                child -> Objects.equals(child.getValue().evaluationScore, reservationTree.getValue().evaluationScore)
        ).getValue().action;
    }

    /*private void printTree(Tree<ReservationNode> tree, int indent) {
        for (int i = 0; i < indent; i++) System.out.print("  ");
        System.out.printf("%s\n", tree.getValue().action);
        for (Tree<ReservationNode> child : tree.getChildren()) printTree(child, indent + 1);
    }*/
}
