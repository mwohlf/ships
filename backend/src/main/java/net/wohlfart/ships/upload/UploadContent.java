package net.wohlfart.ships.upload;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public class UploadContent {

    private final String name;

    private final ByteBuf buffer;

    public UploadContent(@NonNull String name, ByteBuf buffer) {
        this.name = name;
        this.buffer = buffer;
    }

    public static UploadContent create(MultipartFile multipartFile) throws IOException {
        String name = multipartFile.getOriginalFilename();
        Assert.notNull(name, "unknown filename");
        long size = multipartFile.getSize();
        Assert.isTrue(size < Integer.MAX_VALUE, "filesize too large");
        final ByteBuf buffer = Unpooled.directBuffer((int) size);
        buffer.writeBytes(multipartFile.getInputStream(), (int) size);
        return new UploadContent(name, buffer);
    }

    public Reader newReader() {
        return new InputStreamReader(new ByteBufInputStream(buffer.asReadOnly().resetReaderIndex()));
    }

    public boolean containsColumns(String... requiredColumns) {
        byte[] bytes = new byte[Math.min(buffer.readableBytes(), 1024)];
        buffer.getBytes(buffer.readerIndex(), bytes);
        String headerLine = new String(bytes).split("\\R")[0];
        for (String columnHeader : requiredColumns) {
            if (!headerLine.contains(columnHeader)) {
                return false;
            }
        }
        return true;
    }

    public boolean fileNameMatches(String filename) {
        return this.name.toLowerCase().endsWith(filename.toLowerCase());
    }

    @Override
    public String toString() {
        return "UploadContent '" + this.name + "' ";
    }

    public boolean hasJsonPostfix() {
        return this.name.toLowerCase().endsWith(".json");
    }
}
