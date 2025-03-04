package com.github.argon4w.acceleratedrendering.compat.iris.environments;

import com.github.argon4w.acceleratedrendering.compat.iris.IrisRenderType;
import com.github.argon4w.acceleratedrendering.core.buffers.environments.IBufferEnvironment;
import com.github.argon4w.acceleratedrendering.core.gl.buffers.IServerBuffer;
import com.github.argon4w.acceleratedrendering.core.meshes.ServerMesh;
import com.github.argon4w.acceleratedrendering.core.programs.IPolygonProgramDispatcher;
import com.github.argon4w.acceleratedrendering.core.programs.culling.ICullingProgramSelector;
import com.github.argon4w.acceleratedrendering.core.programs.processing.IPolygonProcessor;
import com.github.argon4w.acceleratedrendering.core.programs.transform.ITransformProgramSelector;
import com.github.argon4w.acceleratedrendering.core.programs.transform.TransformProgramDispatcher;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import net.irisshaders.iris.vertices.ImmediateState;
import net.minecraft.client.renderer.RenderType;

public class IrisBufferEnvironment implements IBufferEnvironment {

    private final IBufferEnvironment vanillaSubSet;
    private final IBufferEnvironment irisSubSet;

    public IrisBufferEnvironment(
            IBufferEnvironment vanillaSubSet,
            VertexFormat vanillaVertexFormat,
            VertexFormat irisVertexFormat
    ) {
        this.vanillaSubSet = vanillaSubSet;
        this.irisSubSet = new IrisSubSet(vanillaVertexFormat, irisVertexFormat);
    }

    private boolean shouldUseIrisSubSet() {
        return WorldRenderingSettings.INSTANCE.shouldUseExtendedVertexFormat()
                && ImmediateState.isRenderingLevel;
    }

    private IBufferEnvironment getSubSet() {
        return shouldUseIrisSubSet()
                ? irisSubSet
                : vanillaSubSet;
    }

    @Override
    public void setupBufferState() {
        getSubSet().setupBufferState();
    }

    @Override
    public void addExtraSharings(long address) {
        getSubSet().addExtraSharings(address);
    }

    @Override
    public void addExtraVertex(long address) {
        getSubSet().addExtraVertex(address);
    }

    @Override
    public VertexFormat getActiveFormat() {
        return getSubSet().getActiveFormat();
    }

    @Override
    public IServerBuffer getServerMeshBuffer() {
        return getSubSet().getServerMeshBuffer();
    }

    @Override
    public TransformProgramDispatcher selectTransformProgramDispatcher() {
        return getSubSet().selectTransformProgramDispatcher();
    }

    @Override
    public IPolygonProgramDispatcher selectCullProgramDispatcher(RenderType renderType) {
        return getSubSet().selectCullProgramDispatcher(renderType);
    }

    @Override
    public IPolygonProgramDispatcher selectProcessingProgramDispatcher(VertexFormat.Mode mode) {
        return getSubSet().selectProcessingProgramDispatcher(mode);
    }

    @Override
    public RenderType getRenderType(RenderType renderType) {
        return getSubSet().getRenderType(renderType);
    }

    @Override
    public boolean isAccelerated(VertexFormat vertexFormat) {
        return getSubSet().isAccelerated(vertexFormat);
    }

    @Override
    public int getSharingFlags() {
        return getSubSet().getSharingFlags();
    }

    @Override
    public int getOffset(VertexFormatElement element) {
        return getSubSet().getOffset(element);
    }

    @Override
    public int getVertexSize() {
        return getSubSet().getVertexSize();
    }

    public static class IrisSubSet implements IBufferEnvironment {

        private final VertexFormat vanillaVertexFormat;
        private final VertexFormat irisVertexFormat;

        private final ITransformProgramSelector transformProgramSelector;
        private final ICullingProgramSelector cullingProgramSelector;
        private final IPolygonProcessor polygonProcessor;

        public IrisSubSet(VertexFormat vanillaVertexFormat, VertexFormat irisVertexFormat) {
            this.vanillaVertexFormat = vanillaVertexFormat;
            this.irisVertexFormat = irisVertexFormat;

            this.transformProgramSelector = ITransformProgramSelector.get(this.irisVertexFormat);
            this.cullingProgramSelector = ICullingProgramSelector.get(this.irisVertexFormat);
            this.polygonProcessor = IPolygonProcessor.get(this.irisVertexFormat);
        }

        @Override
        public void setupBufferState() {
            irisVertexFormat.setupBufferState();
        }

        @Override
        public void addExtraSharings(long address) {
            polygonProcessor.addExtraSharings(address);
        }

        @Override
        public void addExtraVertex(long address) {
            polygonProcessor.addExtraVertex(address);
        }

        @Override
        public boolean isAccelerated(VertexFormat vertexFormat) {
            return this.vanillaVertexFormat == vertexFormat
                    || this.irisVertexFormat == vertexFormat;
        }

        @Override
        public VertexFormat getActiveFormat() {
            return irisVertexFormat;
        }

        @Override
        public IServerBuffer getServerMeshBuffer() {
            return ServerMesh.Builder.INSTANCE.getStorageBuffer(irisVertexFormat);
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
            return polygonProcessor.select(mode);
        }

        @Override
        public RenderType getRenderType(RenderType renderType) {
            return renderType.format == vanillaVertexFormat
                    ? new IrisRenderType(renderType, irisVertexFormat)
                    : new IrisRenderType(renderType, renderType.format);
        }

        @Override
        public int getOffset(VertexFormatElement element) {
            return irisVertexFormat.getOffset(element);
        }

        @Override
        public int getSharingFlags() {
            return cullingProgramSelector.getSharingFlags();
        }

        @Override
        public int getVertexSize() {
            return irisVertexFormat.getVertexSize();
        }
    }
}
