package com.example.villagerscommunity.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * Classe para armazenar dados customizados do villager.
 * Contém informações como nome, gênero, cama designada e profissão.
 */
public class VillagerData {
    // Codec para serialização/desserialização dos dados usando Mojang's serialization
    public static final Codec<VillagerData> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            // Campo obrigatório: nome do villager
            Codec.STRING.fieldOf("name").forGetter(VillagerData::getName),
            // Campo obrigatório: gênero do villager ("Masculino" ou "Feminino")
            Codec.STRING.fieldOf("gender").forGetter(VillagerData::getGender),
            // Campo obrigatório: se o villager tem uma cama designada
            Codec.BOOL.fieldOf("hasAssignedBed").forGetter(VillagerData::isHasAssignedBed),
            // Campos opcionais: coordenadas X, Y, Z da cama (padrão 0)
            Codec.INT.optionalFieldOf("bedX", 0).forGetter(VillagerData::getBedX),
            Codec.INT.optionalFieldOf("bedY", 0).forGetter(VillagerData::getBedY),
            Codec.INT.optionalFieldOf("bedZ", 0).forGetter(VillagerData::getBedZ),
            // Campo obrigatório: profissão do villager (convertido de/para string)
            Codec.STRING.xmap(VillagerProfession::fromKey, VillagerProfession::getKey).fieldOf("profession").forGetter(VillagerData::getProfession)
        ).apply(instance, VillagerData::new)
    );

    private String name;
    private String gender; // "Masculino" ou "Feminino"
    private boolean hasAssignedBed; // Se o villager tem uma cama específica atribuída
    private int bedX, bedY, bedZ; // Coordenadas da cama designada
    private VillagerProfession profession; // Profissão atual do villager

    // Construtor padrão (usado quando não há dados salvos)
    public VillagerData() {
        this.name = "";
        this.gender = "";
        this.hasAssignedBed = false;
        this.bedX = 0;
        this.bedY = 0;
        this.bedZ = 0;
        this.profession = VillagerProfession.NONE;
    }

    // Construtor com nome e gênero (sem cama atribuída)
    public VillagerData(String name, String gender, boolean hasAssignedBed) {
        this.name = name;
        this.gender = gender;
        this.hasAssignedBed = hasAssignedBed;
        this.bedX = 0;
        this.bedY = 0;
        this.bedZ = 0;
        this.profession = VillagerProfession.NONE;
    }

    // Construtor com cama atribuída
    public VillagerData(String name, String gender, boolean hasAssignedBed, int bedX, int bedY, int bedZ) {
        this.name = name;
        this.gender = gender;
        this.hasAssignedBed = hasAssignedBed;
        this.bedX = bedX;
        this.bedY = bedY;
        this.bedZ = bedZ;
        this.profession = VillagerProfession.NONE;
    }

    // Construtor completo com profissão
    public VillagerData(String name, String gender, boolean hasAssignedBed, int bedX, int bedY, int bedZ, VillagerProfession profession) {
        this.name = name;
        this.gender = gender;
        this.hasAssignedBed = hasAssignedBed;
        this.bedX = bedX;
        this.bedY = bedY;
        this.bedZ = bedZ;
        this.profession = profession != null ? profession : VillagerProfession.NONE;
    }

    // ===== GETTERS E SETTERS =====

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public boolean isHasAssignedBed() {
        return hasAssignedBed;
    }

    public void setHasAssignedBed(boolean hasAssignedBed) {
        this.hasAssignedBed = hasAssignedBed;
    }

    public int getBedX() {
        return bedX;
    }

    public void setBedX(int bedX) {
        this.bedX = bedX;
    }

    public int getBedY() {
        return bedY;
    }

    public void setBedY(int bedY) {
        this.bedY = bedY;
    }

    public int getBedZ() {
        return bedZ;
    }

    public void setBedZ(int bedZ) {
        this.bedZ = bedZ;
    }

    public VillagerProfession getProfession() {
        return profession;
    }

    public void setProfession(VillagerProfession profession) {
        this.profession = profession != null ? profession : VillagerProfession.NONE;
    }

    // Representação em string para debug/logs
    @Override
    public String toString() {
        return "VillagerData{name='" + name + "', gender='" + gender + "', hasAssignedBed=" + hasAssignedBed +
               ", bedPos=(" + bedX + ", " + bedY + ", " + bedZ + "), profession=" + profession.getDisplayName() + "}";
    }
}