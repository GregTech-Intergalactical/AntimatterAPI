package net.minecraftforge.common.data;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.resources.AssetIndex;
import net.minecraft.client.resources.DefaultClientPackResources;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.*;
import net.minecraft.server.packs.repository.ServerPacksSource;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.mixin.client.ClientPackSourceAccessor;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class ExistingFileHelper {
    private final MultiPackResourceManager clientResources;
    private final MultiPackResourceManager serverData;
    private final boolean enable;
    private final Multimap<PackType, ResourceLocation> generated = HashMultimap.create();

    public ExistingFileHelper(Collection<Path> existingPacks, Set<String> existingMods, boolean enable, @Nullable String assetIndex, @Nullable File assetsDir) {
        List<PackResources> candidateClientResources = new ArrayList();
        List<PackResources> candidateServerResources = new ArrayList();
        candidateClientResources.add(new VanillaPackResources(ClientPackSourceAccessor.getBUILT_IN(), new String[]{"minecraft", "realms"}));
        if (assetIndex != null && assetsDir != null) {
            candidateClientResources.add(new DefaultClientPackResources(ClientPackSourceAccessor.getBUILT_IN(), new AssetIndex(assetsDir, assetIndex)));
        }

        candidateServerResources.add(new VanillaPackResources(ServerPacksSource.BUILT_IN_METADATA, new String[]{"minecraft"}));
        Iterator var8 = existingPacks.iterator();

        for (Path existing : existingPacks) {
            File file = existing.toFile();
            PackResources pack = file.isDirectory() ? new FolderPackResources(file) : new FilePackResources(file);
            candidateClientResources.add(pack);
            candidateServerResources.add(pack);
        }

        var8 = existingMods.iterator();

        //TODO fix this
        /*for (String existingMod : existingMods){
            IModFileInfo modFileInfo = ModList.get().getModFileById(existingMod);
            if (modFileInfo != null) {
                PackResources pack = ResourcePackLoader.createPackForMod(modFileInfo);
                candidateClientResources.add(pack);
                candidateServerResources.add(pack);
            }
        }*/

        this.clientResources = new MultiPackResourceManager(PackType.CLIENT_RESOURCES, candidateClientResources);
        this.serverData = new MultiPackResourceManager(PackType.SERVER_DATA, candidateServerResources);
        this.enable = enable;
    }

    private ResourceManager getManager(PackType packType) {
        return packType == PackType.CLIENT_RESOURCES ? this.clientResources : this.serverData;
    }

    private ResourceLocation getLocation(ResourceLocation base, String suffix, String prefix) {
        return new ResourceLocation(base.getNamespace(), prefix + "/" + base.getPath() + suffix);
    }

    public boolean exists(ResourceLocation loc, PackType packType) {
        if (!this.enable) {
            return true;
        } else {
            return this.generated.get(packType).contains(loc) || this.getManager(packType).hasResource(loc);
        }
    }

    public boolean exists(ResourceLocation loc, IResourceType type) {
        return this.exists(this.getLocation(loc, type.getSuffix(), type.getPrefix()), type.getPackType());
    }

    public boolean exists(ResourceLocation loc, PackType packType, String pathSuffix, String pathPrefix) {
        return this.exists(this.getLocation(loc, pathSuffix, pathPrefix), packType);
    }

    public void trackGenerated(ResourceLocation loc, IResourceType type) {
        this.generated.put(type.getPackType(), this.getLocation(loc, type.getSuffix(), type.getPrefix()));
    }

    public void trackGenerated(ResourceLocation loc, PackType packType, String pathSuffix, String pathPrefix) {
        this.generated.put(packType, this.getLocation(loc, pathSuffix, pathPrefix));
    }

    @VisibleForTesting
    public Resource getResource(ResourceLocation loc, PackType packType, String pathSuffix, String pathPrefix) throws IOException {
        return this.getResource(this.getLocation(loc, pathSuffix, pathPrefix), packType);
    }

    @VisibleForTesting
    public Resource getResource(ResourceLocation loc, PackType packType) throws IOException {
        return this.getManager(packType).getResource(loc);
    }

    public boolean isEnabled() {
        return this.enable;
    }

    public interface IResourceType {
        PackType getPackType();

        String getSuffix();

        String getPrefix();
    }

    public static class ResourceType implements IResourceType {
        final PackType packType;
        final String suffix;
        final String prefix;

        public ResourceType(PackType type, String suffix, String prefix) {
            this.packType = type;
            this.suffix = suffix;
            this.prefix = prefix;
        }

        public PackType getPackType() {
            return this.packType;
        }

        public String getSuffix() {
            return this.suffix;
        }

        public String getPrefix() {
            return this.prefix;
        }
    }
}
