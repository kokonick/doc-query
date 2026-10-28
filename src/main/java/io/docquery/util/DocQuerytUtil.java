package io.docquery.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public final class DocQuerytUtil {

	private static final Logger logger = LoggerFactory.getLogger(DocQuerytUtil.class);

	private DocQuerytUtil() {
	}

	public static Path createAutoDeletingTempDir(String prefix) throws IOException {
		Path tempDir = Files.createTempDirectory(prefix);

		//System.out.println("create temp dir : " + tempDir);
		logger.info("create temp dir : " + tempDir);

		// 2. Register a shutdown hook to clean up contents and the folder itself
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			try {
				if (Files.exists(tempDir)) {
					Files.walkFileTree(tempDir, new SimpleFileVisitor<Path>() {
						@Override
						public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
							Files.delete(file); // Delete nested files
							return FileVisitResult.CONTINUE;
						}

						@Override
						public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
							Files.delete(dir); // Delete empty nested subdirectories & root temp folder
							return FileVisitResult.CONTINUE;
						}
					});
				}
			} catch (IOException e) {
				logger.error("Failed to delete temporary directory: ",  e);
			}
		}));

		return tempDir;
	}


}
