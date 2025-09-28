package com.example.villagerscommunity.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * Classe para armazenar dados customizados do villager.
 */
public class VillagerData {
    public static final Codec<VillagerData> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.STRING.fieldOf("name").forGetter(VillagerData::getName),
            Codec.STRING.fieldOf("gender").forGetter(VillagerData::getGender),
            Codec.BOOL.fieldOf("hasAssignedBed").forGetter(VillagerData::isHasAssignedBed)
        ).apply(instance, VillagerData::new)
    );

    private String name;
    private String gender; // "Masculino" ou "Feminino"
    private boolean hasAssignedBed;

    public VillagerData() {
        this.name = "";
        this.gender = "";
        this.hasAssignedBed = false;
    }

    public VillagerData(String name, String gender, boolean hasAssignedBed) {
        this.name = name;
        this.gender = gender;
        this.hasAssignedBed = hasAssignedBed;
    }

    // Getters e setters
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

    @Override
    public String toString() {
        return "VillagerData{name='" + name + "', gender='" + gender + "', hasAssignedBed=" + hasAssignedBed + "}";
    }
}