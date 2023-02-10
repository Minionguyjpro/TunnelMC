package me.THEREALWWEFAN231.tunnelmc.translator.entity.metadata;

import com.nukkitx.protocol.bedrock.data.entity.EntityData;
import com.nukkitx.protocol.bedrock.data.entity.EntityDataMap;
import com.nukkitx.protocol.bedrock.data.entity.EntityFlags;
import it.unimi.dsi.fastutil.Pair;
import lombok.extern.log4j.Log4j2;
import me.THEREALWWEFAN231.tunnelmc.connection.bedrock.BedrockConnection;
import me.THEREALWWEFAN231.tunnelmc.connection.java.FakeJavaConnection;
import me.THEREALWWEFAN231.tunnelmc.translator.TranslatorManager;
import me.THEREALWWEFAN231.tunnelmc.translator.entity.metadata.defaults.*;
import net.minecraft.entity.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
public class EntityMetadataTranslatorManager extends TranslatorManager<EntityMetadataTranslator<?>, Pair<Entity, EntityDataMap>> {
    private final Map<EntityData, EntityMetadataTranslator<?>> translatorsByEntityDataKey = new HashMap<>();
    private final List<EntityMetadataTranslator<EntityFlags>> flagTranslators = new ArrayList<>();

    public EntityMetadataTranslatorManager() {
        this.addTranslator(new NametagMetadataTranslator());
        this.addTranslator(new NametagVisibilityMetadataTranslator());
        this.addTranslator(new HealthMetadataTranslator());
        this.addTranslator(new AirMetadataTranslator());

        // Flags
        this.addTranslator(new SneakingMetadataTranslator());
        // TODO: Climbing and swimming
    }

    @Override
    protected void addTranslator(EntityMetadataTranslator<?> translator) {
        if(!translator.getClass().isAnnotationPresent(EntityDataIdentifier.class)) {
            log.warn("Skipping translator due to not having an annotation: " + translator.getClass().getSimpleName());
            return;
        }

        EntityDataIdentifier identifier = translator.getClass().getAnnotation(EntityDataIdentifier.class);
        if(identifier.value() == EntityData.FLAGS || identifier.value() == EntityData.FLAGS_2) {
            this.flagTranslators.add((EntityMetadataTranslator<EntityFlags>) translator);
            return;
        }

        this.translatorsByEntityDataKey.put(identifier.value(), translator);
    }

    @Override
    public void translateData(Pair<Entity, EntityDataMap> data, BedrockConnection bedrockConnection, FakeJavaConnection connection) {
        for(EntityData entityData : data.second().keySet()) {
            EntityMetadataTranslator<?> translator = this.translatorsByEntityDataKey.getOrDefault(entityData, null);
            if (translator == null) {
                continue;
            }

            try {
                translator.translateType(new EntityMetadataPair<>(data.first(), data.second().ensureAndGet(entityData)), bedrockConnection, connection);
            } catch (Throwable throwable) {
                log.error(throwable);
            }
        }

        if(data.second().getFlags() == null) {
            return;
        }
        for(EntityMetadataTranslator<EntityFlags> translator : this.flagTranslators) {
            try {
                translator.translateType(new EntityMetadataPair<>(data.first(), data.second().getFlags()), bedrockConnection, connection);
            } catch (Throwable throwable) {
                log.error(throwable);
            }
        }
    }
}
