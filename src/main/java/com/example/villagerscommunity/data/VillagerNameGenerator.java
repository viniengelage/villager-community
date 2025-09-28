package com.example.villagerscommunity.data;

import java.util.List;
import java.util.Random;

/**
 * Utilitário para gerar nomes e gêneros aleatórios para villagers.
 * Similar a funções utilitárias em JS.
 */
public class VillagerNameGenerator {
    private static final Random RANDOM = new Random();

    // Listas de nomes, como arrays em JS
    private static final List<String> MALE_NAMES = List.of(
        "João", "Pedro", "Lucas", "Mateus", "Carlos", "Roberto", "Fernando", "Gustavo", "Rafael", "Daniel"
    );

    private static final List<String> FEMALE_NAMES = List.of(
        "Maria", "Ana", "Beatriz", "Carla", "Diana", "Elena", "Fernanda", "Gabriela", "Helena", "Isabela"
    );

    /**
     * Gera um nome e gênero aleatório.
     * Retorna um array-like com nome e gênero.
     */
    public static String[] generateNameAndGender() {
        boolean isMale = RANDOM.nextBoolean();
        String gender = isMale ? "Masculino" : "Feminino";
        String name = isMale ?
            MALE_NAMES.get(RANDOM.nextInt(MALE_NAMES.size())) :
            FEMALE_NAMES.get(RANDOM.nextInt(FEMALE_NAMES.size()));
        return new String[]{name, gender};
    }
}