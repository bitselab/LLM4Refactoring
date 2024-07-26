package org.eclipse.jgit.internal.storage.file;

import static org.eclipse.jgit.internal.storage.file.PackReverseIndex.MAGIC;
import static org.eclipse.jgit.internal.storage.file.PackReverseIndex.VERSION_1;
import static org.eclipse.jgit.internal.storage.file.PackReverseIndexV1.OID_VERSION_SHA1;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.eclipse.jgit.transport.PackedObjectInfo;
import org.eclipse.jgit.util.IntList;
import org.eclipse.jgit.util.IntList.IntComparator;

/**
 * Writes reverse index files following the version 1 format.
 * <p>
 * The file format is specified at
 * https://git-scm.com/docs/pack-format#_pack_rev_files_have_the_format.
 */
final class PackReverseIndexWriterV1 extends PackReverseIndexWriter {
    private static final int OID_VERSION_SHA1 = 1;
    private static final int DEFAULT_OID_VERSION = OID_VERSION_SHA1;

    PackReverseIndexWriterV1(final OutputStream dst) {
        super(dst);
    }

    @Override
    protected void writeHeader() throws IOException {
        out.write(MAGIC);
        dataOutput.writeInt(VERSION_1);
        dataOutput.writeInt(DEFAULT_OID_VERSION);
    }

    @Override
    protected void writeBody(List<? extends PackedObjectInfo> objectsByIndexPos)
            throws IOException {
        IntList positionsByOffset = IntList.filledWithRange(0,
                objectsByIndexPos.size());
        positionsByOffset
                .sort(new IndexPositionsByOffsetComparator(objectsByIndexPos));

        for (int i = 0; i < positionsByOffset.size(); i++) {
            int indexPosition = positionsByOffset.get(i);
            dataOutput.writeInt(indexPosition);
        }
    }

    private static class IndexPositionsByOffsetComparator
            implements IntComparator {
        private List<? extends PackedObjectInfo> objectsByIndexPos;

        private IndexPositionsByOffsetComparator(
                List<? extends PackedObjectInfo> objectsByIndexPos) {
            this.objectsByIndexPos = objectsByIndexPos;
        }

        @Override
        public int compare(int firstIndexPosition, int secondIndexPosition) {
            return Long.compare(getOffset(firstIndexPosition),
                    getOffset(secondIndexPosition));
        }

        private long getOffset(int indexPosition) {
            return objectsByIndexPos.get(indexPosition).getOffset();
        }
    }
}