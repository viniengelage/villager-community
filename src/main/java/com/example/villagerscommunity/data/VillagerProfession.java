package com.example.villagerscommunity.data;

/**
 * Enum representando todas as profissões padrão dos villagers do Minecraft.
 * Cada profissão tem uma chave interna (key) e um nome de exibição em português.
 */
public enum VillagerProfession {
    NONE("none", "Sem Profissão"),
    ARMORER("armorer", "Armoreiro"),
    BUTCHER("butcher", "Açougueiro"),
    CARTOGRAPHER("cartographer", "Cartógrafo"),
    CLERIC("cleric", "Clérigo"),
    FARMER("farmer", "Fazendeiro"),
    FISHERMAN("fisherman", "Pescador"),
    FLETCHER("fletcher", "Flecheiro"),
    LEATHERWORKER("leatherworker", "Coureiro"),
    LIBRARIAN("librarian", "Bibliotecário"),
    MASON("mason", "Pedreiro"),
    NITWIT("nitwit", "Tolo"),
    SHEPHERD("shepherd", "Pastor"),
    TOOLSMITH("toolsmith", "Ferreiro de Ferramentas"),
    WEAPONSMITH("weaponsmith", "Ferreiro de Armas");

    private final String key;
    private final String displayName;

    VillagerProfession(String key, String displayName) {
        this.key = key;
        this.displayName = displayName;
    }

    public String getKey() {
        return key;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Obtém uma profissão pela chave (usado para desserialização)
     * @param key a chave da profissão (ex: "farmer", "butcher")
     * @return a profissão correspondente, ou NONE se não encontrar
     */
    public static VillagerProfession fromKey(String key) {
        for (VillagerProfession profession : values()) {
            if (profession.key.equals(key)) {
                return profession;
            }
        }
        return NONE; // Default fallback
    }

    @Override
    public String toString() {
        return displayName;
    }
}