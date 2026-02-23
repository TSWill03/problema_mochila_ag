package com.wicolly.algoritmosgeneticos.problemamochila.main;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class GeneticAlgorithmKnapsack {
    private final List<Item> items;
    private final int capacity;

    private final int populationSize;
    private final int generations;
    private final double crossoverRate;
    private final double mutationRate;
    private final int elitismCount;
    private final boolean useRepair;

    private final Random rnd;

    public GeneticAlgorithmKnapsack(
            List<Item> items,
            int capacity,
            int populationSize,
            int generations,
            double crossoverRate,
            double mutationRate,
            int elitismCount,
            boolean useRepair,
            long seed
    ) {
        if (items == null || items.isEmpty()) throw new IllegalArgumentException("items vazio");
        if (capacity <= 0) throw new IllegalArgumentException("capacity deve ser > 0");
        if (populationSize < 2) throw new IllegalArgumentException("populationSize deve ser >= 2");
        if (generations < 1) throw new IllegalArgumentException("generations deve ser >= 1");
        if (crossoverRate < 0 || crossoverRate > 1) throw new IllegalArgumentException("crossoverRate inválido");
        if (mutationRate < 0 || mutationRate > 1) throw new IllegalArgumentException("mutationRate inválido");
        if (elitismCount < 0 || elitismCount >= populationSize) throw new IllegalArgumentException("elitismCount inválido");

        this.items = items;
        this.capacity = capacity;

        this.populationSize = populationSize;
        this.generations = generations;
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
        this.elitismCount = elitismCount;
        this.useRepair = useRepair;

        this.rnd = new Random(seed);
    }

    public Individual run() {
        List<Individual> population = initPopulation();
        evaluatePopulation(population);

        Individual globalBest = bestOf(population);

        for (int g = 0; g < generations; g++) {
            List<Individual> next = new ArrayList<>(populationSize);

            // Elitismo: copia os melhores
            population.sort(Comparator.comparingDouble(Individual::getFitness).reversed());
            for (int i = 0; i < elitismCount; i++) {
                next.add(new Individual(population.get(i).getGenesCopy()));
            }

            // Completa a nova população
            while (next.size() < populationSize) {
                Individual p1 = selectParentRoulette(population);
                Individual p2 = selectParentRoulette(population);

                Individual child = p1.crossoverOnePoint(rnd, p2, crossoverRate);
                child.mutate(rnd, mutationRate);

                if (useRepair) {
                    child.repair(rnd, items, capacity);
                }

                child.evaluate(items, capacity);
                next.add(child);
            }

            population = next;
            // Já avaliamos filhos individualmente; mas elitismo copiou sem avaliar, então:
            evaluatePopulation(population);

            Individual bestGen = bestOf(population);
            if (bestGen.getFitness() > globalBest.getFitness()) {
                globalBest = bestGen;
            }

            // Log opcional (pode comentar)
            // System.out.println("Geração " + g + " | Melhor: " + bestGen);
        }

        return globalBest;
    }

    private List<Individual> initPopulation() {
        int n = items.size();
        List<Individual> pop = new ArrayList<>(populationSize);
        for (int i = 0; i < populationSize; i++) {
            Individual ind = new Individual(n);
            ind.randomize(rnd);
            if (useRepair) ind.repair(rnd, items, capacity);
            pop.add(ind);
        }
        return pop;
    }

    private void evaluatePopulation(List<Individual> population) {
        for (Individual ind : population) {
            ind.evaluate(items, capacity);
        }
    }

    private Individual bestOf(List<Individual> population) {
        return population.stream()
                .max(Comparator.comparingDouble(Individual::getFitness))
                .orElseThrow();
    }

    // Seleção por roleta viciada (fitness proporcional)
    private Individual selectParentRoulette(List<Individual> population) {
        double sum = 0.0;
        for (Individual ind : population) sum += ind.getFitness();

        // Segurança: se por algum motivo sum der 0 (não deveria), escolhe aleatório
        if (sum <= 0.0) return population.get(rnd.nextInt(population.size()));

        double r = rnd.nextDouble() * sum;
        double acc = 0.0;
        for (Individual ind : population) {
            acc += ind.getFitness();
            if (acc >= r) return ind;
        }
        // fallback por erro numérico
        return population.get(population.size() - 1);
    }
}
