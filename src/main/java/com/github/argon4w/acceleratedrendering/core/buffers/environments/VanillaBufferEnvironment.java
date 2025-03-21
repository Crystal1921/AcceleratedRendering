package com.github.argon4w.acceleratedrendering.core.buffers.environments;

import com.github.argon4w.acceleratedrendering.core.gl.buffers.IServerBuffer;
import com.github.argon4w.acceleratedrendering.core.meshes.ServerMesh;
import com.github.argon4w.acceleratedrendering.core.programs.EmptyProgramDispatcher;
import com.github.argon4w.acceleratedrendering.core.programs.IPolygonProgramDispatcher;
import com.github.argon4w.acceleratedrendering.core.programs.culling.ICullingProgramSelector;
import com.github.argon4w.acceleratedrendering.core.programs.processing.EmptyExtraVertexData;
import com.github.argon4w.acceleratedrendering.core.programs.processing.IExtraVertexData;
import com.github.argon4w.acceleratedrendering.core.programs.transform.ITransformProgramSelector;
import com.github.argon4w.acceleratedrendering.core.programs.transform.TransformProgramDispatcher;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.client.renderer.RenderType;

public class VanillaBufferEnvironment implements IBufferEnvironment {

    private final VertexFormat vertexFormat;

    private final ITransformProgramSelector transformProgramSelector;
    private final ICullingProgramSelector cullingProgramSelector;

    public VanillaBufferEnvironment(VertexFormat vertexFormat) {
        this.vertexFormat = vertexFormat;

        this.transformProgramSelector = ITransformProgramSelector.get(vertexFormat);
        this.cullingProgramSelector = ICullingProgramSelector.get(vertexFormat);
    }

    @Override
    public void setupBufferState() {
        vertexFormat.setupBufferState();
    }

    @Override
    public IExtraVertexData getExtraVertex(VertexFormat.Mode mode) {
        return EmptyExtraVertexData.INSTANCE;
    }

    @Override
    public VertexFormat getActiveFormat() {
        return vertexFormat;
    }

    @Override
    public IServerBuffer getServerMeshBuffer() {
        return ServerMesh.Builder.INSTANCE.getStorageBuffer(vertexFormat);
    }

    @Override
    public TransformProgramDispatcher selectTransformProgramDispatcher() {
        return transformProgramSelector.select();
    }

    @Override
    public IPolygonProgramDispatcher selectCullProgramDispatcher(RenderType renderType) {
        return cullingProgramSelector.select(renderType);
    }

    @Override
    public IPolygonProgramDispatcher selectProcessingProgramDispatcher(VertexFormat.Mode mode) {
        return EmptyProgramDispatcher.INSTANCE;
    }

    @Override
    public RenderType getRenderType(RenderType renderType) {
        return renderType;
    }

    @Override
    public boolean isAccelerated(VertexFormat vertexFormat) {
        return this.vertexFormat == vertexFormat;
    }

    @Override
    public int getOffset(VertexFormatElement element) {
        return vertexFormat.getOffset(element);
    }

    @Override
    public int getSharingFlags(VertexFormat.Mode mode) {
        return cullingProgramSelector.getSharingFlags(mode);
    }

    @Override
    public int getVertexSize() {
        return vertexFormat.getVertexSize();
    }
}
