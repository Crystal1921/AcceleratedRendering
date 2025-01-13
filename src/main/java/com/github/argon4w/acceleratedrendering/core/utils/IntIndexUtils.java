package com.github.argon4w.acceleratedrendering.core.utils;

import com.github.argon4w.acceleratedrendering.core.gl.buffers.IClientBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import org.lwjgl.system.MemoryUtil;

public class IntIndexUtils {

    public static void putIndices(VertexFormat.Mode mode, IClientBuffer buffer, int startIndex, int vertexCount) {
        switch (mode) {
            case QUADS -> putQuadIndices(buffer, startIndex, vertexCount / VertexFormat.Mode.QUADS.primitiveLength);
            case LINES -> putLineIndices(buffer, startIndex, vertexCount / VertexFormat.Mode.LINES.primitiveLength);
            default -> putSequentialIndices(buffer, startIndex, vertexCount);
        }
    }

    public static void putQuadIndices(IClientBuffer buffer, int startIndex, int quadCount) {
        if (quadCount == 0) {
            return;
        }

        long address = buffer.reserve(quadCount * 6L * 4L);

        for (int i = 0; i < quadCount; i++) {
            int index = startIndex + i * 4;
            long offset = address + i * 6L * 4L;

            MemoryUtil.memPutInt(offset + 0 * 4, index + 0);
            MemoryUtil.memPutInt(offset + 1 * 4, index + 1);
            MemoryUtil.memPutInt(offset + 2 * 4, index + 2);
            MemoryUtil.memPutInt(offset + 3 * 4, index + 2);
            MemoryUtil.memPutInt(offset + 4 * 4, index + 3);
            MemoryUtil.memPutInt(offset + 5 * 4, index + 0);
        }
    }

    public static void putLineIndices(IClientBuffer buffer, int startIndex, int lineCount) {
        if (lineCount == 0) {
            return;
        }

        long address = buffer.reserve(lineCount * 6L * 4L);

        for (int i = 0; i <= lineCount; i++) {
            int index = startIndex + i * 3;
            long offset = address + i * 6L * 4L;

            MemoryUtil.memPutInt(offset + 0 * 4, index + 0);
            MemoryUtil.memPutInt(offset + 1 * 4, index + 1);
            MemoryUtil.memPutInt(offset + 2 * 4, index + 2);
            MemoryUtil.memPutInt(offset + 3 * 4, index + 2);
            MemoryUtil.memPutInt(offset + 4 * 4, index + 3);
            MemoryUtil.memPutInt(offset + 5 * 4, index + 1);
        }
    }

    public static void putSequentialIndices(IClientBuffer buffer, int startIndex, int vertexCount) {
        if (vertexCount == 0) {
            return;
        }

        long address = buffer.reserve(vertexCount * 4L);

        for (int i = 0; i < vertexCount; i++) {
            MemoryUtil.memPutInt(address + i * 4L, startIndex + i);
        }
    }
}
