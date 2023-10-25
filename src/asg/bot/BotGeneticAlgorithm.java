package asg.bot;

import asg.algorithm.Minimax;
import asg.struct.ActionNode;
import asg.struct.Board;
import asg.struct.Individual;
import asg.struct.Tree;

import java.util.*;

public class BotGeneticAlgorithm extends BotBase {
    private static final int n = 1500;
    private static final int k = 50;
    private static final double mutationRates = 0.033;

    /**
     * Memulai melakukan algoritma mutasi genetik dengan menciptakan k individu baru.
     * @param board Keadaan papan saat ini, yang digunakan sebagai titik mula pencarian.
     * @return generation Kumpulan individu yang telah dibangkitkan.
     */
    protected List<Individual> generateNewGeneration(Board board){
        // Membuat array generasi yang akan diisi kumpulan individu
        List<Individual> generation = new ArrayList<>();

        // Membuat k individu, tiap individu memiliki kedalaman sebanyak pilihan jumlah ronde/ play.
        for (int i = 0; i < k; i++) {
            List<Byte> emptySquares = board.getEmptySquares();

            int depth = Math.min(board.getPliesLeft(), 8);
            Individual individual = new Individual(new Byte[depth], new Tree<>(new ActionNode()));

            // Mengacak kotak yang masih tersedia di papan untuk diisikan pada tiap kedalaman individu yang sedang dibangkitkan.
            for (int j = 0; j < depth; j++) {
                int emptySquareIdx = (int) (Math.random() * emptySquares.size());
                individual.setAction(j, emptySquares.get(emptySquareIdx));
                emptySquares.remove(emptySquareIdx);
            }
//            System.out.printf("Individual action is %s\n", String.join(", ", Arrays.stream(individual.actions).map(Object::toString).toArray(String[]::new)));
            generation.add(individual);
        }

        // Mengembalikan generasi yang telah dibangkitkan
        return generation;
    }

    /**
     * Menyimpan aksi individu ke dalam bentuk pohon.
     * @param reservationTree Pohon yang akan menyimpan aksi-aksi dari invidu,
     * @param individual Invidu yang akan disimpan aksinya dalam pohon.
     */
    protected void reserve (Tree<ActionNode> reservationTree, Individual individual){
        Tree<ActionNode> currentTree = reservationTree;
        for (Byte action : individual.actions) {
            ActionNode actionNode = new ActionNode(null, action);
            if (!currentTree.hasChildTValue(actionNode)){
                currentTree.addChild(new Tree<>(actionNode));
            }
            currentTree = currentTree.getChild(actionNode);
        }
        individual.leaf = currentTree;

    }

