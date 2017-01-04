package heb.pay.util;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;

public class FileUtil {
	

	public static String readResourceAsString(String filePath) {
		InputStream in = null;
		try {
			in = FileUtil.class.getClassLoader().getResourceAsStream(filePath);
			return IOUtils.toString(in, "UTF-8");
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

}