    /**
     * Melakukan penyilangan dan mutasi pada 2 individu.
     * @param parent1 Individu parent pertama yang akan dimutasi
     * @param parent2 Individu parent kedua yang akan dimutasi
     * @return child Individu anakan dari hasil penyilangan dan mutasi
     */
    protected Individual crossoverMutate(Board board, Individual parent1, Individual parent2){
        Individual child = new Individual(new Byte[parent1.actions.length], new Tree<>(new ActionNode()));

        /* Crossover */
        // Menentukan titik crossover acak di antara tindakan orang tua
        int crossoverPoint = (int) (Math.random() * parent1.actions.length);
        // Salin tindakan dari parent1 hingga titik crossover ke anak
        System.arraycopy(parent1.actions, 0, child.actions, 0, parent1.actions.length);
        // Salin tindakan dari parent2 mulai dari titik crossover ke anak
        System.arraycopy(parent2.actions, crossoverPoint, child.actions, crossoverPoint, parent2.actions.length - crossoverPoint);

        /* Mutation */
        // Mengumpulkan semua aksi potensial yang masih dapat dilakukan bot dari list empty square
        List<Byte> actionOptions = new ArrayList<Byte>(board.getEmptySquares());
        // Menghapus semua tindakan yang sudah ada di anak dari daftar potensi tindakan
        for (Byte action : child.actions ) {
            actionOptions.remove(action);
        }

        // Memetakan semua tindakan yang sudah dipilih oleh child sehingga tidak ada tindakan yang double
        HashMap<Byte, Integer> actionMap = new HashMap<Byte, Integer>();
        for (int i = 0; i < child.actions.length; i++) {
            if (!actionMap.containsKey(child.actions[i])){
                actionMap.put(child.actions[i], i);
            } else {
                // Jika ada tindakan yang sama, maka mutasi dengan memilih tindakan baru dari actionOptions
                int mutationActionIdx = (int) (Math.random() * actionOptions.size());
                child.actions[i] = actionOptions.get(mutationActionIdx);
                actionOptions.remove(mutationActionIdx);
            }
        }

        // Mutasi dengan menukar swap jika memenuhi laju mutasi yang ditentukan
        if ((double) (Math.random()) < mutationRates) {
            int mutationIdx1 = (int) (Math.random() * child.actions.length);
            int mutationIdx2 = (int) (Math.random() * child.actions.length);
            Byte temp = child.actions[mutationIdx1];
            child.actions[mutationIdx1] = child.actions[mutationIdx2];
            child.actions[mutationIdx2] = temp;
        }

        // Mengembalikan individu anak yang telah dibuat
        return child;
    }
    /**
     * Mencari aksi paling optimal yang akan dilakukah bot dengan menerapkan Genetic Algorithm.
     * @param board Kondisi papan permainan terkini.
     * @return choosenChild Individu anak yang telah dipilih sebagai pemilik aksi paling optimal berdasarkan Genetic Algorithm.
     */
    @Override
    protected byte searchMove(Board board) {
        // Inisiasi pohon untuk penyimpanan individu dari Genetic Algorithm
        Tree<ActionNode> reservationTree = new Tree<>(new ActionNode());

        // Membuat generasi awal individu dengan fungsi generateNewGeneration
        List<Individual> generation = generateNewGeneration(board);

        // Melakukan iterasi pencarian individu dengan aksi paling optimal
        for (int i = 0; i < n; i++) {
            // Memeriksa apakah sudah didapatkan individu paling optimal
            // Apabila sudah, iterasi dihentikan
            if (isStopped()){
                break;
            }

            // Menyimpan aksi individu yang akan dicek dalam reservation tree
            for (Individual individual : generation) {
                reserve(reservationTree, individual);
            }

            // Menghitung fitness function tiap individu dengan menggunakan fungsi evaluation tree
            Minimax.evaluateTree(reservationTree, board);

            if (i==n-1){
                break;
            }
            // Menghitung total fitness function satu generasi
            Integer totalFitnessValue = 0;
            for (Individual individual : generation) {
                individual.calcFitnessValue();
                totalFitnessValue += individual.fitnessValue;
            }

            // Mmebangkitkan generasi baru dari generasi yang lama
            List<Individual> newGeneration = new ArrayList<>();
            for (int j = 0; j < 2*k; j++) {
                // Mengacak nilai untuk pemilihan parent
                int rouletteValue =  (int) (Math.random() * totalFitnessValue);

                // Memilih parent, melakukan crossover dan mutasi, dan membentuk generasi baru
                for (Individual individual : generation ) {
                    if (rouletteValue <= individual.fitnessValue){
                        if (j % 2 == 0) {
                            // Menyimpan parent pertama
                            newGeneration.add(individual);
                        } else {
                            // Mengambil parent pertama
                            Individual prevParent = newGeneration.get(newGeneration.size()-1);
                            // Membangkitkan child dengan crossover dan mutasi
                            newGeneration.add(crossoverMutate(board, prevParent, individual));
                            // Menghapus parent dari list
                            newGeneration.remove(newGeneration.size()-2);
                        }
                        break;
                    } else {
                        totalFitnessValue -= individual.fitnessValue;
                    }
                }
            }

            generation.clear();
            // Menambahkan hasil anakan baru ke dalam list generasi total
            generation.addAll(newGeneration);
            newGeneration.clear();
        }

        // Mengembalikan child dengan nilai paling optimal
        return reservationTree.getChild(
                child -> Objects.equals(child.getValue().evaluationScore, reservationTree.getValue().evaluationScore)
        ).getValue().action;
    }

}
